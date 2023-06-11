package com.bot.projects.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
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
    @JsonProperty("ProjectMemberDetailId")
    int projectMemberDetailId;
    @Column(name = "ProjectId")
    @JsonProperty("ProjectId")
    int projectId;
    @Column(name = "EmployeeId")
    @JsonProperty("EmployeeId")
    long employeeId;
    @Column(name = "DesignationId")
    @JsonProperty("DesignationId")
    int designationId;
    @Column(name = "FullName")
    @JsonProperty("FullName")
    String fullName;
    @Column(name = "Email")
    @JsonProperty("Email")
    String email;
    @Column(name = "IsActive")
    @JsonProperty("IsActive")
    boolean isActive;
    @Column(name = "AssignedOn")
    @JsonProperty("AssignedOn")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date assignedOn;
    @Column(name = "LastDateOnProject")
    @JsonProperty("LastDateOnProject")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date lastDateOnProject;
    @Transient
    @JsonProperty("ProjectName")
    String projectName;
    @Transient
    @JsonProperty("ProjectDescription")
    String projectDescription;
    @Transient
    @JsonProperty("CTC")
    float cTC;
    @Transient
    @JsonProperty("DesignationName")
    String designationName;
    @JsonProperty("MemberType")
    int memberType;
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