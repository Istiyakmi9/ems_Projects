package com.bot.projects.service;

import com.bot.projects.db.service.DbManager;
import com.bot.projects.entity.ProjectAppraisal;
import com.bot.projects.model.CurrentSession;
import com.bot.projects.repository.ProjectAppraisalRepository;
import com.bot.projects.serviceinterface.IProjectAppraisalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectAppraisalService implements IProjectAppraisalService {
    @Autowired
    CurrentSession currentSession;
    @Autowired
    DbManager dbManager;
    @Autowired
    ProjectAppraisalRepository projectAppraisalRepository;

    public List<ProjectAppraisal> getProjectAppraisalService(int projectId) throws Exception {
        if (projectId == 0)
            throw new Exception("Invalid project. Please select a valid project");

        return projectAppraisalRepository.getProjectAppraisalRepository(projectId);
    }

    @Override
    public List<ProjectAppraisal> addProjectAppraisalService(ProjectAppraisal projectAppraisal) throws Exception {
        validateProjectAppraisal(projectAppraisal);
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());
        projectAppraisal.setProjectAppraisalId(dbManager.nextIntPrimaryKey(ProjectAppraisal.class));

        projectAppraisal.setCreatedOn(date);
        projectAppraisal.setCreatedBy(currentSession.getUserId());
        dbManager.save(projectAppraisal);
        return this.getProjectAppraisalService(projectAppraisal.getProjectId());
    }

    public List<ProjectAppraisal> updateProjectAppraisalService(int projectAppraisalId, ProjectAppraisal projectAppraisal) throws Exception {
        if (projectAppraisalId == 0)
            throw new Exception("Invalid project appraisal selected");

        validateProjectAppraisal(projectAppraisal);
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());
        var existProjectAppraisal = dbManager.getById(projectAppraisalId, ProjectAppraisal.class);
        existProjectAppraisal.setFromDate(projectAppraisal.getFromDate());
        existProjectAppraisal.setToDate(projectAppraisal.getToDate());
        existProjectAppraisal.setProjectAppraisalBudget(projectAppraisal.getProjectAppraisalBudget());
        existProjectAppraisal.setUpdatedOn(date);
        existProjectAppraisal.setUpdatedBy(currentSession.getUserId());
        dbManager.save(existProjectAppraisal);
        return this.getProjectAppraisalService(projectAppraisal.getProjectId());
    }

    private void validateProjectAppraisal(ProjectAppraisal projectAppraisal) throws Exception {
        if (projectAppraisal.getProjectId() == 0)
            throw new Exception("Invalid project selected");

        if (projectAppraisal.getProjectAppraisalBudget() == 0)
            throw new Exception("Invalid project appraisal budget");

        if (projectAppraisal.getFromDate() == null)
            throw new Exception("Invalid from date selected");

        if (projectAppraisal.getToDate() == null)
            throw new Exception("Invalid to date selected");
    }
}
