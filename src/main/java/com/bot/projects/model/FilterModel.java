package com.bot.projects.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class FilterModel {
    @JsonProperty("IsActive")
    boolean isActive;

    @JsonProperty("SearchString")
    String searchString ;

    @JsonProperty("PageIndex")
    int pageIndex;

    @JsonProperty("PageSize")
    int pageSize;

    @JsonProperty("SortBy")
    String sortBy;

    @JsonProperty("ObjectiveCatagoryType")
    String objectiveCatagoryType;

    @JsonProperty("TypeDescription")
    String TypeDescription;

    @JsonProperty("RolesId")
    String rolesId;

    int companyId;

    int offsetIndex;

    Long employeeId;

    public FilterModel(boolean isActive, String searchString, int pageIndex, int pageSize, String sortBy, int companyId, int offsetIndex, Long employeeId) {
        this.isActive = isActive;
        this.searchString = searchString;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.companyId = companyId;
        this.offsetIndex = offsetIndex;
        this.employeeId = employeeId;
    }

    public FilterModel() {}
}
