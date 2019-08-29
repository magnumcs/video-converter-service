package com.portfolio.magnum.service.Imp;

import com.portfolio.magnum.domain.FlvFile;
import com.portfolio.magnum.service.VideoService;
import com.portfolio.magnum.utils.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.*;

import java.io.File;

@Service
public class VideoServiceImp implements VideoService {

    @Override
    public File getFLVExtensionVideo(MultipartFile file, String fileName) throws Exception{
        File source = FileUtil.convertMultipartfileToFile(file, fileName);

        File target = new File(FlvFile.getFileName());
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec(FlvFile.getAudioCodec());
        audio.setBitRate(FlvFile.getAudioBitRate());
        audio.setChannels(FlvFile.getAudioChannels());
        audio.setSamplingRate(FlvFile.getAudioSamplingRate());
        VideoAttributes video = new VideoAttributes();
        video.setCodec(FlvFile.getVideoCodec());
        video.setBitRate(FlvFile.getVideoBitRate());
        video.setFrameRate(FlvFile.getVideoFrameRate());
//		video.setSize(new VideoSize(1280, 720));
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat(FlvFile.getFormat());
        attrs.setAudioAttributes(audio);
        attrs.setVideoAttributes(video);
        Encoder encoder = new Encoder();
        encoder.encode(new MultimediaObject(source), target, attrs);
        return target;
    }
}
