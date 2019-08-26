package com.portfolio.magnum.domain.wrapper;

import java.io.Serializable;

public class ByteFileWrapper implements Serializable {

    private byte[] byteResponse;

    public ByteFileWrapper(byte[] byteResponse) {
        this.byteResponse = byteResponse;
    }

    public byte[] getByteResponse() {
        return byteResponse;
    }

    public void setByteResponse(byte[] byteResponse) {
        this.byteResponse = byteResponse;
    }
}
