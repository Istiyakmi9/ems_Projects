package com.bot.projects.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api/ps/projects/")
public class ProjectController {

    @RequestMapping(value = "get/{employeeId}", method = RequestMethod.GET)
    public ResponseEntity<?> getProjectByUser(@PathVariable long employeeId) {
        List<String> result = Arrays.asList("test", "application");
        return ResponseEntity.ok(result);
    }
}
