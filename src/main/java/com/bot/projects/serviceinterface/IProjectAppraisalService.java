package com.bot.projects.serviceinterface;

import com.bot.projects.entity.ProjectAppraisal;

import java.util.List;

public interface IProjectAppraisalService {
    List<ProjectAppraisal> getProjectAppraisalService(int projectId) throws Exception;
    List<ProjectAppraisal> addProjectAppraisalService(ProjectAppraisal projectAppraisal) throws Exception;
    List<ProjectAppraisal> updateProjectAppraisalService(int projectAppraisalId, ProjectAppraisal projectAppraisal) throws Exception;
}
