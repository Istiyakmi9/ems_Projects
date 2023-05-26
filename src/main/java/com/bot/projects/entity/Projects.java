package com.bot.projects.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "project")
@Data
@NoArgsConstructor
public class Projects {
    @Id
    @Column(name = "ProjectMemberDetailId")
    int projectMemberDetailId;
    @Column(name = "ProjectId")
    int projectId;
    @Column(name = "EmployeeId")
    long employeeId;
    @Column(name = "DesignationId")
    int designationId;
    @Column(name = "FullName")
    String fullName;
    @Column(name = "Email")
    String email;
    @Column(name = "IsActive")
    boolean isActive;
    @Column(name = "AssignedOn")
    Date assignedOn;
    @Column(name = "LastDateOnProject")
    Date lastDateOnProject;
}

class ProjectDetail {
    String ProjectName;
    String ProjectDescription;

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public String getProjectDescription() {
        return ProjectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        ProjectDescription = projectDescription;
    }
}