package com.bot.projects.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AppraisalReviewDetail {
    @JsonProperty("EmployeeId")
    Long employeeId;
    @JsonProperty("PromotedDesignation")
    int promotedDesignation;
    @JsonProperty("HikePercentage")
    BigDecimal hikePercentage;
    @JsonProperty("HikeAmount")
    BigDecimal hikeAmount;
    @JsonProperty("EstimatedSalary")
    BigDecimal estimatedSalary;
    @JsonProperty("CompanyId")
    int companyId;
    @JsonProperty("AppraisalDetailId")
    int appraisalDetailId;
    @JsonProperty("ProjectId")
    int projectId;
    @JsonProperty("AppraisalReviewId")
    long appraisalReviewId;
    @JsonProperty("PreviousSalary")
    BigDecimal previousSalary;
    @JsonProperty("AppraisalCycleStartDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date appraisalCycleStartDate;
    @JsonProperty("ReviewerId")
    long reviewerId;
    @JsonProperty("Comments")
    String comments;
    @JsonProperty("Rating")
    BigDecimal rating;
    @JsonProperty("ReactedOn")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date reactedOn;
    @JsonProperty("Status")
    int status;
}