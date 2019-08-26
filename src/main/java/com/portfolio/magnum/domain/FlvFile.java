package com.portfolio.magnum.domain;

public class FlvFile {
    private static FlvFile ourInstance = new FlvFile();

    private static String fileName = "target.flv";
    private static String audioCodec = "libmp3lame";
    private static Integer audioBitRate = 64000;
    private static Integer audioChannels = 1;
    private static Integer audioSamplingRate = 22050;
    private static String videoCodec = "flv";
    private static Integer videoBitRate = 160000;
    private static Integer videoFrameRate = 15;

    private static String format = "flv";

    public static FlvFile getInstance() {
        return ourInstance;
    }

    private FlvFile() {
    }

    public static String getFileName() {
        return fileName;
    }

    public static String getAudioCodec() {
        return audioCodec;
    }

    public static Integer getAudioBitRate() {
        return audioBitRate;
    }

    public static Integer getAudioChannels() {
        return audioChannels;
    }

    public static Integer getAudioSamplingRate() {
        return audioSamplingRate;
    }

    public static String getVideoCodec() {
        return videoCodec;
    }

    public static Integer getVideoBitRate() {
        return videoBitRate;
    }

    public static Integer getVideoFrameRate() {
        return videoFrameRate;
    }

    public static String getFormat() {
        return format;
    }

}
