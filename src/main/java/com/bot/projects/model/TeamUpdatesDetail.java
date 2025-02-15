package com.bot.projects.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamUpdatesDetail {
    @JsonProperty("Index")
    int index;

    @JsonProperty("TeamName")
    String teamName;

    @JsonProperty("MembersId")
    List<Long> membersId;

    @JsonProperty("ModifiedBy")
    long modifiedBy;

    @JsonProperty("Status")
    boolean status;

    @JsonProperty("UpdatedOn")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date updatedOn;
}
