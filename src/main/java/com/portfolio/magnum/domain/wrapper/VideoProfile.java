package com.portfolio.magnum.domain.wrapper;

import com.bitmovin.api.encoding.codecConfigurations.enums.ConfigType;
import com.bitmovin.api.encoding.codecConfigurations.enums.ProfileH264;

public class VideoProfile extends MediaProfile {

    private int width;
    private int height;
    private float fps;

    public VideoProfile(int width, int height, long bitrate, float fps)
    {
        super(bitrate);
        this.width = width;
        this.height = height;
        this.fps = fps;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Float getFps() {
        return fps;
    }

    public void setFps(Float fps) {
        this.fps = fps;
    }

    @Override
    public ConfigType getCodecType() {
        return ConfigType.H264;
    }

}
