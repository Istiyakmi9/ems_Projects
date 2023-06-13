package com.bot.projects.service;

import com.bot.projects.entity.EmployeeRole;
import com.bot.projects.entity.ProjectAppraisal;
import com.bot.projects.entity.ProjectMembers;
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
        Projects result;
        if (projects.getProjectId() == 0) {
            Projects lastProjectRecord = projectRepository.getLastProjectRecordId();
            if(lastProjectRecord != null) {
                projects.setProjectId(lastProjectRecord.getProjectId() + 1);
            } else {
                projects.setProjectId(1);
            }

            result = projectRepository.save(projects);
        } else {
            var data = projectRepository.findById(projects.getProjectId());
            if (data.isPresent()) {
                result = data.get();
            } else {
                result = null;
                throw new Exception("Project does't exist.");
            }
        }

        int id = 0;
        ProjectMembers projectMember = projectMemberRepository.getLastProjectMembersRecordId();
        List<ProjectMembers> projectMembers = projects.getTeamMembers();
        if (projectMember != null){
            id = projectMember.getProjectMemberDetailId();
        }

        int i = 0;
        while(i < projectMembers.size()) {
            projectMembers.get(i).setProjectId(result.getProjectId());
            projectMembers.get(i).setProjectMemberDetailId(++id);
            if(projectMembers.get(i).getTeam() == null || projectMembers.get(i).getTeam().isEmpty()) {
                projectMembers.get(i).setTeam("CORE");
            }
            i++;
        }

        List<ProjectMembers> resultSet = projectMemberRepository.saveAll(projectMembers);

        result.setTeamMembers(resultSet);
        return result;
    }
}
