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
        var project = objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<ProjectDetail>>() {});
        var projectAppraisal = objectMapper.convertValue(dataSet.get("#result-set-2"), new TypeReference<List<ProjectAppraisal>>() {});

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("Project", project);
        responseBody.put("ProjectAppraisal", projectAppraisal);
        return  responseBody;
    }

    public List<ProjectDetail> getProjectService(Long managerId) throws Exception {
        if (managerId == 0)
            throw new Exception("Invalid user id. Please login again");

        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_EmployeeId", managerId, Types.BIGINT));
        dbParameters.add(new DbParameters("_DesignationId", 2, Types.INTEGER));
        var dataSet = lowLevelExecution.executeProcedure("sp_project_members_get_by_employee", dbParameters);
        var result = objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<ProjectDetail>>() {});
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
            if(current.isPresent()) {
                if(current.get().getEmployeeId() == managerId) {
                    current.get().setProjectManagerId(managerId);
                    projectResults.add(current.get());
                }
            } else {
                projectResults.add(elem.get(0));
            }
        }

        return projectResults;
    }

    @Transactional
    public Projects addProjectService(Projects projects) throws Exception {
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());
        Projects lastProjectRecord = projectRepository.getLastProjectRecordId();
        if(lastProjectRecord != null)
            projects.setProjectId(lastProjectRecord.getProjectId() + 1);
        else
            projects.setProjectId(1);
        projects.setCreatedOn(date);
        projects.setUpdatedOn(date);
        projects.setCreatedBy(currentSession.getUserDetail().getUserId());
        projects.setUpdatedBy(currentSession.getUserDetail().getUserId());
        //        if (projects.getProjectId() == 0) {
//            Projects lastProjectRecord = projectRepository.getLastProjectRecordId();
//            if(lastProjectRecord != null) {
//                projects.setProjectId(lastProjectRecord.getProjectId() + 1);
//            } else {
//                projects.setProjectId(1);
//            }
//
//            result = projectRepository.save(projects);
//        } else {
//            var data = projectRepository.findById(projects.getProjectId());
//            if (data.isPresent()) {
//                result = data.get();
//            } else {
//                result = null;
//                throw new Exception("Project doesn't exist.");
//            }
//        }

//        int id = 0;
//        ProjectMembers projectMember = projectMemberRepository.getLastProjectMembersRecordId();
//        List<ProjectMembers> projectMembers = projects.getTeamMembers();
//        if (projectMember != null){
//            id = projectMember.getProjectMemberDetailId();
//        }
//
//        int i = 0;
//        while(i < projectMembers.size()) {
//            projectMembers.get(i).setProjectId(result.getProjectId());
//            projectMembers.get(i).setProjectMemberDetailId(++id);
//            if(projectMembers.get(i).getTeam() == null || projectMembers.get(i).getTeam().isEmpty()) {
//                projectMembers.get(i).setTeam("CORE");
//            }
//            i++;
//        }
//
//        List<ProjectMembers> resultSet = projectMemberRepository.saveAll(projectMembers);

//        result.setTeamMembers(resultSet);
        return projectRepository.save(projects);
    }

    @Transactional
    public Projects updateProjectService(int projectId, Projects projects) throws Exception {
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

//        List<ProjectMembers> projectMembers = projects.getTeamMembers();
//        List<ProjectMembers> teamMembers = projectMemberRepository.getProjectMemberByProjectId(projectId);
//        ProjectMembers projectMember = projectMemberRepository.getLastProjectMembersRecordId();
//        if (teamMembers.size() > 0) {
//            List<ProjectMembers> finalTeamMembers = teamMembers;
//            projectMembers.forEach(x -> {
//                var id = projectMember.getProjectMemberDetailId();
//                var teammemberData = finalTeamMembers.stream().filter(i -> i.getProjectMemberDetailId() == x.getProjectMemberDetailId()).findFirst();
//                if (teammemberData.isPresent()) {
//                    var teammember = teammemberData.get();
//                    teammember.setGrade(x.getGrade());
//                    teammember.setMemberType(x.getMemberType());
//                    teammember.setProjectManagerId(x.getProjectManagerId());
//                    if(x.getTeam() == null || x.getTeam().isEmpty())
//                        teammember.setTeam("CORE");
//                    else
//                        teammember.setTeam(x.getTeam());
//                } else  {
//                    id = id + 1;
//                    x.setProjectManagerId(projectMember.getProjectManagerId());
//                    x.setProjectMemberDetailId(id);
//                    if(x.getTeam() == null || x.getTeam().isEmpty())
//                        x.setTeam("CORE");
//                    x.setProjectId(projectId);
//                    finalTeamMembers.add(x);
//                }
//            });
//        } else {
//            int id = 0;
//            if (projectMember != null){
//                id = projectMember.getProjectMemberDetailId();
//            }
//
//            int i = 0;
//            while(i < projectMembers.size()) {
//                projectMembers.get(i).setProjectId(projectId);
//                projectMembers.get(i).setProjectMemberDetailId(++id);
//                if(projectMembers.get(i).getTeam() == null || projectMembers.get(i).getTeam().isEmpty()) {
//                    projectMembers.get(i).setTeam("CORE");
//                }
//                i++;
//            }
//            teamMembers = projectMembers;
//        }
//        List<ProjectMembers> resultSet = projectMemberRepository.saveAll(teamMembers);
//
//        result.setTeamMembers(resultSet);
    }

    public Map<String, Object> getProjectDetailService(int projectId) throws Exception {
//        if (projectId <= 0)
//            throw new Exception("Invalid project id passed.");

        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_ProjectId", projectId, Types.BIGINT));
        var resultSet = lowLevelExecution.executeProcedure("sp_project_get_page_data", dbParameters);

        var result = objectMapper.convertValue(resultSet.get("#result-set-1"), new TypeReference<List<Projects>>() {});
//        if(result == null || result.size() == 0) {
//            throw new Exception("Project detail not found.");
//        }

        var clients = objectMapper.convertValue(resultSet.get("#result-set-2"), new TypeReference<List<ClientDetail>>() {});

        Map<String, List<ProjectMembers>> membersCollection = new HashMap<>();
        var members = objectMapper.convertValue(resultSet.get("#result-set-3"), new TypeReference<List<ProjectMembers>>() {});
        if(members != null && members.size() > 0) {
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

    public Map<String, List<ProjectMembers>> addProjectMembersService(List<ProjectMembers> projectMembers, int projectId) throws Exception {
        if (projectId == 0)
            throw new Exception("Invalid project");

        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());
        int id = 0;
        ProjectMembers projectMember = projectMemberRepository.getLastProjectMembersRecordId();
        if (projectMember != null){
            id = projectMember.getProjectMemberDetailId();
        }

        int i = 0;
        while(i < projectMembers.size()) {
            projectMembers.get(i).setProjectId(projectId);
            projectMembers.get(i).setProjectMemberDetailId(++id);
            if(projectMembers.get(i).getTeam() == null || projectMembers.get(i).getTeam().isEmpty()) {
                projectMembers.get(i).setTeam("CORE");
            }
            projectMembers.get(i).setAssignedOn(date);
            i++;
        }

        projectMemberRepository.saveAll(projectMembers);
        return this.getGroupProjectMember(projectId);
    }

    public Map<String, List<ProjectMembers>> updateProjectMembersService(List<ProjectMembers> projectMembers, int projectId) throws Exception {
        List<ProjectMembers> teamMembers = projectMemberRepository.getProjectMemberByProjectId(projectId);
        ProjectMembers projectMember = projectMemberRepository.getLastProjectMembersRecordId();
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());
        if (teamMembers.size() > 0) {
            List<ProjectMembers> finalTeamMembers = teamMembers;
            int j = 0;
            var id = projectMember.getProjectMemberDetailId();
            while (j < projectMembers.size()) {
                int finalJ = j;
                var teammemberData = finalTeamMembers.stream().filter(i -> i.getProjectMemberDetailId() == projectMembers.get(finalJ).getProjectMemberDetailId()).findFirst();
                if (teammemberData.isPresent()) {
                    var teammember = teammemberData.get();
                    teammember.setGrade(projectMembers.get(j).getGrade());
                    teammember.setMemberType(projectMembers.get(j).getMemberType());
                    teammember.setProjectManagerId(projectMembers.get(j).getProjectManagerId());
                    if(projectMembers.get(j).getTeam() == null || projectMembers.get(j).getTeam().isEmpty())
                        teammember.setTeam("CORE");
                    else
                        teammember.setTeam(projectMembers.get(j).getTeam());
                } else  {
                    projectMembers.get(j).setProjectManagerId(projectMember.getProjectManagerId());
                    projectMembers.get(j).setProjectMemberDetailId(++id);
                    if(projectMembers.get(j).getTeam() == null || projectMembers.get(j).getTeam().isEmpty())
                        projectMembers.get(j).setTeam("CORE");
                    projectMembers.get(j).setProjectId(projectId);
                    projectMembers.get(j).setAssignedOn(date);
                    finalTeamMembers.add(projectMembers.get(j));
                }
                j++;
            }
        } else {
            int id = 0;
            if (projectMember != null){
                id = projectMember.getProjectMemberDetailId();
            }

            int i = 0;
            while(i < projectMembers.size()) {
                projectMembers.get(i).setProjectId(projectId);
                projectMembers.get(i).setProjectMemberDetailId(++id);
                if(projectMembers.get(i).getTeam() == null || projectMembers.get(i).getTeam().isEmpty()) {
                    projectMembers.get(i).setTeam("CORE");
                }
                projectMembers.get(i).setAssignedOn(date);
                i++;
            }
            teamMembers = projectMembers;
        }
        projectMemberRepository.saveAll(teamMembers);
        return this.getGroupProjectMember(projectId);
    }

    private Map<String, List<ProjectMembers>> getGroupProjectMember(int projectId) {
        Map<String, List<ProjectMembers>> membersCollection = new HashMap<>();
        var members = projectMemberRepository.getProjectMemberByProjectId(projectId);
        if(members != null && members.size() > 0) {
            membersCollection = members.stream().collect(
                    Collectors.groupingBy(
                            ProjectMembers::getTeam
                    )
            );
        }
        return membersCollection;
    }
}
