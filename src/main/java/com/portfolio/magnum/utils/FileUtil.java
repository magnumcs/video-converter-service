package com.portfolio.magnum.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtil {

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {
    }

    public static File convertMultipartfileToFile(MultipartFile multipartFile, String fileName) {
        File source = new File(fileName);
        try {
            multipartFile.transferTo(source);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return source;
    }

    public static MultipartFile convertFileToMultipartfile(File file, String fileName) {
        MultipartFile mpfTarget = null;
        try {
            mpfTarget = new MockMultipartFile(fileName, new FileInputStream(file));
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return mpfTarget;
    }

}
