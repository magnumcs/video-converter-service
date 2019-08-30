package com.portfolio.magnum.domain.wrapper;

import java.io.Serializable;

public class S3ObjectWrapper implements Serializable {

    private String key;
    private String url;

    public S3ObjectWrapper(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
