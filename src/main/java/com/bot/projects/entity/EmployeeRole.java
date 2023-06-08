package com.bot.projects.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class EmployeeRole {
    @JsonProperty("RoleId")
    Long roleId;
    @JsonProperty("RoleName")
    String roleName;
    @JsonProperty("Description")
    String description;
    @JsonProperty("AccessCode")
    int accessCode;
    @JsonProperty("DepartmentId")
    int departmentId;
}
