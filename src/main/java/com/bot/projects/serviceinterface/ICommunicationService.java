package com.bot.projects.serviceinterface;

import com.bot.projects.model.ApiResponse;
import com.bot.projects.model.FileFolderDetail;
import com.bot.projects.model.TextFileFolderDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(name = "fileConverterService", url = "${file.saver.service.url}")
public interface ICommunicationService {

    @PostMapping(value = "File/saveFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse upload(@RequestPart("files") MultipartFile[] files,
                       @RequestPart("data") String data);

    @PostMapping(value = "File/SaveAsTextFile", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse SaveAsTextFile(@RequestBody TextFileFolderDetail textFileFolderDetail,
                               @RequestHeader Map<String, String> headers);

    @PostMapping(value = "File/DeleteFiles", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse DeleteFiles(@RequestBody FileFolderDetail fileFolderDetail,
                               @RequestHeader Map<String, String> headers);

    @PostMapping(value = "File/RenameFile", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse RenameFile(@RequestBody FileFolderDetail fileFolderDetail,
                            @RequestHeader Map<String, String> headers);

}
