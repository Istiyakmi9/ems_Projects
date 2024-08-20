package com.bot.projects.serviceinterface;

import com.bot.projects.entity.ProjectMembers;
import com.bot.projects.entity.Projects;
import com.bot.projects.model.FilterModel;
import com.bot.projects.model.ProjectDetail;

import java.util.List;
import java.util.Map;

public interface IProjectService {
    Map<String, Object> getMembersDetailService(Long employeeId, int projectId) throws Exception;
    List<ProjectDetail> getProjectService(Long managerId) throws Exception;
    Map<String, Object> addUpdateProjectService(int projectId, Projects projects) throws Exception;
    Map<String, Object> getProjectDetailService(int projectId) throws Exception;
    List<ProjectMembers> getProjectMembersService(int projectId) throws Exception;
    Map<String, Object> getProjectMemberByFilterService(FilterModel filterModel) throws Exception;
}
