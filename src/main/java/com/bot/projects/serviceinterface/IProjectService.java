package com.bot.projects.serviceinterface;

import com.bot.projects.entity.Projects;

import java.util.List;
import java.util.Map;

public interface IProjectService {
    Map<String, Object> getProjectByUserService(Long employeeId) throws Exception;
}
