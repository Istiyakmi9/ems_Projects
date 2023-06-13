package com.bot.projects.repository;

import com.bot.projects.entity.ProjectMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMembers, Integer> {
    @Query(value = "select * from project_members_detail order by ProjectMemberDetailId desc limit 1", nativeQuery = true)
    ProjectMembers getLastProjectMembersRecordId();
}
