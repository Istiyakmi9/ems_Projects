package com.bot.projects.model;

import com.bot.projects.entity.ProjectMembers;
import com.bot.projects.entity.Projects;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ProjectDetail extends Projects {
    @JsonProperty("ProjectMemberDetailId")
    int projectMemberDetailId;
    @JsonProperty("EmployeeId")
    long employeeId;
    @JsonProperty("DesignationId")
    int designationId;
    @JsonProperty("FullName")
    String fullName;
    @JsonProperty("Email")
    String email;
    @JsonProperty("IsActive")
    boolean isActive;
    @JsonProperty("AssignedOn")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date assignedOn;
    @JsonProperty("LastDateOnProject")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date lastDateOnProject;
    @JsonProperty("DesignationName")
    String designationName;
    @JsonProperty("ProjectManagerId")
    long projectManagerId;
    @JsonProperty("Team")
    String team;
    @JsonProperty("MemberType")
    int memberType;
    @JsonProperty("TeamMembers")
    List<ProjectMembers> teamMembers;
    @JsonProperty("ExprienceInYear")
    BigDecimal exprienceInYear;

    @JsonProperty("CompanyId")
    int companyId;

    public BigDecimal getExprienceInYear() {
        return exprienceInYear;
    }

    public void setExprienceInYear(BigDecimal exprienceInYear) {
        this.exprienceInYear = exprienceInYear;
    }

    public int getProjectMemberDetailId() {
        return projectMemberDetailId;
    }

    public void setProjectMemberDetailId(int projectMemberDetailId) {
        this.projectMemberDetailId = projectMemberDetailId;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public int getDesignationId() {
        return designationId;
    }

    public void setDesignationId(int designationId) {
        this.designationId = designationId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getAssignedOn() {
        return assignedOn;
    }

    public void setAssignedOn(Date assignedOn) {
        this.assignedOn = assignedOn;
    }

    public Date getLastDateOnProject() {
        return lastDateOnProject;
    }

    public void setLastDateOnProject(Date lastDateOnProject) {
        this.lastDateOnProject = lastDateOnProject;
    }

    public String getDesignationName() {
        return designationName;
    }

    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }

    public long getProjectManagerId() {
        return projectManagerId;
    }

    public void setProjectManagerId(long projectManagerId) {
        this.projectManagerId = projectManagerId;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }

    public List<ProjectMembers> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<ProjectMembers> teamMembers) {
        this.teamMembers = teamMembers;
    }

    //    @Transient
//    @JsonProperty("ProjectId")
//    int projectId;
//    @JsonProperty("ProjectStartedOn")
//    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
//    Date projectStartedOn;
//    @JsonProperty("ProjectEndedOn")
//    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
//    Date projectEndedOn;
//    @JsonProperty("ProjectName")
//    String projectName;
//    @JsonProperty("ProjectDescription")
//    String projectDescription;
}