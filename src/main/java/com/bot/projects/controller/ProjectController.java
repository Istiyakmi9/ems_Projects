package com.bot.projects.controller;

import com.bot.projects.entity.ProjectMembers;
import com.bot.projects.model.ApiResponse;
import com.bot.projects.model.FilterModel;
import com.bot.projects.model.ProjectAttachment;
import com.bot.projects.serviceinterface.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/ps/projects/")
public class ProjectController {
    @Autowired
    IProjectService iProjectService;
    @RequestMapping(value = "memberdetail/{employeeId}/{projectId}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse> getMembersDetail(@PathVariable long employeeId,
                                                        @PathVariable int projectId) throws Exception {
        var result = iProjectService.getMembersDetailService(employeeId, projectId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "get/{managerId}", method = RequestMethod.GET)
    public ResponseEntity<?> getProjects(@PathVariable("managerId") long managerId) throws Exception {
        var result = iProjectService.getProjectService(managerId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "addProjectDetail", method = RequestMethod.POST)
    public ResponseEntity<?> addUpdateProject(@RequestParam(value="attachment", required = false) MultipartFile[] attachment,
                                              @RequestParam("thumbnail")MultipartFile thumbnail,
                                              @RequestParam("data") String data) throws Exception {
        var result = iProjectService.addProjectDetailService(data, attachment, thumbnail);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "getProjectDetail/{projectId}", method = RequestMethod.GET)
    public ResponseEntity<?> getProjectDetail(@PathVariable int projectId) throws Exception {
        var result = iProjectService.getProjectDetailService(projectId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "getProjectMemberDetail/{projectId}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse> getProjectMemberDetail(@PathVariable int projectId) throws Exception {
        var result = iProjectService.getProjectMembersService(projectId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "getProjectMemberByFilter", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse> getProjectMemberByFilter(@RequestBody FilterModel filterModel) throws Exception {
        var result = iProjectService.getProjectMemberByFilterService(filterModel);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "deleteProjectAttachment/{projectId}", method = RequestMethod.PUT)
    public ResponseEntity<ApiResponse> deleteProjectAttachment(@PathVariable int projectId,
                                                               @RequestBody ProjectAttachment projectAttachment) throws Exception {
        var result = iProjectService.deleteProjectAttachmentService(projectId, projectAttachment);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "addProjectAttachment/{projectId}", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse> deleteProjectAttachment(@PathVariable int projectId,
                                                               @RequestParam(value="attachment") MultipartFile[] attachment) throws Exception {
        var result = iProjectService.addProjectAttachmentService(projectId, attachment);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "addProjectMembers/{projectId}", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse> addProjectMembers(@PathVariable int projectId,
                                                            @RequestBody List<ProjectMembers> projectMembers) throws Exception {
        var result = iProjectService.addProjectMembersService(projectId, projectMembers);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "updateProjectDetail", method = RequestMethod.POST)
    public ResponseEntity<?> updateProjectDetail(@RequestParam(value = "thumbnail", required = false)MultipartFile thumbnail,
                                              @RequestParam("data") String data) throws Exception {
        var result = iProjectService.updateProjectDetailService(data, thumbnail);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "updateProjectDescription/{projectId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProjectDescription(@PathVariable int projectId,
                                              @RequestBody String description) throws Exception {
        var result = iProjectService.updateProjectDescriptionService(projectId, description);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "manageProjectTeam/{projectId}", method = RequestMethod.PUT)
    public ResponseEntity<?> manageProjectTeam(@PathVariable int projectId,
                                              @RequestBody String teamName) throws Exception {
        var result = iProjectService.manageProjectTeamService(teamName, projectId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "deleteProjectMember/{projectId}/{projectMemberDetailId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProjectMember(@PathVariable int projectId,
                                               @PathVariable int projectMemberDetailId) throws Exception {
        var result = iProjectService.deleteProjectMemberService(projectId, projectMemberDetailId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "updateDesignationAndTeam/{projectId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateDesignationAndTeam(@PathVariable int projectId,
                                                    @RequestBody ProjectMembers projectMembers) throws Exception {
        var result = iProjectService.updateDesignationAndTeamService(projectId, projectMembers);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "updateAttachmentName/{projectId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateAttachmentName(@PathVariable int projectId,
                                                      @RequestBody ProjectAttachment projectAttachment) throws Exception {
        var result = iProjectService.updateAttachmentNameService(projectId, projectAttachment);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }
}
