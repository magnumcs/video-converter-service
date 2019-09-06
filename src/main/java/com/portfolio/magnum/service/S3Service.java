package com.portfolio.magnum.service;

import com.amazonaws.services.s3.model.S3Object;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    String urlFileConverted(String keyName, MultipartFile file);

    S3Object uploadFile(String keyName, MultipartFile file);

}
