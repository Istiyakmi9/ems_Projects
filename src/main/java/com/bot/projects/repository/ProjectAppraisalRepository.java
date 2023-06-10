package com.bot.projects.repository;

import com.bot.projects.entity.ProjectAppraisal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface ProjectAppraisalRepository extends JpaRepository<ProjectAppraisal, Integer> {
    @Query(nativeQuery = true, value = "select p.* from project_appraisal p order by p.ProjectAppraisalId desc limit 1")
    ProjectAppraisal getLastProjectAppraisal();
}
