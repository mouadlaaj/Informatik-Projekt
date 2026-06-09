package com.task.mgmt.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {

	List<Team> findAllByOrderByCreatedDateDesc();

	List<Team> findAllByIdIn(List<String> teams);

	List<Team> findAllByIdInAndStatus(List<String> list, String status);

	List<Team> findAllByIdInAndMembersIdIn(List<String> teamId, List<String> teamMembersId);

	Team findByIdAndMembersIdInAndMembersStatus(String teamId, List<String> membersId, String status);

	Team findByIdAndStatus(String teamId, String status);

	Team findByTeamLeadIdId(String teamLeadId);

	List<Team> findAllById(String teamId);

	List<Team> findAllByStatusAndMembersId(String status, String memberId);

	List<Team> findAllByMembersId(String memberId);

	List<Team> findAllByTeamLeadIdId(String teamLeadId);

	Team findByMembersId(String memberId);

	Team findFirstByTeamLeadIdId(String teamLeadId);

	Team findByIdAndTeamLeadIdIdAndStatus(String searchTeamId, String teamLeadId, String status);

	List<Team> findAllByStatus(String status);

	@Query("SELECT t FROM Team t JOIN t.members m WHERE m.id = :memberId")
	List<Team> findTeamsByMemberId(@Param("memberId") String memberId);

	@Query("SELECT DISTINCT m FROM Team t JOIN t.members m WHERE t.teamLeadId.id = :teamLeadId")
	List<Member> findDistinctMembersByTeamLeadId(@Param("teamLeadId") String teamLeadId);

	@Query("SELECT DISTINCT m FROM Team t JOIN t.members m")
	List<Member> findDistinctMembers();

	Team findByIdAndTeamLeadIdIdAndMembersIdAndStatus(String searchTeamId, String teamLeadId, String teamMemberId,
			String status);

	Team findByIdAndMembersIdAndStatus(String searchTeamId, String teamMemberId, String status);

}
