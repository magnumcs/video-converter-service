package com.portfolio.magnum.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    void uploadFile(String keyName, MultipartFile file);

}
