package com.bot.projects.controller;

import com.bot.projects.entity.ProjectAppraisal;
import com.bot.projects.model.ApiResponse;
import com.bot.projects.serviceinterface.IProjectAppraisalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/ps/projectsAppraisal/")

public class ProjectAppraisalController {
    @Autowired
    IProjectAppraisalService projectAppraisalService;
    @GetMapping("getProjectAppraisal/{projectId}")
    public ResponseEntity<ApiResponse> getProjectAppraisal(@PathVariable int projectId) throws Exception {
        var result = projectAppraisalService.getProjectAppraisalService(projectId);
        return  ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @PostMapping("addProjectAppraisal")
    public ResponseEntity<ApiResponse> addProjectAppraisal(@RequestBody ProjectAppraisal projectAppraisal) throws Exception {
        var result = projectAppraisalService.addProjectAppraisalService(projectAppraisal);
        return  ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @PutMapping("updateProjectAppraisal/{projectAppraisalId}")
    public ResponseEntity<ApiResponse> updateProjectAppraisal(@PathVariable int projectAppraisalId,
                                                              @RequestBody ProjectAppraisal projectAppraisal) throws Exception {
        var result = projectAppraisalService.updateProjectAppraisalService(projectAppraisalId, projectAppraisal);
        return  ResponseEntity.ok(ApiResponse.Ok(result));
    }
}
