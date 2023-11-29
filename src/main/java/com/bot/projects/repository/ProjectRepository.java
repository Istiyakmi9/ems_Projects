package com.bot.projects.repository;

import com.bot.projects.db.service.DbManager;
import com.bot.projects.db.utils.LowLevelExecution;
import com.bot.projects.entity.ProjectAppraisal;
import com.bot.projects.entity.ProjectMembers;
import com.bot.projects.entity.Projects;
import com.bot.projects.model.AppraisalReviewDetail;
import com.bot.projects.model.ClientDetail;
import com.bot.projects.model.DbParameters;
import com.bot.projects.model.ProjectDetail;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ProjectRepository {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    LowLevelExecution lowLevelExecution;
    @Autowired
    DbManager dbManager;

    public List<ProjectDetail> getProjectRepository(Long managerId) throws Exception {
        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_EmployeeId", managerId, Types.BIGINT));
        var dataSet = lowLevelExecution.executeProcedure("sp_project_members_get_by_employee", dbParameters);
        return objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<ProjectDetail>>() {});
    }

    public Map<String, Object> getMembersDetailRepository(Long employeeId, int projectId) throws Exception {
        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_EmployeeId", employeeId, Types.BIGINT));
        dbParameters.add(new DbParameters("_ProjectId", projectId, Types.INTEGER));
        var dataSet = lowLevelExecution.executeProcedure("sp_project_member_get_projects", dbParameters);
        var project = objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<ProjectDetail>>() {
        });
        var projectAppraisal = objectMapper.convertValue(dataSet.get("#result-set-2"), new TypeReference<List<ProjectAppraisal>>() {
        });
        var reviewDetail = objectMapper.convertValue(dataSet.get("#result-set-3"), new TypeReference<List<AppraisalReviewDetail>>() {
        });

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("Project", project);
        responseBody.put("ProjectAppraisal", projectAppraisal);
        responseBody.put("ReviewDetail", reviewDetail);
        return responseBody;
    }

    public Map<String, Object> getProjectDetailRepository(int projectId) throws Exception {
        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_ProjectId", projectId, Types.BIGINT));
        var resultSet = lowLevelExecution.executeProcedure("sp_project_get_page_data", dbParameters);

        var result = objectMapper.convertValue(resultSet.get("#result-set-1"), new TypeReference<List<Projects>>() {
        });
        var clients = objectMapper.convertValue(resultSet.get("#result-set-2"), new TypeReference<List<ClientDetail>>() {
        });

        Map<String, List<ProjectMembers>> membersCollection = new HashMap<>();
        var members = objectMapper.convertValue(resultSet.get("#result-set-3"), new TypeReference<List<ProjectMembers>>() {
        });

        if (members != null && members.size() > 0) {
            membersCollection = members.stream().collect(
                    Collectors.groupingBy(
                            ProjectMembers::getTeam
                    )
            );
        }

        Map<String, Object> map = new HashMap<>();
        map.put("Project", result);
        map.put("Members", membersCollection);
        map.put("Clients", clients);

        return map;
    }

    public List<ProjectMembers> getProjectMembersRepository(int projectId) throws Exception {
        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_ProjectId", projectId, Types.BIGINT));
        var resultSet = lowLevelExecution.executeProcedure("sp_project_member_getby_projectid", dbParameters);
        return objectMapper.convertValue(resultSet.get("#result-set-1"), new TypeReference<List<ProjectMembers>>() { });
    }
}
