package com.bot.projects.model;

import com.bot.projects.db.annotations.Column;
import com.bot.projects.db.annotations.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrgHierarchyModel {

    @JsonProperty("RoleId")
    @Column(name = "RoleId")
    @Id
    Integer roleId;
    @JsonProperty("ParentNode")
    @Column(name = "ParentNode")
    Integer parentNode;
    @JsonProperty("RoleName")
    @Column(name = "RoleName")
    String roleName;
    @JsonProperty("EmployeeId")
    @Column(name = "EmployeeId")
    Long employeeId;
    @JsonProperty("Email")
    @Column(name = "Email")
    String email;
    @JsonProperty("ImageUrl")
    @Column(name = "ImageUrl")
    String imageUrl;

    @JsonProperty("CompanyId")
    @Column(name = "CompanyId")
    String companyId;

    @JsonProperty("IsActive")
    @Column(name = "IsActive")
    Boolean isActive;

    @JsonProperty("IsDepartment")
    @Column(name = "IsDepartment")
    Boolean isDepartment;
}
