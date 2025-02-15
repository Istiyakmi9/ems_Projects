package com.bot.projects.entity;

import com.bot.projects.db.annotations.Column;
import com.bot.projects.db.annotations.Id;
import com.bot.projects.db.annotations.Table;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "project_teams")
public class ProjectTeams {
    @Id
    @Column(name = "ProjectId")
    @JsonProperty("ProjectId")
    int projectId;

    @Column(name = "Teams")
    @JsonProperty("Teams")
    String teams;

    @Column(name = "TeamUpdates")
    @JsonProperty("TeamUpdates")
    String teamUpdates;
}
