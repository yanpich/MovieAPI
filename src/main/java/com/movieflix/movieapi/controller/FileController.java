package com.movieflix.movieapi.controller;


import com.movieflix.movieapi.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file/")
public class FileController {

    private final FileService fileService;
    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
    @Value("${project.poster}")
    private String path;
    @PostMapping("upload")
    public ResponseEntity<String> upload(@RequestPart MultipartFile file) throws IOException {
        String uploadFilename = fileService.uploadFile(path, file);
        return ResponseEntity.ok("File uploaded" + uploadFilename);
    }

    @GetMapping("{fileName}")
    public void serveFileHeader(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        InputStream resourceFile = fileService.getResource(path,fileName);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resourceFile, response.getOutputStream());
    }
}



