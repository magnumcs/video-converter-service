package com.portfolio.magnum.domain.wrapper;

import com.bitmovin.api.encoding.codecConfigurations.enums.ConfigType;

public class AACAudioProfile extends MediaProfile{

    private Float rate;
    private String language;

    public AACAudioProfile(long bitrate, Float rate, String language) {
        super(bitrate);
        this.rate = rate;
        this.language = language;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public ConfigType getCodecType() {
        return ConfigType.AAC;
    }
}
