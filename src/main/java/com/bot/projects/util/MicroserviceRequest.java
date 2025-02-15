package com.bot.projects.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MicroserviceRequest {
    String payload;
    String token;
    String companyCode;
    MultipartFile[] fileCollections;
    String connectionString;
}