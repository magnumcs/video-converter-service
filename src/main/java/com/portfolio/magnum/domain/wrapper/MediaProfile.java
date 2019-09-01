package com.portfolio.magnum.domain.wrapper;

import com.bitmovin.api.encoding.codecConfigurations.enums.ConfigType;
import com.bitmovin.api.encoding.encodings.muxing.Muxing;
import com.bitmovin.api.encoding.encodings.streams.Stream;

import java.util.ArrayList;
import java.util.List;

public abstract class MediaProfile {

    private long bitrate;
    private Stream stream;
    private List<Muxing> muxings;

    public MediaProfile(long bitrate) {
        this.bitrate = bitrate;
        this.muxings = new ArrayList<>();
    }

    public abstract ConfigType getCodecType();

    public long getBitrate() {
        return bitrate;
    }

    public void setBitrate(long bitrate) {
        this.bitrate = bitrate;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public List<Muxing> getMuxings() {
        return muxings;
    }

    public void setMuxings(List<Muxing> muxings) {
        this.muxings = muxings;
    }
}
