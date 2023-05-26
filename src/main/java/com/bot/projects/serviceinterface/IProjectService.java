package com.bot.projects.serviceinterface;

import com.bot.projects.entity.Projects;

import java.util.List;

public interface IProjectService {
    List<Projects> getProjectByUserService(Long employeeId) throws Exception;
}
