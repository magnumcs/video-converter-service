package com.portfolio.magnum.service.Imp;

import com.portfolio.magnum.domain.FlvFile;
import com.portfolio.magnum.service.VideoService;
import com.portfolio.magnum.utils.FileUtil;
import org.springframework.stereotype.Service;
import ws.schild.jave.*;

import java.io.File;

@Service
public class VideoServiceImp implements VideoService {

    @Override
    public byte[] getByteFLVVideo(byte[] bSource, String extension) throws Exception{
        File source = FileUtil.getFileFromBytes(bSource, extension);

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
        source.delete();
        return null;
    }
}
