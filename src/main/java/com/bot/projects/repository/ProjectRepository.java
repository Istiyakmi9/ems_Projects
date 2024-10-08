package com.bot.projects.repository;

import com.bot.projects.db.utils.LowLevelExecution;
import com.bot.projects.entity.ProjectAppraisal;
import com.bot.projects.entity.ProjectMembers;
import com.bot.projects.entity.Projects;
import com.bot.projects.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ProjectRepository {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    LowLevelExecution lowLevelExecution;

    public List<ProjectDetail> getProjectRepository(Long managerId) throws Exception {
        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_EmployeeId", managerId, Types.BIGINT));
        var dataSet = lowLevelExecution.executeProcedure("sp_project_members_get_by_employee", dbParameters);
        if (dataSet.containsKey("#result-set-1"))
            return objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<ProjectDetail>>() {});
        else
            return null;
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

    public List<OrgHierarchyModel> getHighHierarchy(int companyId) throws Exception {
        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_CompanyId", companyId, Types.INTEGER));
        var resultSet = lowLevelExecution.executeProcedure("sp_org_hierarchy_highlevel_byId", dbParameters);
        return objectMapper.convertValue(resultSet.get("#result-set-1"), new TypeReference<List<OrgHierarchyModel>>() { });
    }

    public  Map<String, Object> getProjectMemberByFilterService(FilterModel filterModel) throws Exception {
        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_SearchString", filterModel.getSearchString(), Types.VARCHAR));
        dbParameters.add(new DbParameters("_SortBy", filterModel.getSearchString(), Types.VARCHAR));
        dbParameters.add(new DbParameters("_PageIndex", filterModel.getPageIndex(), Types.INTEGER));
        dbParameters.add(new DbParameters("_PageSize", filterModel.getPageSize(), Types.INTEGER));
        var resultSet = lowLevelExecution.executeProcedure("sp_project_member_getby_filter", dbParameters);
        var projectMembers = objectMapper.convertValue(resultSet.get("#result-set-1"), new TypeReference<List<ProjectMembers>>() { });
        var workingShift = objectMapper.convertValue(resultSet.get("#result-set-2"), new TypeReference<List<WorkShift>>() { });

        Map<String, Object> map = new HashMap<>();
        map.put("ProjectMember", projectMembers);
        map.put("WorkingShift", workingShift);

        return map;
    }
}
