package com.bot.projects.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FileFolderDetail {
    String FolderPath;
    List<String> OldFileName;
    String ServiceName;
    List<String> DeletableFiles;
    String FileNewName;
}
