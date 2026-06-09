package com.task.mgmt.tracker.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.mgmt.tracker.exception.AppException;

import jakarta.ws.rs.core.Response;

@Service
public class KeycloakService {

	@Value("${keycloak.server-url}")
	private String serverUrl;

	@Value("${keycloak.realm}")
	private String realm;

	@Value("${keycloak.username}")
	private String adminUsername;

	@Value("${keycloak.password}")
	private String adminPassword;

	@Value("${keycloak.client-id-admin}")
	private String clientId;

	public void createUser(String memberId, String email, String firstName, String lastName, String password,
			String role) {
		try {
			Keycloak keycloak = KeycloakBuilder.builder().serverUrl(serverUrl).realm("master").username(adminUsername)
					.password(adminPassword).clientId(clientId).grantType(OAuth2Constants.PASSWORD).build();

			UserRepresentation user = new UserRepresentation();
			user.setUsername(memberId);
			user.setEmail(email);
			user.setEnabled(true);
			if (firstName != null) {
				user.setFirstName(firstName);
			}
			if (lastName != null) {
				user.setLastName(lastName);
			}
			user.setEmailVerified(true);

			Response response = keycloak.realm(realm).users().create(user);

			if (response.getStatus() != 201) {
				throw new AppException("Failed to create user in Keycloak");
			}

			String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

			CredentialRepresentation passwordCred = new CredentialRepresentation();
			passwordCred.setTemporary(false);
			passwordCred.setType(CredentialRepresentation.PASSWORD);
			passwordCred.setValue(password);

			keycloak.realm(realm).users().get(userId).resetPassword(passwordCred);

			keycloak.realm(realm).users().get(userId).roles().realmLevel()
					.add(Collections.singletonList(keycloak.realm(realm).roles().get(role).toRepresentation()));

		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Error while creating Keycloak user: " + e.getMessage());
		}
	}

	private String getAdminToken() {
		String tokenUrl = serverUrl + "/realms/master/protocol/openid-connect/token";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("grant_type", "password");
		form.add("client_id", clientId);
		form.add("username", adminUsername);
		form.add("password", adminPassword);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

		try {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

			Map<String, Object> responseBody = response.getBody();
			if (responseBody == null || !responseBody.containsKey("access_token")) {
				throw new AppException("Invalid access token received from Keycloak");
			}

			return (String) responseBody.get("access_token");

		} catch (HttpClientErrorException e) {
			throw new AppException(
					"Failed to fetch token from Keycloak: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
		}
	}

	public boolean keycloakUserExists(String email) {
		try {
			String token = getAdminToken();

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(token);
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Void> entity = new HttpEntity<>(headers);
			String url = serverUrl + "/admin/realms/" + realm + "/users?email=" + email;

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

			ObjectMapper objectMapper = new ObjectMapper();
			List<?> users = objectMapper.readValue(response.getBody(), new TypeReference<List<?>>() {
			});

			return users != null && !users.isEmpty();

		} catch (Exception e) {
			throw new AppException("Error while checking if user exists in Keycloak: " + e.getMessage());
		}
	}
}
