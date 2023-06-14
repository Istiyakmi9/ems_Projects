package com.bot.projects.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientDetail {
    @JsonProperty("ClientId")
    Long clientId;
    @JsonProperty("ClientName")
    String clientName;
}
