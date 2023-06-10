package com.bot.projects.service;

import com.bot.projects.entity.ProjectAppraisal;
import com.bot.projects.model.CurrentSession;
import com.bot.projects.model.DbParameters;
import com.bot.projects.repository.LowLevelExecution;
import com.bot.projects.repository.ProjectAppraisalRepository;
import com.bot.projects.serviceinterface.IProjectAppraisalService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectAppraisalService implements IProjectAppraisalService {
    @Autowired
    LowLevelExecution lowLevelExecution;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ProjectAppraisalRepository projectAppraisalRepository;
    @Autowired
    CurrentSession currentSession;
    public List<ProjectAppraisal> getProjectAppraisalService(int projectId) throws Exception {
        if (projectId == 0)
            throw new Exception("Invalid project. Please select a valid project");

        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_ProjectId", projectId, Types.BIGINT));
        var dataSet = lowLevelExecution.executeProcedure("sp_project_appraisal_get_by_project", dbParameters);
        return objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<ProjectAppraisal>>() {});
    }

    @Override
    public List<ProjectAppraisal> addProjectAppraisalService(ProjectAppraisal projectAppraisal) throws Exception {
        validateProjectAppraisal(projectAppraisal);
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());
        var lastProjectAppraisal = projectAppraisalRepository.getLastProjectAppraisal();
        if (lastProjectAppraisal == null)
            projectAppraisal.setProjectAppraisalId(1);
        else
            projectAppraisal.setProjectAppraisalId(lastProjectAppraisal.getProjectAppraisalId());

        projectAppraisal.setCreatedOn(date);
        projectAppraisal.setCreatedBy(currentSession.getUserDetail().getUserId());
        projectAppraisalRepository.save(projectAppraisal);
        return  this.getProjectAppraisalService(projectAppraisal.getProjectId());
    }

    public List<ProjectAppraisal> updateProjectAppraisalService(int projectAppraisalId, ProjectAppraisal projectAppraisal) throws Exception {
        if (projectAppraisalId == 0)
            throw new Exception("Invalid project appraisal selected");

        validateProjectAppraisal(projectAppraisal);
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());
        var existProjectAppraisalData = projectAppraisalRepository.findById(projectAppraisalId);
        if (existProjectAppraisalData.isEmpty())
            throw new Exception("Project appraisal not found. Please contact to admin");

        var existProjectAppraisal = existProjectAppraisalData.get();
        existProjectAppraisal.setFromDate(projectAppraisal.getFromDate());
        existProjectAppraisal.setToDate(projectAppraisal.getToDate());
        existProjectAppraisal.setProjectAppraisalBudget(projectAppraisal.getProjectAppraisalBudget());
        existProjectAppraisal.setUpdatedOn(date);
        existProjectAppraisal.setUpdatedBy(currentSession.getUserDetail().getUserId());
        projectAppraisalRepository.save(existProjectAppraisal);
        return  this.getProjectAppraisalService(projectAppraisal.getProjectId());
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
