package com.portfolio.magnum.domain;

public enum VideoCodecEnum {

    VC_1024(1500000L, 1024, 576),
    VC_768(1000000L, 768, 432),
    VC_640(750000L, 640, 360),
    VC_512(550000L, 512, 288),
    VC_384(240000L, 384, 216);

    private Long bitrate;
    private Integer width;
    private Integer height;

    VideoCodecEnum(Long bitrate, Integer witdh, Integer height) {
        this.bitrate = bitrate;
        this.width = witdh;
        this.height = height;
    }

    public Long getBitrate() {
        return bitrate;
    }

    public void setBitrate(Long bitrate) {
        this.bitrate = bitrate;
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
}
