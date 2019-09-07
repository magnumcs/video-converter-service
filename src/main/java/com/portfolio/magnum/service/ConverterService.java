package com.portfolio.magnum.service;

import com.bitmovin.api.exceptions.BitmovinApiException;
import com.bitmovin.api.http.RestException;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.portfolio.magnum.domain.wrapper.S3ObjectWrapper;
import com.portfolio.magnum.domain.wrapper.VideoWrapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;

public interface ConverterService {

    S3ObjectWrapper getVideoFileConvertedFile(MultipartFile file) throws BitmovinApiException, UnirestException, IOException, URISyntaxException, RestException, InterruptedException;

    S3ObjectWrapper getVideoFileConvertedFileURL(VideoWrapper videoWrapper) throws InterruptedException;

}
