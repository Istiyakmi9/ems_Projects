package com.bot.projects.entity;

import com.bot.projects.db.annotations.Column;
import com.bot.projects.db.annotations.Id;
import com.bot.projects.db.annotations.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Table(name = "project_members_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMembers {
    @Id()
    @Column(name = "ProjectMemberDetailId")
    @JsonProperty("ProjectMemberDetailId")
    int projectMemberDetailId;
    @Column(name = "ProjectId")
    @JsonProperty("ProjectId")
    int projectId;
    @Column(name = "EmployeeId")
    @JsonProperty("EmployeeId")
    long employeeId;
    @Column(name = "FullName")
    @JsonProperty("FullName")
    String fullName;
    @Column(name = "Email")
    @JsonProperty("Email")
    String email;
    @Column(name = "IsActive")
    @JsonProperty("IsActive")
    boolean isActive;
    @Column(name = "MemberType")
    @JsonProperty("MemberType")
    int memberType;
    @Column(name = "AssignedOn")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("AssignedOn")
    Date assignedOn;
    @Column(name = "LastDateOnProject")
    @JsonProperty("LastDateOnProject")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date lastDateOnProject;
    @Column(name = "Team")
    @JsonProperty("Team")
    String team;
    @Column(name = "ProjectManagerId")
    @JsonProperty("ProjectManagerId")
    long projectManagerId;
}
