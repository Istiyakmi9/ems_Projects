package com.bot.projects.repository;

import com.bot.projects.db.utils.LowLevelExecution;
import com.bot.projects.entity.ProjectAppraisal;
import com.bot.projects.model.DbParameters;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectAppraisalRepository {
    @Autowired
    LowLevelExecution lowLevelExecution;
    @Autowired
    ObjectMapper objectMapper;
    public List<ProjectAppraisal> getProjectAppraisalRepository(int projectId) throws Exception {
        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_ProjectId", projectId, Types.BIGINT));
        var dataSet = lowLevelExecution.executeProcedure("sp_project_appraisal_get_by_project", dbParameters);
        return objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<ProjectAppraisal>>() {
        });
    }
}
