package com.portfolio.magnum.service;

import com.portfolio.magnum.domain.wrapper.S3ObjectWrapper;
import com.portfolio.magnum.domain.wrapper.VideoWrapper;

public interface ConverterService {

    S3ObjectWrapper getVideoFileConvertedFile(VideoWrapper videoWrapper);

    S3ObjectWrapper getVideoFileConvertedFileURL(VideoWrapper videoWrapper);

}
