package com.bot.projects.service;

import com.bot.projects.db.service.DbManager;
import com.bot.projects.entity.ProjectMembers;
import com.bot.projects.entity.ProjectTeams;
import com.bot.projects.entity.Projects;
import com.bot.projects.model.*;
import com.bot.projects.repository.ProjectRepository;
import com.bot.projects.serviceinterface.IProjectService;
import com.bot.projects.util.MicroserviceRequest;
import com.bot.projects.util.RequestMicroservice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService implements IProjectService {
    @Autowired
    CurrentSession currentSession;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    DbManager dbManager;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RequestMicroservice requestMicroservice;

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
            return null;
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
            if (project.isEmpty())
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
        try {
            java.util.Date utilDate = new java.util.Date();
            var date = new java.sql.Timestamp(utilDate.getTime());
            addHighHierarchy(projects);
            projects.setCreatedOn(date);
            projects.setUpdatedOn(date);
            projects.setCreatedBy(currentSession.getUserDetail().getUserId());
            projects.setUpdatedBy(currentSession.getUserDetail().getUserId());
            projects.setCEOId(1L);
            dbManager.save(projects);
            return projects;
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }

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


    public String addProjectDetailService(String data, MultipartFile[] attachment, MultipartFile thumbnail) throws Exception {
        Projects projectData = objectMapper.readValue(data, Projects.class);
        validateProjectDetail(projectData, thumbnail);

        if (projectData.getProjectId() == 0)
            projectData.setProjectId(dbManager.nextIntPrimaryKey(Projects.class));

        var projectDescriptionPath = saveProjectDescription(projectData, projectData.getProjectDescription());
        projectData.setProjectDescriptionFilePath(projectDescriptionPath);

        MultipartFile[] fileArray = new MultipartFile[]{thumbnail};
        var thumbnailDetail = saveProjectFile(fileArray, null, projectData.getProjectId());
        var thumbnailPath = Paths.get(thumbnailDetail.get(0).getFilePath(), thumbnailDetail.get(0).getFileName()).toString();
        thumbnailPath = replaceEscapeCharacter(thumbnailPath);
        projectData.setThumbnailPath(thumbnailPath);

        if (attachment != null && attachment.length > 0) {
            var attachmentPaths = new ArrayList<ProjectAttachment>();
            java.util.Date utilDate = new java.util.Date();
            var date = new java.sql.Timestamp(utilDate.getTime());

            var attachments = saveProjectFile(attachment, null, projectData.getProjectId());
            attachments.forEach(x -> {
                attachmentPaths.add(ProjectAttachment.builder()
                        .index(attachmentPaths.size() + 1)
                        .filePath(replaceEscapeCharacter(x.getFilePath()))
                        .fileName(x.getFileName())
                        .fileType(x.getFileExtension())
                        .fileSize(x.getFileSize())
                        .uploadedDate(date)
                        .build());
            });


            projectData.setAttachmentPath(objectMapper.writeValueAsString(attachmentPaths));
        }
        projectData.setProjectDescription(objectMapper.writeValueAsString(projectData.getProjectDescription()));
        addProjectService(projectData);
//        projectId = manageProject(projectId, projects);
//        return getProjectDetailService(projectId);
        return "added";
    }

    private void validateProjectDetail(Projects projects, MultipartFile thumbnail) throws Exception {
        if (projects.getProjectName().isEmpty() || projects.getProjectName() == null)
            throw new Exception("Invalid project name");

        if (projects.getProjectDescription().isEmpty() || projects.getProjectDescription() == null)
            throw new Exception("Invalid project description");

        if (projects.getPriority() == 0)
            throw new Exception("Invalid priority selected");

        if (projects.getStatus() == 0)
            throw new Exception("Invalid status selected");

        if (projects.isClientProject() && projects.getClientId() == 0)
            throw new Exception("Invalid client selected");

        if ((projects.getStatus() == 2 || projects.getStatus() == 3 || projects.getStatus() == 4) && projects.getProjectStartedOn() == null)
            throw new Exception("Invalid project start date selected");

        if (projects.getStatus() == 4 && projects.getProjectEndedOn() == null)
            throw new Exception("Invalid project end date selected");

        if (thumbnail == null)
            throw new Exception("Invalid project thumbnail");
    }

    private List<Files> saveProjectFile(MultipartFile[] files, List<String> oldFileName, int projectId) throws Exception {
        String documentPath = Paths.get(currentSession.getCompanyCode(), "project_" + projectId).toString();

        FileFolderDetail fileFolderDetail = FileFolderDetail.builder()
                .OldFileName(oldFileName)
                .ServiceName(ApplicationConstant.EmstumFileService)
                .FolderPath(documentPath)
                .build();

        MicroserviceRequest microserviceRequest = MicroserviceRequest.builder()
                .payload(objectMapper.writeValueAsString(fileFolderDetail))
                .token(currentSession.getAuthorization())
                .connectionString(currentSession.getLocalConnectionString())
                .companyCode(currentSession.getCompanyCode())
                .fileCollections(files)
                .build();
        return requestMicroservice.uploadFile(microserviceRequest);
    }

    private Files saveTextFile(TextFileFolderDetail textFileFolderDetail) throws Exception {
        MicroserviceRequest microserviceRequest = MicroserviceRequest.builder()
                .token(currentSession.getAuthorization())
                .connectionString(currentSession.getLocalConnectionString())
                .companyCode(currentSession.getCompanyCode())
                .build();
        return requestMicroservice.saveTextFile(microserviceRequest, textFileFolderDetail);
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

    private void addHighHierarchy(Projects projects) throws Exception {
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

        for (var member : newTeam) {
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

        return projectRepository.getProjectMembersRepository(projectId);
    }

    public Map<String, Object> getProjectMemberByFilterService(FilterModel filterModel) throws Exception {
        return projectRepository.getProjectMemberByFilterService(filterModel);
    }

    public List<ProjectAttachment> deleteProjectAttachmentService(int projectId, ProjectAttachment projectAttachment) throws Exception {
        if (projectId == 0)
            throw new Exception("Invalid project id");

        if (projectAttachment.getIndex() == 0)
            throw new Exception("Invalid attachment selected");

        Projects result = dbManager.getById(projectId, Projects.class);
        if (result == null)
            throw new Exception("Project detail not found.");

        var projectAttachments = objectMapper.readValue(result.getAttachmentPath(), new TypeReference<List<ProjectAttachment>>() {
        });
        var selectedAttachment = projectAttachments.stream().filter(x -> x.getIndex() == projectAttachment.getIndex())
                .findFirst().orElseThrow(() -> new Exception("File detail not found"));
        projectAttachments.remove(selectedAttachment);

        deleteFile(selectedAttachment.getFileName(), projectId);

        if (projectAttachments.size() > 0) {
            projectAttachments.forEach(x -> {
                x.setFilePath(replaceEscapeCharacter(x.getFilePath()));
            });
        }
        result.setAttachmentPath(projectAttachments.size() > 0 ? objectMapper.writeValueAsString(projectAttachments) : "[]");
        result.setProjectDescriptionFilePath(replaceEscapeCharacter(result.getProjectDescriptionFilePath()));
        result.setThumbnailPath(replaceEscapeCharacter(result.getThumbnailPath()));

        dbManager.save(result);

        return projectAttachments;
    }

    public List<ProjectAttachment> addProjectAttachmentService(int projectId, MultipartFile[] attachment) throws Exception {
        if (projectId == 0)
            throw new Exception("Invalid project id");

        if (attachment == null || attachment.length == 0)
            throw new Exception("Invalid attachment selected");

        Projects result = dbManager.getById(projectId, Projects.class);
        if (result == null)
            throw new Exception("Project detail not found.");

        ArrayList<ProjectAttachment> attachmentPaths;
        int lastIndex;
        if (result.getAttachmentPath() != null && !result.getAttachmentPath().equals("") && !result.getAttachmentPath().equals("[]")) {
            attachmentPaths = objectMapper.readValue(result.getAttachmentPath(), new TypeReference<ArrayList<ProjectAttachment>>() {
            });

            attachmentPaths.forEach(x -> {
                x.setFilePath(replaceEscapeCharacter(x.getFilePath()));
            });

            lastIndex = attachmentPaths.get(attachmentPaths.size() - 1).getIndex();
        } else {
            lastIndex = 0;
            attachmentPaths = new ArrayList<ProjectAttachment>();
        }

        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());

        var attachments = saveProjectFile(attachment, null, projectId);
        attachments.forEach(x -> {
            attachmentPaths.add(ProjectAttachment.builder()
                    .index(lastIndex + 1)
                    .filePath(replaceEscapeCharacter(x.getFilePath()))
                    .fileName(x.getFileName())
                    .fileType(x.getFileExtension())
                    .fileSize(x.getFileSize())
                    .uploadedDate(date)
                    .build());
        });

        result.setAttachmentPath(objectMapper.writeValueAsString(attachmentPaths));
        result.setProjectDescriptionFilePath(replaceEscapeCharacter(result.getProjectDescriptionFilePath()));
        result.setThumbnailPath(replaceEscapeCharacter(result.getThumbnailPath()));

        dbManager.save(result);

        return attachmentPaths;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ProjectMembers> addProjectMembersService(int projectId, List<ProjectMembers> projectMembers) throws Exception {
        if (projectId == 0)
            throw new Exception("Invalid project id");

        if (projectMembers.size() == 0)
            throw new Exception("Invalid project members");

        int lastId = dbManager.nextIntPrimaryKey(ProjectMembers.class);
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());

        for (ProjectMembers projectMember : projectMembers) {
            validateProjectMember(projectMember);

            projectMember.setProjectId(projectId);
            projectMember.setProjectMemberDetailId(lastId++);
            projectMember.setAssignedOn(date);
            projectMember.setActive(true);
            projectMember.setProjectManagerId(0);
            projectMember.setAllocatedMinutes(0);
        }

        dbManager.saveAll(projectMembers, ProjectMembers.class);

        String teamName = projectMembers.get(0).getTeam();
        List<Long> membersId = projectMembers.stream().map(ProjectMembers::getEmployeeId).toList();
        updateProjectTeams(projectId, teamName, membersId);

        return projectRepository.getProjectMembersRepository(projectId);
    }

    private void updateProjectTeams(int projectId, String teamName, List<Long> membersId) throws Exception {
        var projectTeam = dbManager.getById(projectId, ProjectTeams.class);
        if (projectTeam == null)
            throw new Exception("Project team not found");

        List<List<?>> result = manageTeamsDetail(projectTeam.getTeams(), teamName, membersId);
        List<TeamsDetail> teamsDetails = (List<TeamsDetail>) result.get(0);
        var teamUpdatesDetail = manageTeamUpdateDetail(projectTeam.getTeamUpdates(), teamName, (List<Long>) result.get(1));

        projectTeam.setTeams(objectMapper.writeValueAsString(teamsDetails));
        projectTeam.setTeamUpdates(objectMapper.writeValueAsString(teamUpdatesDetail));

        dbManager.save(projectTeam);
    }

    private void validateProjectMember(ProjectMembers projectMembers) throws Exception {
        if (projectMembers.getFullName() == null || projectMembers.getFullName().isEmpty())
            throw new Exception("Invalid employee name");

        if (projectMembers.getEmail() == null || projectMembers.getEmail().isEmpty())
            throw new Exception("Invalid email id");

        if (projectMembers.getEmployeeId() == 0)
            throw new Exception("Invalid employee selected");

        if (projectMembers.getMemberType() == 0)
            throw new Exception("Invalid employee designation selected");

        if (projectMembers.getTeam() == null || projectMembers.getTeam().isEmpty())
            throw new Exception("Invalid team selected");
    }

    private void manageTeams(int projectId, List<ProjectMembers> members) throws Exception {
        var existingProjectTeams = dbManager.getById(projectId, ProjectTeams.class);

        String teamName = members.get(0).getTeam();
        List<Long> membersId = members.stream().map(ProjectMembers::getEmployeeId).toList();

        List<List<?>> result = manageTeamsDetail(existingProjectTeams.getTeams(), teamName, membersId);
        List<TeamsDetail> teamsDetails = (List<TeamsDetail>) result.get(0);

        var teamUpdatesDetails = manageTeamUpdateDetail(existingProjectTeams.getTeamUpdates(), teamName, (List<Long>) result.get(1));
        existingProjectTeams.setTeams(objectMapper.writeValueAsString(teamsDetails));
        existingProjectTeams.setTeamUpdates(objectMapper.writeValueAsString(teamUpdatesDetails));

        dbManager.save(existingProjectTeams);
    }

    public Projects updateProjectDetailService(String data, MultipartFile thumbnail) throws Exception {
        Projects projectData = objectMapper.readValue(data, Projects.class);
        if (projectData.getProjectId() == 0)
            throw new Exception("Invalid project id");

        validateEditProjectDetail(projectData);

        Projects existingProject = dbManager.getById(projectData.getProjectId(), Projects.class);
        if (existingProject == null)
            throw new Exception("Project detail not found.");

        updateProjectDetail(projectData, existingProject);

        if (thumbnail!= null && !thumbnail.isEmpty())
            updateProjectThumbnail(thumbnail, existingProject);
        else
            existingProject.setThumbnailPath(replaceEscapeCharacter(existingProject.getThumbnailPath()));

        existingProject.setProjectDescriptionFilePath(replaceEscapeCharacter(existingProject.getProjectDescriptionFilePath()));

        if (existingProject.getAttachmentPath() != null && !existingProject.getAttachmentPath().equals("") && !existingProject.getAttachmentPath().equals("[]")) {
            var projectAttachments = objectMapper.readValue(existingProject.getAttachmentPath(), new TypeReference<List<ProjectAttachment>>() {
            });

            if (projectAttachments.size() > 0) {
                projectAttachments.forEach(x -> {
                    x.setFilePath(replaceEscapeCharacter(x.getFilePath()));
                });
            }
            existingProject.setAttachmentPath(objectMapper.writeValueAsString(projectAttachments));
        }

        dbManager.save(existingProject);

        return existingProject;
    }

    public String updateProjectDescriptionService(int projectId, String description) throws Exception {
        if (projectId == 0)
            throw new Exception("Invalid project id");

        if (description == null || description.isEmpty())
            throw new Exception("Invalid project description");

        Projects existingProject = dbManager.getById(projectId, Projects.class);
        if (existingProject == null)
            throw new Exception("Project detail not found.");

        String projectDescriptionPath = saveProjectDescription(existingProject, description);
        existingProject.setProjectDescriptionFilePath(projectDescriptionPath);

        existingProject.setThumbnailPath(replaceEscapeCharacter(existingProject.getThumbnailPath()));
        if (existingProject.getAttachmentPath() != null && !existingProject.getAttachmentPath().equals("") && !existingProject.getAttachmentPath().equals("[]")) {
            var projectAttachments = objectMapper.readValue(existingProject.getAttachmentPath(), new TypeReference<List<ProjectAttachment>>() {
            });

            if (projectAttachments.size() > 0) {
                projectAttachments.forEach(x -> {
                    x.setFilePath(replaceEscapeCharacter(x.getFilePath()));
                });
            }
            existingProject.setAttachmentPath(objectMapper.writeValueAsString(projectAttachments));
        }

        dbManager.save(existingProject);

        return description;
    }

    private String saveProjectDescription(Projects projects, String description) throws Exception {
        String filename = projects.getProjectName().replaceAll("\\s+", "");
        filename = filename.substring(0, Math.min(filename.length(), 15)) + "_" + projects.getProjectId() + ".txt";

        String documentPath = Paths.get(currentSession.getCompanyCode(), "project_" + projects.getProjectId()).toString();
        String oldFileName = null;
        if (projects.getProjectDescriptionFilePath() != null && !projects.getProjectDescriptionFilePath().isEmpty())
            oldFileName = Path.of(projects.getProjectDescriptionFilePath()).getFileName().toString();

        TextFileFolderDetail textFileFolderDetail = TextFileFolderDetail.builder()
                .textDetail(description)
                .oldFileName(oldFileName)
                .serviceName(ApplicationConstant.EmstumFileService)
                .folderPath(documentPath)
                .fileName(filename)
                .build();

        var projectDescriptionFile = saveTextFile(textFileFolderDetail);
        var projectDescriptionPath = Paths.get(projectDescriptionFile.getFilePath(), projectDescriptionFile.getFileName()).toString();
        projectDescriptionPath = replaceEscapeCharacter(projectDescriptionPath);

        return projectDescriptionPath;
    }

    private void updateProjectThumbnail(MultipartFile thumbnail, Projects existingProject) throws Exception {
        MultipartFile[] fileArray = new MultipartFile[]{thumbnail};
        var oldFileName = Path.of(existingProject.getThumbnailPath()).getFileName().toString();

        var thumbnailDetail = saveProjectFile(fileArray, Arrays.asList(oldFileName), existingProject.getProjectId());
        var thumbnailPath = Paths.get(thumbnailDetail.get(0).getFilePath(), thumbnailDetail.get(0).getFileName()).toString();
        thumbnailPath = replaceEscapeCharacter(thumbnailPath);
        existingProject.setThumbnailPath(thumbnailPath);
    }

    private void updateProjectDetail(Projects projectData, Projects existingProject) {
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());

        existingProject.setProjectName(projectData.getProjectName());
        existingProject.setClientProject(projectData.isClientProject());
        existingProject.setClientId(projectData.getClientId());
        existingProject.setProjectStartedOn(projectData.getProjectStartedOn());
        existingProject.setProjectEndedOn(projectData.getProjectEndedOn());
        existingProject.setUpdatedOn(date);
        existingProject.setUpdatedBy(currentSession.getUserDetail().getUserId());
    }

    private void deleteFile(String oldFileName, int projectId) throws Exception {
        String documentPath = Paths.get(currentSession.getCompanyCode(), "project_" + projectId).toString();

        FileFolderDetail fileFolderDetail = FileFolderDetail.builder()
                .DeletableFiles(Arrays.asList(oldFileName))
                .ServiceName(ApplicationConstant.EmstumFileService)
                .FolderPath(documentPath)
                .build();

        MicroserviceRequest microserviceRequest = MicroserviceRequest.builder()
                .token(currentSession.getAuthorization())
                .connectionString(currentSession.getLocalConnectionString())
                .companyCode(currentSession.getCompanyCode())
                .build();

        var result = requestMicroservice.deleteFile(microserviceRequest, fileFolderDetail);
        if (result == null || result.isEmpty())
            throw new Exception("Fail to delete the file");
    }

    private void validateEditProjectDetail(Projects projects) throws Exception {
        if (projects.getProjectName().isEmpty() || projects.getProjectName() == null)
            throw new Exception("Invalid project name");

        if (projects.getPriority() == 0)
            throw new Exception("Invalid priority selected");

        if (projects.getStatus() == 0)
            throw new Exception("Invalid status selected");

        if (projects.isClientProject() && projects.getClientId() == 0)
            throw new Exception("Invalid client selected");

        if ((projects.getStatus() == 2 || projects.getStatus() == 3 || projects.getStatus() == 4) && projects.getProjectStartedOn() == null)
            throw new Exception("Invalid project start date selected");

        if (projects.getStatus() == 4 && projects.getProjectEndedOn() == null)
            throw new Exception("Invalid project end date selected");
    }

    private String replaceEscapeCharacter(String filePath) {
        return  filePath.replace("\\", "\\\\");
    }

    public List<String> manageProjectTeamService(String teamName, int projectId) throws Exception {
        if (teamName == null || teamName.isEmpty())
            throw new Exception(("Invalid team name"));

        if (projectId == 0)
            throw new Exception("Invalid project selected");

        var existingProjectTeams = dbManager.getById(projectId, ProjectTeams.class);
        if (existingProjectTeams == null)
            existingProjectTeams = new ProjectTeams();

        List<List<?>> result =  manageTeamsDetail(existingProjectTeams.getTeams(), teamName, new ArrayList<>());
        var teamUpdatesDetail = manageTeamUpdateDetail(existingProjectTeams.getTeamUpdates(), teamName, new ArrayList<>());
        List<TeamsDetail> teamsDetails = (List<TeamsDetail>) result.get(0);

        existingProjectTeams.setProjectId(projectId);
        existingProjectTeams.setTeams(objectMapper.writeValueAsString(teamsDetails));
        existingProjectTeams.setTeamUpdates(objectMapper.writeValueAsString(teamUpdatesDetail));

        dbManager.save(existingProjectTeams);

        return teamsDetails.stream().map(TeamsDetail::getTeamName).toList();
    }

    private List<List<?>> manageTeamsDetail(String existingTeamDetail, String teamName, List<Long> newMembersId) throws JsonProcessingException {
        List<TeamsDetail> teamsDetails = new ArrayList<>();
        List<Long> currentTeamMembersId = new ArrayList<>();

        if (existingTeamDetail != null && !existingTeamDetail.equals("") && !existingTeamDetail.equals("[]"))
            teamsDetails = objectMapper.readValue(existingTeamDetail, new TypeReference<List<TeamsDetail>>() {});

        var existingTeam = teamsDetails.stream().filter(x -> Objects.equals(x.getTeamName(), teamName)).findFirst().orElse(null);
        if (existingTeam == null) {
            teamsDetails.add(TeamsDetail.builder()
                    .index(teamsDetails.size() + 1)
                    .teamName(teamName)
                    .membersId(newMembersId)
                    .build());

            currentTeamMembersId.addAll(newMembersId);
        } else {
            existingTeam.getMembersId().addAll(newMembersId);
            currentTeamMembersId.addAll(existingTeam.getMembersId());
        }

        return  List.of(teamsDetails, currentTeamMembersId);
    }

    private List<TeamUpdatesDetail> manageTeamUpdateDetail(String projectTeamUpdates, String teamName, List<Long> membersId) throws JsonProcessingException {
        List<TeamUpdatesDetail> teamUpdatesDetails = new ArrayList<>();
        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());

        if (projectTeamUpdates != null && !projectTeamUpdates.equals("") && !projectTeamUpdates.equals("[]"))
            teamUpdatesDetails = objectMapper.readValue(projectTeamUpdates, new TypeReference<List<TeamUpdatesDetail>>() {});

        teamUpdatesDetails.add(TeamUpdatesDetail.builder()
                .index(teamUpdatesDetails.size() + 1)
                .teamName(teamName)
                .membersId(membersId)
                .modifiedBy(currentSession.getUserDetail().getUserId())
                .status(true)
                .updatedOn(date)
                .build());

        return teamUpdatesDetails;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ProjectMembers> deleteProjectMemberService(int projectId, int projectMemberDetailId) throws Exception {
        if (projectId == 0)
            throw new Exception("Invalid project selected");

        if (projectMemberDetailId == 0)
            throw new Exception("Invalid member selected");

        var existingProjectMember = dbManager.getById(projectMemberDetailId, ProjectMembers.class);
        if (existingProjectMember == null)
            throw new Exception("member detail not found");

        java.util.Date utilDate = new java.util.Date();
        var date = new java.sql.Timestamp(utilDate.getTime());

        existingProjectMember.setActive(false);
        existingProjectMember.setLastDateOnProject(date);

        dbManager.save(existingProjectMember);

        removeAndUpdateProjectTeams(projectId, existingProjectMember.getTeam(), existingProjectMember.getEmployeeId());

        return projectRepository.getProjectMembersRepository(projectId);
    }

    private void removeAndUpdateProjectTeams(int projectId, String teamName, long employeeId) throws Exception {
        var existingProjectMember = dbManager.getById(projectId, ProjectTeams.class);
        if (existingProjectMember == null)
            throw new Exception("Team detail not found");

        List<List<?>> result = updateTeamsDetail(existingProjectMember.getTeams(), teamName, employeeId);
        List<TeamsDetail> teamsDetails = (List<TeamsDetail>) result.get(0);
        var teamUpdatesDetail = manageTeamUpdateDetail(existingProjectMember.getTeamUpdates(), teamName, (List<Long>) result.get(1));

        existingProjectMember.setTeams(objectMapper.writeValueAsString(teamsDetails));
        existingProjectMember.setTeamUpdates(objectMapper.writeValueAsString(teamUpdatesDetail));

        dbManager.save(existingProjectMember);
    }

    private List<List<?>> updateTeamsDetail(String existingTeamDetail, String teamName, long employeeId) throws Exception {
        List<TeamsDetail> teamsDetails = objectMapper.readValue(existingTeamDetail, new TypeReference<List<TeamsDetail>>() {});

        var existingTeam = teamsDetails.stream().filter(x -> Objects.equals(x.getTeamName(), teamName)).findFirst().orElse(null);
        if (existingTeam == null)
            throw new Exception("team detail not found");

        existingTeam.getMembersId().remove(employeeId);

        return  List.of(teamsDetails, existingTeam.getMembersId());
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ProjectMembers> updateDesignationAndTeamService(int projectId, ProjectMembers projectMembers) throws Exception {
        if (projectId == 0)
            throw new Exception("Invalid project selected");

        if (projectMembers.getProjectMemberDetailId() == 0)
            throw new Exception("Invalid member selected");

        if (projectMembers.getTeam() == null || projectMembers.getTeam().isEmpty())
            throw new Exception("Invalid team selected");

        if (projectMembers.getMemberType() == 0)
            throw new Exception("Invalid designation selected");

        var existingMember = dbManager.getById(projectMembers.getProjectMemberDetailId(), ProjectMembers.class);
        if (existingMember == null)
            throw new Exception("Project member not found");

        if (!Objects.equals(existingMember.getTeam(), projectMembers.getTeam())) {
            removeAndAddProjectMember(projectId, existingMember.getTeam(), projectMembers.getTeam(), existingMember.getEmployeeId());
        }

        existingMember.setTeam(projectMembers.getTeam());
        existingMember.setMemberType(projectMembers.getMemberType());

        dbManager.save(existingMember);

        return projectRepository.getProjectMembersRepository(projectId);
    }

    private void removeAndAddProjectMember(int projectId, String oldTeamName, String newTeamName, long employeeId) throws Exception {
        var existingProjectMember = dbManager.getById(projectId, ProjectTeams.class);
        if (existingProjectMember == null)
            throw new Exception("Team detail not found");

        List<List<?>> result = updateTeamsDetail(existingProjectMember.getTeams(), oldTeamName, employeeId);
        result = addMemberDetail((List<TeamsDetail>) result.get(0), newTeamName, employeeId);
        List<TeamsDetail> teamsDetails = (List<TeamsDetail>) result.get(0);

        var teamUpdatesDetail = manageTeamUpdateDetail(existingProjectMember.getTeamUpdates(), newTeamName, (List<Long>) result.get(1));

        existingProjectMember.setTeams(objectMapper.writeValueAsString(teamsDetails));
        existingProjectMember.setTeamUpdates(objectMapper.writeValueAsString(teamUpdatesDetail));

        dbManager.save(existingProjectMember);
    }

    private List<List<?>> addMemberDetail(List<TeamsDetail> teamsDetails, String teamName, long employeeId) throws Exception {
        var existingTeam = teamsDetails.stream().filter(x -> Objects.equals(x.getTeamName(), teamName)).findFirst().orElse(null);
        if (existingTeam == null)
            throw new Exception("team detail not found");

        existingTeam.getMembersId().add(employeeId);

        return  List.of(teamsDetails, existingTeam.getMembersId());
    }

    public List<ProjectAttachment> updateAttachmentNameService(int projectId, ProjectAttachment projectAttachment) throws Exception {
        if (projectId == 0)
            throw new Exception("Invalid project id");

        if (projectAttachment.getFileName() == null || projectAttachment.getFileName().isEmpty())
            throw new Exception("Invalid attachment name");

        if (projectAttachment.getIndex() == 0)
            throw new Exception("Invalid attachment index");

        Projects result = dbManager.getById(projectId, Projects.class);
        if (result == null)
            throw new Exception("Project detail not found.");

        ArrayList<ProjectAttachment> attachmentPaths = objectMapper.readValue(result.getAttachmentPath(), new TypeReference<ArrayList<ProjectAttachment>>() {
        });

        var currentAttachment = attachmentPaths.stream().filter(x -> x.getIndex() == projectAttachment.getIndex()).findFirst()
                                                .orElseThrow(() -> new Exception("Attachment path not found"));

        String documentPath = Paths.get(currentSession.getCompanyCode(), "project_" + projectId).toString();
        String newFileName = projectAttachment.getFileName();
        if (newFileName.contains("."))
            newFileName = newFileName.substring(0, newFileName.lastIndexOf('.'));

        newFileName = newFileName + "."+ currentAttachment.getFileType();
        for (ProjectAttachment attachmentPath : attachmentPaths) {
            if (Objects.equals(attachmentPath.getFileName(), newFileName))
                throw new Exception("New file name is already exist");
        }

        FileFolderDetail fileFolderDetail = FileFolderDetail.builder()
                .OldFileName(Arrays.asList(currentAttachment.getFileName()))
                .ServiceName(ApplicationConstant.EmstumFileService)
                .FolderPath(documentPath)
                .FileNewName(newFileName)
                .build();

        var status = renameFile(fileFolderDetail);
        if (status == null || status.isEmpty())
            throw new Exception("Fail to rename the fail");

        currentAttachment.setFileName(newFileName);
        attachmentPaths.forEach(x -> {
            x.setFilePath(replaceEscapeCharacter(x.getFilePath()));
        });

        result.setAttachmentPath(objectMapper.writeValueAsString(attachmentPaths));
        result.setProjectDescriptionFilePath(replaceEscapeCharacter(result.getProjectDescriptionFilePath()));
        result.setThumbnailPath(replaceEscapeCharacter(result.getThumbnailPath()));

        dbManager.save(result);

        return attachmentPaths;
    }

    private String renameFile(FileFolderDetail fileFolderDetail) throws Exception {
        MicroserviceRequest microserviceRequest = MicroserviceRequest.builder()
                .token(currentSession.getAuthorization())
                .connectionString(currentSession.getLocalConnectionString())
                .companyCode(currentSession.getCompanyCode())
                .build();
        return requestMicroservice.renameFile(microserviceRequest, fileFolderDetail);
    }
}