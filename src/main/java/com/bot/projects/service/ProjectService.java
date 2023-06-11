package com.bot.projects.service;

import com.bot.projects.entity.EmployeeRole;
import com.bot.projects.entity.ProjectAppraisal;
import com.bot.projects.model.DbParameters;
import com.bot.projects.entity.Projects;
import com.bot.projects.repository.LowLevelExecution;
import com.bot.projects.serviceinterface.IProjectService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService implements IProjectService {

    @Autowired
    LowLevelExecution lowLevelExecution;
    @Autowired
    ObjectMapper objectMapper;
    @Override
    public Map<String, Object> getProjectByUserService(Long employeeId) throws Exception {
        if (employeeId == 0)
            throw new Exception("Invalid user id. Please login again");

        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_EmployeeId", employeeId, Types.BIGINT));
        var dataSet = lowLevelExecution.executeProcedure("sp_project_member_get_projects", dbParameters);
        var project = objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<Projects>>() {});
        var designation = objectMapper.convertValue(dataSet.get("#result-set-2"), new TypeReference<List<EmployeeRole>>() {});
        var projectAppraisal = objectMapper.convertValue(dataSet.get("#result-set-3"), new TypeReference<List<ProjectAppraisal>>() {});
        project.forEach(x -> {
            var rolename = designation.stream().filter(i -> i.getRoleId() == x.getDesignationId()).findFirst().get();
            x.setDesignationName(rolename.getRoleName());
        });
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("Project", project);
        responseBody.put("Designation", designation);
        responseBody.put("ProjectAppraisal", projectAppraisal);
        return  responseBody;
    }
}
