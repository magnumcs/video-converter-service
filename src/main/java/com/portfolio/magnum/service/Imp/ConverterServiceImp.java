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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
            File target = videoServiceImp.getFLVExtensionVideo(videoWrapper.getFile(), videoWrapper.getFileName());
            return getVideoFileConverted(target);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    @Override
    public S3ObjectWrapper getVideoFileConvertedFileURL(VideoWrapper videoWrapper) {
        return null;
    }

    private S3ObjectWrapper getVideoFileConverted(File target) {
        MultipartFile mpfTarget = FileUtil.convertFileToMultipartfile(target, target.getName());
        String url = s3Service.uploadFile(target.getName(), mpfTarget);
        return new S3ObjectWrapper(target.getName(), url);
    }
}
