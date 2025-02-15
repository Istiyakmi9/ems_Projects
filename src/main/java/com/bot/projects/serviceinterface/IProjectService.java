package com.bot.projects.serviceinterface;

import com.bot.projects.entity.ProjectMembers;
import com.bot.projects.entity.Projects;
import com.bot.projects.model.FilterModel;
import com.bot.projects.model.ProjectAttachment;
import com.bot.projects.model.ProjectDetail;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IProjectService {
    Map<String, Object> getMembersDetailService(Long employeeId, int projectId) throws Exception;
    List<ProjectDetail> getProjectService(Long managerId) throws Exception;
    String addProjectDetailService(String Data, MultipartFile[] attachment, MultipartFile thumbnail) throws Exception;
    Map<String, Object> getProjectDetailService(int projectId) throws Exception;
    List<ProjectMembers> getProjectMembersService(int projectId) throws Exception;
    Map<String, Object> getProjectMemberByFilterService(FilterModel filterModel) throws Exception;
    List<ProjectAttachment> deleteProjectAttachmentService(int projectId, ProjectAttachment projectAttachment) throws Exception;
    List<ProjectAttachment> addProjectAttachmentService(int projectId, MultipartFile[] attachment) throws Exception;
    List<ProjectMembers> addProjectMembersService(int projectId, List<ProjectMembers> projectMembers) throws Exception;
    Projects updateProjectDetailService(String Data, MultipartFile thumbnail) throws Exception;
    String updateProjectDescriptionService(int projectId, String description) throws Exception;
    List<String> manageProjectTeamService(String teamName, int projectId) throws Exception;
    List<ProjectMembers> deleteProjectMemberService(int projectId, int projectMemberDetailId) throws Exception;
    List<ProjectMembers> updateDesignationAndTeamService(int projectId, ProjectMembers projectMembers) throws Exception;
    List<ProjectAttachment> updateAttachmentNameService(int projectId, ProjectAttachment projectAttachment) throws Exception;
}
