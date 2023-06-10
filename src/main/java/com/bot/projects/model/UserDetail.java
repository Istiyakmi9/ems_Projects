package com.bot.projects.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDetail {
    @JsonProperty("EmployeeId")
    Long employeeId;
    @JsonProperty("UserId")
    Long userId;
    @JsonProperty("OrganizationId")
    int organizationId;
    @JsonProperty("CompanyId")
    int companyId;
    @JsonProperty("FirstName")
    String firstName;
    @JsonProperty("LastName")
    String lastName;
    @JsonProperty("FullName")
    String fullName;
    @JsonProperty("ManagerName")
    String managerName;
    @JsonProperty("Mobile")
    String mobile;
    @JsonProperty("Email")
    String email;
    @JsonProperty("RoleId")
    int roleId;
    @JsonProperty("Password")
    String password;
    @JsonProperty("ReportingManagerId")
    Long reportingManagerId;
    @JsonProperty("AdminId")
    int adminId;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getReportingManagerId() {
        return reportingManagerId;
    }

    public void setReportingManagerId(Long reportingManagerId) {
        this.reportingManagerId = reportingManagerId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public UserDetail(Long employeeId, Long userId, int organizationId, int companyId, String firstName, String lastName, String fullName, String managerName, String mobile, String email, int roleId, String password, Long reportingManagerId, int adminId) {
        this.employeeId = employeeId;
        this.userId = userId;
        this.organizationId = organizationId;
        this.companyId = companyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.managerName = managerName;
        this.mobile = mobile;
        this.email = email;
        this.roleId = roleId;
        this.password = password;
        this.reportingManagerId = reportingManagerId;
        this.adminId = adminId;
    }

    public UserDetail() {}

    @Override
    public String toString() {
        return "UserDetail{" +
                "employeeId=" + employeeId +
                ", userId=" + userId +
                ", organizationId=" + organizationId +
                ", companyId=" + companyId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", managerName='" + managerName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", roleId=" + roleId +
                ", password='" + password + '\'' +
                ", reportingManagerId=" + reportingManagerId +
                ", adminId=" + adminId +
                '}';
    }
}
