package com.portfolio.magnum.domain;

public enum AudioCodecEnum {

    ACC(128000L, 48000f, "en");

    private Long bitraate;
    private Float rate;
    private String language;

    AudioCodecEnum(Long bitraate, Float rate, String language) {
        this.bitraate = bitraate;
        this.rate = rate;
        this.language = language;
    }

    public Long getBitraate() {
        return bitraate;
    }

    public void setBitraate(Long bitraate) {
        this.bitraate = bitraate;
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
}
