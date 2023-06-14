package com.bot.projects.serviceinterface;

import com.bot.projects.entity.ProjectMembers;
import com.bot.projects.entity.Projects;
import com.bot.projects.model.ProjectDetail;

import java.util.List;
import java.util.Map;

public interface IProjectService {
    Map<String, Object> getMembersDetailService(Long employeeId) throws Exception;

    List<ProjectDetail> getProjectService(Long managerId) throws Exception;
    Projects addProjectService(Projects project) throws Exception;
    Projects updateProjectService(int projectId, Projects projects) throws Exception;
    Map<String, Object> getProjectDetailService(int projectId) throws Exception;
    Map<String, List<ProjectMembers>> addProjectMembersService(List<ProjectMembers> projectMembers, int projectId) throws Exception;
    Map<String, List<ProjectMembers>> updateProjectMembersService(List<ProjectMembers> projectMembers, int projectId) throws Exception;
}
