package com.portfolio.magnum.service;

import com.bitmovin.api.encoding.InputStream;
import com.bitmovin.api.encoding.encodings.Encoding;
import com.bitmovin.api.encoding.inputs.Input;
import com.bitmovin.api.encoding.inputs.S3Input;
import com.bitmovin.api.encoding.outputs.S3Output;
import com.bitmovin.api.exceptions.BitmovinApiException;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface BitmovinService {

    S3Input createInput() throws URISyntaxException, BitmovinApiException, UnirestException, IOException;
    S3Output createOutput() throws IOException, BitmovinApiException, UnirestException, URISyntaxException;
    Encoding createEncoding() throws IOException, BitmovinApiException, UnirestException, URISyntaxException;
    InputStream setupStreamVideo(Input input, String inputPath) throws BitmovinApiException, UnirestException, IOException, URISyntaxException;
    InputStream setupStreamAudio(Input input, String inputPath) throws BitmovinApiException, UnirestException, IOException, URISyntaxException;

}
