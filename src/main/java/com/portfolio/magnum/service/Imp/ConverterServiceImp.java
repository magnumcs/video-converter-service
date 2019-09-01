package com.portfolio.magnum.service.Imp;

import com.portfolio.magnum.domain.wrapper.S3ObjectWrapper;
import com.portfolio.magnum.domain.wrapper.VideoWrapper;
import com.portfolio.magnum.service.ConverterService;
import com.portfolio.magnum.service.S3Service;
import com.portfolio.magnum.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Service
public class ConverterServiceImp implements ConverterService {

    private static Logger logger = LoggerFactory.getLogger(ConverterServiceImp.class);

    private final VideoServiceImp videoServiceImp;
    private final S3Service s3Service;

    @Autowired
    public ConverterServiceImp(VideoServiceImp videoServiceImp, S3Service s3Service) {
        this.videoServiceImp = videoServiceImp;
        this.s3Service= s3Service;
    }

    @Override
    public S3ObjectWrapper getVideoFileConvertedFile(VideoWrapper videoWrapper) {
        try {
            File source = videoServiceImp.getFileFromMFP(videoWrapper.getFile(), videoWrapper.getFileName());
            return getVideoFileUploaded(source);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    @Override
    public S3ObjectWrapper getVideoFileConvertedFileURL(VideoWrapper videoWrapper) {
        try {
            URL website = new URL(videoWrapper.getUrl());
            File target = new File(videoWrapper.getFileName());
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(target);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            return getVideoFileUploaded(target);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    private S3ObjectWrapper getVideoFileUploaded(File source) {
        MultipartFile mpfTarget = FileUtil.convertFileToMultipartfile(source, source.getName());
        String url = s3Service.uploadFile(source.getName(), mpfTarget);
        return new S3ObjectWrapper(source.getName(), url);
    }
}
