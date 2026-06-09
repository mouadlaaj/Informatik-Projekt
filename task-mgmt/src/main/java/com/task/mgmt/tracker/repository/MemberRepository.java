package com.task.mgmt.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.task.mgmt.tracker.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

	Member findByRole(String role);

	Member findByEmailId(String emailId);

	Member findByIdAndRole(String user, String role);
	
	Member findByIdAndRoleIn(String user, List<String> roles);
	
	Member findByIdAndStatus(String memberId,String status);

	Member findByIdAndStatusAndRoleIn(String id, String status, List<String> roles);

	List<Member> findAllByIdIn(List<String> list);

	List<Member> findAllByIdInAndStatus(List<String> teamMembers, String name);
	
	List<Member> findAllByIdInAndStatusAndRoleIn(List<String> teamMembers, String name, List<String> roles);
	
	List<Member> findByFirstNameContainingOrLastNameContainingOrIdContaining(String firstName, String lastName, String memberId);

	List<Member> findAllByStatusAndRoleInOrderByCreatedDateDesc(String status, List<String> role);
	
	List<Member> findAllByStatusIn(List<String> status);

	List<Member> findAllByStatusOrderByCreatedDateDesc(String name);
	
	Member findByIdAndStatusAndRole(String memberId,String status, String role);

	List<Member> findAllByRoleAndStatus(String role,String status);

	List<Member> findAllByStatus(String status);

	Member findByEmailIdAndStatus(String userName, String name);

	List<Member> findAllByStatusAndRoleOrderByCreatedDateDesc(String name, String name2);


}
