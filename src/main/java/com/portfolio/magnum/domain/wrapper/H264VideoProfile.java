package com.portfolio.magnum.domain.wrapper;

import com.bitmovin.api.encoding.codecConfigurations.enums.ConfigType;
import com.bitmovin.api.encoding.codecConfigurations.enums.ProfileH264;

public class H264VideoProfile extends VideoProfile {

    public H264VideoProfile(long bitrate, Integer width, Integer height, Float fps, ProfileH264 profileH264) {
        super(bitrate, width, height, fps, profileH264);
    }

    @Override
    public ConfigType getCodecType()
    {
        return ConfigType.H264;
    }
}
