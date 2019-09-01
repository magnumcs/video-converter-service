package com.portfolio.magnum.service.Imp;

import com.portfolio.magnum.service.VideoService;
import com.portfolio.magnum.utils.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class VideoServiceImp implements VideoService {

    @Override
    public File getFileFromMFP(MultipartFile file, String fileName) {
        return FileUtil.convertMultipartfileToFile(file, fileName);
    }
}
