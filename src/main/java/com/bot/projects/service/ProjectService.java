package com.bot.projects.service;

import com.bot.projects.entity.ProjectAppraisal;
import com.bot.projects.entity.ProjectMembers;
import com.bot.projects.model.ClientDetail;
import com.bot.projects.model.CurrentSession;
import com.bot.projects.model.DbParameters;
import com.bot.projects.entity.Projects;
import com.bot.projects.model.ProjectDetail;
import com.bot.projects.repository.LowLevelExecution;
import com.bot.projects.repository.ProjectMemberRepository;
import com.bot.projects.repository.ProjectRepository;
import com.bot.projects.serviceinterface.IProjectService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService implements IProjectService {

    @Autowired
    LowLevelExecution lowLevelExecution;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectMemberRepository projectMemberRepository;
    @Autowired
    CurrentSession currentSession;

    @Override
    public Map<String, Object> getMembersDetailService(Long employeeId) throws Exception {
        if (employeeId == 0)
            throw new Exception("Invalid user id. Please login again");

        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_EmployeeId", employeeId, Types.BIGINT));
        var dataSet = lowLevelExecution.executeProcedure("sp_project_member_get_projects", dbParameters);
        var project = objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<ProjectDetail>>() {
        });
        var projectAppraisal = objectMapper.convertValue(dataSet.get("#result-set-2"), new TypeReference<List<ProjectAppraisal>>() {
        });

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("Project", project);
        responseBody.put("ProjectAppraisal", projectAppraisal);
        return responseBody;
    }

    public List<ProjectDetail> getProjectService(Long managerId) throws Exception {
        if (managerId == 0)
            throw new Exception("Invalid user id. Please login again");

        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_EmployeeId", managerId, Types.BIGINT));
        dbParameters.add(new DbParameters("_DesignationId", 2, Types.INTEGER));
        var dataSet = lowLevelExecution.executeProcedure("sp_project_members_get_by_employee", dbParameters);
        var result = objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<ProjectDetail>>() {
        });
        return filterProjectByTeam(result, managerId);
    }

    private List<ProjectDetail> filterProjectByTeam(List<ProjectDetail> projects, long managerId) {
        List<ProjectDetail> projectResults = new ArrayList<>();
        for (Map.Entry<String, List<ProjectDetail>> entry : projects.stream()
                .collect(
                        Collectors.groupingBy(
                                ProjectDetail::getTeam,
                                Collectors.toList()
                        )
                ).entrySet()) {

            List<ProjectDetail> elem = entry.getValue();
            Optional<ProjectDetail> current = elem.stream().filter(i -> i.getMemberType() == 2).findFirst();
            if (current.isPresent()) {
                if (current.get().getEmployeeId() == managerId) {
                    current.get().setProjectManagerId(managerId);
                    projectResults.add(current.get());
                }
            } else {
                projectResults.add(elem.get(0));
            }
        }

        return projectResults;
    }

    private Projects addProjectService(Projects projects) throws Exception {
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());
        Projects lastProjectRecord = projectRepository.getLastProjectRecordId();
        if (lastProjectRecord != null)
            projects.setProjectId(lastProjectRecord.getProjectId() + 1);
        else
            projects.setProjectId(1);
        projects.setCreatedOn(date);
        projects.setUpdatedOn(date);
        projects.setCreatedBy(currentSession.getUserDetail().getUserId());
        projects.setUpdatedBy(currentSession.getUserDetail().getUserId());
        return projectRepository.save(projects);
    }

    private Projects updateProjectService(Projects projects, int projectId) throws Exception {
        if (projectId == 0)
            throw new Exception("Please select a valid project");

        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());
        Optional<Projects> resultData = projectRepository.findById(projectId);
        if (resultData.isEmpty())
            throw new Exception("Project detail not found.");
        Projects result = resultData.get();
        result.setProjectName(projects.getProjectName());
        result.setProjectDescription(projects.getProjectDescription());
        result.setHomePageUrl(projects.getHomePageUrl());
        result.setClientProject(projects.isClientProject());
        result.setClientId(projects.getClientId());
        result.setProjectStartedOn(projects.getProjectStartedOn());
        result.setProjectEndedOn(projects.getProjectEndedOn());
        result.setProjectManagerId(projects.getProjectManagerId());
        result.setCreatedOn(date);
        result.setUpdatedOn(date);
        result.setCreatedBy(currentSession.getUserDetail().getUserId());
        result.setUpdatedBy(currentSession.getUserDetail().getUserId());
        return projectRepository.save(result);
    }


    public Map<String, Object> addUpdateProjectService(int projectId, Projects projects) throws Exception {
        manageProject(projectId, projects);
        return getProjectDetailService(projectId);
    }

    public Map<String, Object> getProjectDetailService(int projectId) throws Exception {
        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_ProjectId", projectId, Types.BIGINT));
        var resultSet = lowLevelExecution.executeProcedure("sp_project_get_page_data", dbParameters);

        var result = objectMapper.convertValue(resultSet.get("#result-set-1"), new TypeReference<List<Projects>>() {});
        var clients = objectMapper.convertValue(resultSet.get("#result-set-2"), new TypeReference<List<ClientDetail>>() {});

        Map<String, List<ProjectMembers>> membersCollection = new HashMap<>();
        var members = objectMapper.convertValue(resultSet.get("#result-set-3"), new TypeReference<List<ProjectMembers>>() {});
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
    @Transactional(rollbackOn = Exception.class)
    private void manageProject(int projectId, Projects projects) throws Exception {
        Projects projectsRecords;
        if (projectId == 0) {
            projectsRecords = addProjectService(projects);
        } else {
            projectsRecords = updateProjectService(projects, projectId);
        }
        if (projectId == 0)
            projectId = projectsRecords.getProjectId();

        var updatedMemberList = updateProjectMembersService(projects.getTeamMembers(), projectsRecords.getProjectId());
        projectMemberRepository.saveAll(updatedMemberList);
    }

    private void deactivateTeam(List<ProjectMembers> oldTeam, List<ProjectMembers> newTeam) {
        int i = 0;
        while (i < newTeam.size()) {
            var currentTeam = newTeam.get(i);
            var existingTeam = oldTeam.stream().filter(x -> x.getTeam().equals(currentTeam.getTeam())).toList();
            if (existingTeam.size() == 0) {
                oldTeam.stream().
                        filter(x -> x.getEmployeeId() == currentTeam.getEmployeeId())
                        .forEach(p -> p.setActive(false));
            }
            i++;
        }
    }

    private int addUpdateMembers(List<ProjectMembers> oldTeam, List<ProjectMembers> newTeam, List<ProjectMembers> currentTeamMembers, int id) {
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());

        for (ProjectMembers members : oldTeam) {
            var current = newTeam.stream().filter(i -> i.getProjectMemberDetailId() == members.getProjectMemberDetailId()
                    && i.getTeam().equals(members.getTeam())).findFirst();
            if (current.isPresent()) {
                var member = current.get();
                member.setMemberType(members.getMemberType());
                member.setGrade(members.getGrade());
                member.setActive(true);
                currentTeamMembers.add((member));
            } else {
                members.setActive(false);
                members.setLastDateOnProject(date);
                currentTeamMembers.add(members);
            }
        }

        return id;
    }

    private void addNewTeam(List<ProjectMembers> newTeam, List<ProjectMembers> currentTeamMembers, int id, int projectId) {
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());

        for(var member : newTeam) {
            member.setProjectMemberDetailId(++id);
            member.setActive(true);
            member.setProjectId(projectId);
            member.setAssignedOn(date);

            currentTeamMembers.add(member);
        }
    }

    private List<ProjectMembers> updateProjectMembersService(List<ProjectMembers> projectMembers, int projectId) throws Exception {
        if (validateMemberInMultiTeam(projectMembers))
            throw new Exception("Same employee found in multiple teams.");

        ProjectMembers projectMember = projectMemberRepository.getLastProjectMembersRecordId();
        int lastId = projectMember.getProjectMemberDetailId();

        Date utilDate = new Date();
        var date = new Timestamp(utilDate.getTime());

        List<ProjectMembers> currentTeamMembers = new ArrayList<>();

        List<ProjectMembers> teamMembers = projectMemberRepository.getProjectMemberByProjectId(projectId);

        List<ProjectMembers> existingMembers = projectMembers.stream()
                .filter(x -> teamMembers.stream().anyMatch(i -> i.getTeam().equals(x.getTeam())))
                .toList();

        lastId = addUpdateMembers(teamMembers, existingMembers, currentTeamMembers, lastId);

        List<ProjectMembers> newTeamMembers = projectMembers.stream()
                .filter(x -> teamMembers.stream().noneMatch(i -> i.getTeam().equals(x.getTeam()) && i.getProjectMemberDetailId() == x.getProjectMemberDetailId()))
                .toList();

        addNewTeam(newTeamMembers, currentTeamMembers, lastId, projectId);

        return currentTeamMembers;
    }

    private boolean validateMemberInMultiTeam(List<ProjectMembers> members) throws Exception {
        List<Long> collect = members.stream()
                .collect(
                        Collectors.groupingBy(
                                ProjectMembers::getEmployeeId,
                                Collectors.counting()
                        )
                )
                .entrySet()
                .stream()
                .filter(x -> x.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        return collect.size() > 0;
    }
}
