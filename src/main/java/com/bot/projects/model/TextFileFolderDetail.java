package com.bot.projects.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TextFileFolderDetail {
    @JsonProperty("FolderPath")
    String folderPath;

    @JsonProperty("OldFileName")
    String oldFileName;

    @JsonProperty("ServiceName")
    String serviceName;

    @JsonProperty("FileName")
    String fileName;

    @JsonProperty("TextDetail")
    String textDetail;
}
