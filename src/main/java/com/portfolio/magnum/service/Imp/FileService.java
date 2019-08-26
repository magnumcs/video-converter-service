package com.portfolio.magnum.service.Imp;

import com.google.common.io.Files;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileService {

    public byte[] getByteFromFile() throws Exception {
        File test = new ClassPathResource("/sample.mkv").getFile();
        return Files.toByteArray(test);
    }





}
