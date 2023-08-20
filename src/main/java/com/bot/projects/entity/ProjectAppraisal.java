package com.bot.projects.entity;

import com.bot.projects.db.annotations.Column;
import com.bot.projects.db.annotations.Id;
import com.bot.projects.db.annotations.Table;
import com.bot.projects.db.annotations.Transient;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Table(name = "project_appraisal")
@Data
@NoArgsConstructor
public class ProjectAppraisal {
    @Id
    @Column(name = "ProjectAppraisalId")
    @JsonProperty("ProjectAppraisalId")
    int projectAppraisalId;
    @Column(name = "ProjectId")
    @JsonProperty("ProjectId")
    int projectId;
    @Column(name = "FromDate")
    @JsonProperty("FromDate")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date fromDate;
    @Column(name = "ToDate")
    @JsonProperty("ToDate")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date toDate;
    @Column(name = "ProjectAppraisalBudget")
    @JsonProperty("ProjectAppraisalBudget")
    double projectAppraisalBudget;
    @Column(name = "CreatedBy")
    @JsonProperty("CreatedBy")
    Long createdBy;
    @Column(name = "CreatedOn")
    @JsonProperty("CreatedOn")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date createdOn;
    @Column(name = "UpdatedBy")
    @JsonProperty("UpdatedBy")
    Long updatedBy;
    @Column(name = "UpdatedOn")
    @JsonProperty("UpdatedOn")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date updatedOn;
    @Transient
    @JsonProperty("MembersCount")
    int membersCount;
}
