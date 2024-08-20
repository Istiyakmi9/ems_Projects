package com.bot.projects.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WorkShift {
    @JsonProperty("WorkShiftId")
    int workShiftId;

    @JsonProperty("OfficeTime")
    String officeTime;

    @JsonProperty("Duration")
    int duration;

    @JsonProperty("LunchDuration")
    int lunchDuration;
}
