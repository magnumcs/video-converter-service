package com.portfolio.magnum.domain.wrapper;

import com.bitmovin.api.encoding.codecConfigurations.enums.ConfigType;
import com.bitmovin.api.encoding.codecConfigurations.enums.ProfileH264;

public class H264VideoProfile extends VideoProfile {

    private ProfileH264 profile;

    public H264VideoProfile(int width, int height, long bitrate, float fps, ProfileH264 profile)
    {
        super(width, height, bitrate, fps);
        this.profile = profile;
    }

    public ProfileH264 getProfile() {
        return profile;
    }

    public void setProfile(ProfileH264 profile) {
        this.profile = profile;
    }

    @Override
    public ConfigType getCodecType()
    {
        return ConfigType.H264;
    }
}
