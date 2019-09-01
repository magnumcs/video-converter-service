package com.portfolio.magnum.domain.wrapper;

import com.bitmovin.api.encoding.codecConfigurations.enums.ConfigType;
import com.bitmovin.api.encoding.codecConfigurations.enums.ProfileH264;

public class VideoProfile extends MediaProfile {

    private Integer width;
    private Integer height;
    private Float fps;
    private ProfileH264 profileH264;

    public VideoProfile(long bitrate, Integer width, Integer height, Float fps, ProfileH264 profileH264) {
        super(bitrate);
        this.width = width;
        this.height = height;
        this.fps = fps;
        this.profileH264 = profileH264;
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

    public ProfileH264 getProfileH264() {
        return profileH264;
    }

    public void setProfileH264(ProfileH264 profileH264) {
        this.profileH264 = profileH264;
    }

    @Override
    public ConfigType getCodecType() {
        return ConfigType.AAC;
    }

}
