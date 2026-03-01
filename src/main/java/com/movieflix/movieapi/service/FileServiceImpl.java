package com.movieflix.movieapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl  implements FileService {
    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        // get name of file
        String fileName = file.getOriginalFilename();

        // to get tile path
        String filePath = path + File.separator + fileName;

        // create file object
        File f = new File(path);
        if(!f.exists()){
            f.mkdirs();
        }
        // copy the file or upload
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
    @Override
    public InputStream getResource(String path, String name) throws FileNotFoundException {
        String fileName = path + File.separator + name;
        return new FileInputStream(fileName);
    }
}
