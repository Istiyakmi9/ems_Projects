package com.bot.projects.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectAttachment {
    @JsonProperty("Index")
    int index;

    @JsonProperty("FilePath")
    String filePath;

    @JsonProperty("FileName")
    String fileName;

    @JsonProperty("FileType")
    String fileType;

    @JsonProperty("FileSize")
    String fileSize;

    @JsonProperty("UploadedDate")
    Date uploadedDate;
}
