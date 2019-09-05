package com.portfolio.magnum.service;

import com.portfolio.magnum.domain.wrapper.S3ObjectWrapper;
import com.portfolio.magnum.domain.wrapper.VideoWrapper;
import org.springframework.web.multipart.MultipartFile;

public interface ConverterService {

    S3ObjectWrapper getVideoFileConvertedFile(MultipartFile file);

    S3ObjectWrapper getVideoFileConvertedFileURL(VideoWrapper videoWrapper);

}
