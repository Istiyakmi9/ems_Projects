package com.bot.projects.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Files {
    @JsonProperty("FilePath")
    String filePath;
    @JsonProperty("FileName")
    String fileName;
    @JsonProperty("FileExtension")
    String fileExtension;

    @JsonProperty("FileSize")
    String fileSize;
}
