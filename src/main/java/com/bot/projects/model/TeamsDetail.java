package com.bot.projects.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamsDetail {
    @JsonProperty("Index")
    int index;

    @JsonProperty("TeamName")
    String teamName;

    @JsonProperty("MembersId")
    List<Long> membersId;
}
