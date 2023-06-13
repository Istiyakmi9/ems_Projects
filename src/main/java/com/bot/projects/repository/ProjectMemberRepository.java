package com.bot.projects.repository;

import com.bot.projects.entity.ProjectMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMembers, Integer> {
    @Query(value = "select * from project_members_detail order by ProjectMemberDetailId desc limit 1", nativeQuery = true)
    ProjectMembers getLastProjectMembersRecordId();

//    @Query("select p from ProjectMembers p where p.projectId = :projectId")
//    List<ProjectMembers> getProjectMemberByProjectId(@Param("projectId") int projectId);

    @Query(value = "Call sp_project_member_getby_projectid(:_ProjectId)", nativeQuery = true)
    List<ProjectMembers> getProjectMemberByProjectId(@Param("_ProjectId") int _ProjectId);
}
