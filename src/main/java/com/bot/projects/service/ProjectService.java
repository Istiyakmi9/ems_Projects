package com.bot.projects.service;

import com.bot.projects.db.service.DbManager;
import com.bot.projects.entity.ProjectMembers;
import com.bot.projects.entity.Projects;
import com.bot.projects.model.ApplicationConstant;
import com.bot.projects.model.CurrentSession;
import com.bot.projects.model.OrgHierarchyModel;
import com.bot.projects.model.ProjectDetail;
import com.bot.projects.repository.ProjectRepository;
import com.bot.projects.serviceinterface.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService implements IProjectService {

    @Autowired
    CurrentSession currentSession;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    DbManager dbManager;

    @Override
    public Map<String, Object> getMembersDetailService(Long employeeId, int projectId) throws Exception {
        if (employeeId == 0)
            throw new Exception("Invalid user id. Please login again");

        return projectRepository.getMembersDetailRepository(employeeId, projectId);
    }

    public List<ProjectDetail> getProjectService(Long managerId) throws Exception {
        if (managerId == 0)
            throw new Exception("Invalid user id. Please login again");

        var result = projectRepository.getProjectRepository(managerId);
        if (result != null)
            return filterProjectByTeam(result, managerId);
        else
            return  null;
    }

    private List<ProjectDetail> filterProjectByTeam(List<ProjectDetail> projects, long managerId) {
        List<ProjectDetail> projectResults = new ArrayList<>();
        for (Map.Entry<Integer, List<ProjectDetail>> entry : projects.stream()
                .collect(
                        Collectors.groupingBy(
                                ProjectDetail::getProjectId,
                                Collectors.toList()
                        )
                ).entrySet()) {

            List<ProjectDetail> elem = entry.getValue();
            Optional<ProjectDetail> project = elem.stream().findFirst();
            if(project.isEmpty())
                continue;

            var result = elem.stream()
                    .map(ProjectDetail::getTeam)
                    .distinct()
                    .collect(
                            Collectors.joining(",")
                    );

            project.get().setTeam(result);
            project.get().setFullName("");
            project.get().setEmployeeId(0);
            project.get().setDesignationId(0);
            project.get().setMemberType(0);

            projectResults.add(project.get());
        }

        return projectResults;
    }

    private Projects addProjectService(Projects projects) throws Exception {
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());
        int lastProjectRecordId = dbManager.nextIntPrimaryKey(Projects.class);
        projects.setProjectId(lastProjectRecordId);
        projects.setCreatedOn(date);
        projects.setUpdatedOn(date);
        projects.setCreatedBy(currentSession.getUserDetail().getUserId());
        projects.setUpdatedBy(currentSession.getUserDetail().getUserId());
        dbManager.save(projects);
        return projects;
    }

    private Projects updateProjectService(Projects projects, int projectId) throws Exception {
        if (projectId == 0)
            throw new Exception("Please select a valid project");

        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());
        Projects result = dbManager.getById(projectId, Projects.class);
        if (result == null)
            throw new Exception("Project detail not found.");

        result.setProjectName(projects.getProjectName());
        result.setProjectDescription(projects.getProjectDescription());
        result.setHomePageUrl(projects.getHomePageUrl());
        result.setClientProject(projects.isClientProject());
        result.setClientId(projects.getClientId());
        result.setProjectStartedOn(projects.getProjectStartedOn());
        result.setProjectEndedOn(projects.getProjectEndedOn());
        result.setProjectManagerId(projects.getProjectManagerId());
        result.setCEOId(projects.getCEOId());
        result.setCanCEOAccess(projects.getCanCEOAccess());
        result.setCTOId(projects.getCTOId());
        result.setCanCTOAccess(projects.getCanCTOAccess());
        result.setCFOId(projects.getCFOId());
        result.setCanCFOAccess(projects.getCanCFOAccess());
        result.setCOOId(projects.getCOOId());
        result.setCanCOOAccess(projects.getCanCOOAccess());
        result.setCreatedOn(date);
        result.setUpdatedOn(date);
        result.setCreatedBy(currentSession.getUserDetail().getUserId());
        result.setUpdatedBy(currentSession.getUserDetail().getUserId());
        dbManager.save(result);
        return result;
    }


    public Map<String, Object> addUpdateProjectService(int projectId, Projects projects) throws Exception {
        projectId = manageProject(projectId, projects);
        return getProjectDetailService(projectId);
    }

    public Map<String, Object> getProjectDetailService(int projectId) throws Exception {
        return projectRepository.getProjectDetailRepository(projectId);
    }

    private int manageProject(int projectId, Projects projects) throws Exception {
        Projects projectsRecords;
        var members = projects.getTeamMembers().stream().filter(x -> x.getMemberType() == 2).toList();
        if (members.size() > 0) {
            long projectManagerId = members.get(0).getEmployeeId();
            if (projects.getProjectManagerId() != projectManagerId) {
                projects.setProjectManagerId(projectManagerId);
                projects.getTeamMembers().forEach(x -> {
                    x.setProjectManagerId(projectManagerId);
                });
            }
        }

        addHighHierarchy(projects);
        if (projectId == 0)
            projectsRecords = addProjectService(projects);
        else
            projectsRecords = updateProjectService(projects, projectId);

        if (projects.getTeamMembers().size() > 0) {
            var updatedMemberList = updateProjectMembersService(projects.getTeamMembers(), projectsRecords.getProjectId());
            dbManager.saveAll(updatedMemberList, ProjectMembers.class);
        }
        return projectsRecords.getProjectId();
    }

    private  void addHighHierarchy(Projects projects) throws Exception {
        var highLevelHierarchy = projectRepository.getHighHierarchy(currentSession.getUserDetail().getCompanyId());
        if (highLevelHierarchy != null && highLevelHierarchy.size() > 0) {
            var cEOId = highLevelHierarchy.stream().filter(x -> x.getRoleName().equals(ApplicationConstant.CEO)).map(OrgHierarchyModel::getEmployeeId).findFirst().orElse(0L);
            var cTOId = highLevelHierarchy.stream().filter(x -> x.getRoleName().equals(ApplicationConstant.CTO)).map(OrgHierarchyModel::getEmployeeId).findFirst().orElse(0L);
            var cFOId = highLevelHierarchy.stream().filter(x -> x.getRoleName().equals(ApplicationConstant.CFO)).map(OrgHierarchyModel::getEmployeeId).findFirst().orElse(0L);
            var cOOId = highLevelHierarchy.stream().filter(x -> x.getRoleName().equals(ApplicationConstant.COO)).map(OrgHierarchyModel::getEmployeeId).findFirst().orElse(0L);
            projects.setCEOId(cEOId);
            projects.setCanCEOAccess(true);
            projects.setCTOId(cTOId);
            projects.setCanCTOAccess(true);
            projects.setCFOId(cFOId);
            projects.setCanCFOAccess(true);
            projects.setCOOId(cOOId);
            projects.setCanCOOAccess(true);
        }
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
        if (oldTeam.size() > 0) {
            for (ProjectMembers members : oldTeam) {
                var current = newTeam.stream().filter(i -> i.getProjectMemberDetailId() == members.getProjectMemberDetailId()
                        && i.getTeam().equals(members.getTeam())).findFirst();
                if (current.isPresent()) {
                    var member = current.get();
                    members.setMemberType(member.getMemberType());
                    members.setActive(true);
                    currentTeamMembers.add((members));
                } else {
                    members.setActive(false);
                    members.setLastDateOnProject(date);
                    currentTeamMembers.add(members);
                }
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

        int lastId = dbManager.nextIntPrimaryKey(ProjectMembers.class);
        var teamMembers = projectRepository.getProjectMembersRepository(projectId);

        List<ProjectMembers> currentTeamMembers = new ArrayList<>();
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

    public List<ProjectMembers> getProjectMembersService(int projectId) throws Exception {
        if (projectId == 0)
            throw new Exception("Invalid project selected");

        return  projectRepository.getProjectMembersRepository(projectId);
    }
}
