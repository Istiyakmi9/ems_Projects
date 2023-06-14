package com.bot.projects.service;

import com.bot.projects.entity.EmployeeRole;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Transactional(rollbackOn = Exception.class)
    public Map<String, Object> addUpdateProjectService(int projectId, Projects projects) throws Exception {
        Projects projectsRecords;
        if (projectId == 0) {
            projectsRecords = addProjectService(projects);
        } else {
            projectsRecords = updateProjectService(projects, projectId);
        }
        if (projectId == 0)
            projectId = projectsRecords.getProjectId();

        updateProjectMembersService(projects.getTeamMembers(), projectsRecords.getProjectId());
        return getProjectDetailService(projectId);
    }

    public Map<String, Object> getProjectDetailService(int projectId) throws Exception {
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

    private void deactivateTeam(List<ProjectMembers> oldTeam, List<ProjectMembers> newTeam) {
        int i = 0;
        while(i < newTeam.size()) {
            var currentTeam = newTeam.get(i);
            var existingTeam = oldTeam.stream().filter(x -> x.getTeam().equals(currentTeam.getTeam())).toList();
            if(existingTeam.size() == 0) {
                oldTeam.stream().
                        filter(x -> x.getTeam().equals(currentTeam.getTeam()))
                        .map(i -> i.setActive(false));
            }
            i++;
        }
    }

    private Map<String, List<ProjectMembers>> updateProjectMembersService(List<ProjectMembers> projectMembers, int projectId) throws Exception {
        if (validateMemberInMultiTeam(projectMembers))
            throw new Exception("Same employee found in multiple teams.");

        List<ProjectMembers> teamMembers = projectMemberRepository.getProjectMemberByProjectId(projectId);
        deactivateTeam(teamMembers, projectMembers);

        ProjectMembers projectMember = projectMemberRepository.getLastProjectMembersRecordId();
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());

        if (teamMembers.size() > 0) {
            var id = projectMember.getProjectMemberDetailId();

            for (Map.Entry<String, List<ProjectMembers>> memberLists : projectMembers.stream()
                    .collect(
                            Collectors.groupingBy(
                                    ProjectMembers::getTeam
                            )
                    ).entrySet()) {

                var currentTeam = teamMembers.stream().filter(x -> x.getTeam().equals(memberLists.getKey())).toList();
                if (currentTeam.size() == 0) {
                    // team doesn't exists then insert all records
                    id = addNewTeamMembers(teamMembers, projectMembers, projectId, id);
                } else {
                    int j = 0;
                    while (j < currentTeam.size()) {
                        var member = currentTeam.get(j);
                        var teammemberData = teamMembers.stream().filter(i -> i.getProjectMemberDetailId() == member.getProjectMemberDetailId()).findFirst();
                        if (teammemberData.isPresent()) {
                            var teammember = teammemberData.get();
                            teammember.setGrade(member.getGrade());
                            teammember.setMemberType(member.getMemberType());
                            teammember.setProjectManagerId(member.getProjectManagerId());
                            if (member.getTeam() == null || member.getTeam().isEmpty())
                                teammember.setTeam("CORE");
                            else
                                teammember.setTeam(member.getTeam());
                        } else {
                            member.setProjectManagerId(projectMember.getProjectManagerId());
                            member.setProjectMemberDetailId(++id);
                            if (member.getTeam() == null || member.getTeam().isEmpty())
                                member.setTeam("CORE");
                            member.setProjectId(projectId);
                            member.setAssignedOn(date);
                            teamMembers.add(member);
                        }
                        j++;
                    }
                }
            }
        } else {
            addNewTeamMembers(teamMembers, projectMembers, projectId, projectMember.getProjectMemberDetailId());
        }

        projectMemberRepository.saveAll(teamMembers);
        return this.getGroupProjectMember(projectId);
    }

    private int addNewTeamMembers(List<ProjectMembers> existingMembers, List<ProjectMembers> projectMembers, int projectId, int memberId) {
        int i = 0;
        while (i < projectMembers.size()) {
            projectMembers.get(i).setProjectId(projectId);
            projectMembers.get(i).setProjectMemberDetailId(++id);
            if (projectMembers.get(i).getTeam() == null || projectMembers.get(i).getTeam().isEmpty()) {
                projectMembers.get(i).setTeam("CORE");
            }
            projectMembers.get(i).setAssignedOn(date);
            i++;
        }

        return memberId;
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

    private Map<String, List<ProjectMembers>> getGroupProjectMember(int projectId) {
        Map<String, List<ProjectMembers>> membersCollection = new HashMap<>();
        var members = projectMemberRepository.getProjectMemberByProjectId(projectId);
        if (members != null && members.size() > 0) {
            membersCollection = members.stream().collect(
                    Collectors.groupingBy(
                            ProjectMembers::getTeam
                    )
            );
        }
        return membersCollection;
    }
}
