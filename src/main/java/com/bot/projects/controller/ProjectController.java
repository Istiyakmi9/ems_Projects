package com.bot.projects.controller;

import com.bot.projects.model.ApiResponse;
import com.bot.projects.serviceinterface.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/ps/projects/")
public class ProjectController {
    @Autowired
    IProjectService iProjectService;
    @RequestMapping(value = "get/{employeeId}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse> getProjectByUser(@PathVariable long employeeId) throws Exception {
        var result = iProjectService.getProjectByUserService(employeeId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }
}
