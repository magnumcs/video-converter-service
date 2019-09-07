package com.portfolio.magnum.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    String urlFileConverted(String keyName);

    String uploadFile(String keyName, MultipartFile file);

}
