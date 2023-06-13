package com.bot.projects.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity(name = "project_members_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMembers {
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
    @Column(name = "Grade")
    @JsonProperty("Grade")
    String grade;
    @Column(name = "MemberType")
    @JsonProperty("MemberType")
    int memberType;
    @Column(name = "AssignedOn")
    @JsonProperty("AssignedOn")
    Date assignedOn;
    @Column(name = "LastDateOnProject")
    @JsonProperty("LastDateOnProject")
    Date lastDateOnProject;
    @Column(name = "Team")
    @JsonProperty("Team")
    String team;
    @Column(name = "ProjectManagerId")
    @JsonProperty("ProjectManagerId")
    long projectManagerId;
}
