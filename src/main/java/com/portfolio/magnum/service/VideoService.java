package com.portfolio.magnum.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface VideoService {

    File getFileFromMFP(MultipartFile file) throws Exception;

}
