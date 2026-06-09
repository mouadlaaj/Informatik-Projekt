package com.task.mgmt.tracker.config;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class TokenValidationService {

    private static final Logger logger = LoggerFactory.getLogger(TokenValidationService.class);

    @Value("${keycloak.userinfo-uri}")
    private String introspectionUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public IntrospectionResponse validateAndExtractUser(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("token", token);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

            ResponseEntity<IntrospectionResponse> response = restTemplate.postForEntity(
                introspectionUri, request, IntrospectionResponse.class);

            IntrospectionResponse body = response.getBody();
            
            if (body != null && body.isActive()) {
                logger.debug("Token validation successful for user: {}", body.getUsername());
                return body;
            } else {
                logger.warn("Token validation failed - token not active");
            }
        } catch (Exception e) {
            logger.error("Error validating token with Keycloak: {}", e.getMessage(), e);
        }
        return null;
    }

    public static class IntrospectionResponse {
        private boolean active;
        private String username;
        private String client_id;
        private Long exp;
        private Long iat;
        private String sub;
        private String scope;

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getClient_id() { return client_id; }
        public void setClient_id(String client_id) { this.client_id = client_id; }
        
        public Long getExp() { return exp; }
        public void setExp(Long exp) { this.exp = exp; }
        
        public Long getIat() { return iat; }
        public void setIat(Long iat) { this.iat = iat; }
        
        public String getSub() { return sub; }
        public void setSub(String sub) { this.sub = sub; }
        
        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }
    }
}