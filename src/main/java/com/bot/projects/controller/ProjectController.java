package com.bot.projects.controller;

import com.bot.projects.entity.ProjectMembers;
import com.bot.projects.entity.Projects;
import com.bot.projects.model.ApiResponse;
import com.bot.projects.model.ProjectDetail;
import com.bot.projects.serviceinterface.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/ps/projects/")
public class ProjectController {
    @Autowired
    IProjectService iProjectService;
    @RequestMapping(value = "memberdetail/{employeeId}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse> getMembersDetail(@PathVariable long employeeId) throws Exception {
        var result = iProjectService.getMembersDetailService(employeeId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "get/{managerId}", method = RequestMethod.GET)
    public ResponseEntity<?> getProjects(@PathVariable("managerId") long managerId) throws Exception {
        var result = iProjectService.getProjectService(managerId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseEntity<?> addProject(@RequestBody Projects project) throws Exception {
        var result = iProjectService.addProjectService(project);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "update/{projectId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProject(@RequestBody Projects project, @PathVariable int projectId) throws Exception {
        var result = iProjectService.updateProjectService(projectId, project);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "getProjectDetail/{projectId}", method = RequestMethod.GET)
    public ResponseEntity<?> getProjectDetail(@PathVariable int projectId) throws Exception {
        var result = iProjectService.getProjectDetailService(projectId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "addProjectMembers/{projectId}", method = RequestMethod.POST)
    public ResponseEntity<?> addProjectMembers(@RequestBody List<ProjectMembers> projectMembers, @PathVariable int projectId) throws Exception {
        var result = iProjectService.addProjectMembersService(projectMembers, projectId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @RequestMapping(value = "updateProjectMembers/{projectId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProjectMembers(@RequestBody List<ProjectMembers> projectMembers, @PathVariable int projectId) throws Exception {
        var result = iProjectService.updateProjectMembersService(projectMembers, projectId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }
}
