package com.bot.projects.repository;

import com.bot.projects.entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Projects, Integer> {
    @Query(value = "select * from project order by ProjectId desc limit 1", nativeQuery = true)
    Projects getLastProjectRecordId();
}
