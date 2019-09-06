package com.portfolio.magnum.service.Imp;

import com.bitmovin.api.BitmovinApi;
import com.bitmovin.api.encoding.InputStream;
import com.bitmovin.api.encoding.codecConfigurations.AACAudioConfig;
import com.bitmovin.api.encoding.codecConfigurations.H264VideoConfiguration;
import com.bitmovin.api.encoding.codecConfigurations.enums.ProfileH264;
import com.bitmovin.api.encoding.encodings.Encoding;
import com.bitmovin.api.encoding.encodings.streams.Stream;
import com.bitmovin.api.encoding.enums.CloudRegion;
import com.bitmovin.api.encoding.enums.StreamSelectionMode;
import com.bitmovin.api.encoding.inputs.Input;
import com.bitmovin.api.encoding.inputs.S3Input;
import com.bitmovin.api.encoding.outputs.S3Output;
import com.bitmovin.api.exceptions.BitmovinApiException;
import com.bitmovin.api.http.RestException;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.portfolio.magnum.domain.wrapper.AACAudioProfile;
import com.portfolio.magnum.domain.wrapper.H264VideoProfile;
import com.portfolio.magnum.domain.wrapper.VideoProfile;
import com.portfolio.magnum.service.BitmovinService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class BitmovinServiceImp implements BitmovinService {
    @Value("${storage.aws.access_key_id}")
    private String awsId;

    @Value("${storage.aws.secret_access_key}")
    private String awsKey;

    @Value("${spring.api.key}")
    private String apiKey;

    @Value("${storage.s3.bucket}")
    private String bucket;

    private static final CloudRegion CLOUD_REGION = CloudRegion.AWS_US_EAST_2;

    public static final VideoProfile[] VIDEO_ENCODING_PROFILES = new VideoProfile[] {
            new H264VideoProfile(640, 360, 1200, 30.0f, ProfileH264.MAIN),
            new H264VideoProfile(854, 480, 1750, 30.0f, ProfileH264.MAIN),
            new H264VideoProfile(1280, 720, 2350, 30.0f, ProfileH264.MAIN),
    };

    public static final AACAudioProfile[] AUDIO_ENCODING_PROFILES = new AACAudioProfile[] {
            new AACAudioProfile(128, 48000f, "en"),
    };

    public BitmovinServiceImp() {
    }

    public BitmovinApi instanceBitmovin() throws IOException {
        return new BitmovinApi(apiKey);
    }

    @Override
    public S3Input createInput() throws URISyntaxException, BitmovinApiException, UnirestException, IOException {
        S3Input input = new S3Input();
        input.setAccessKey(awsId);
        input.setSecretKey(awsKey);
        input.setBucketName(bucket);
        return instanceBitmovin().input.s3.create(input);
    }

    @Override
    public S3Output createOutput() throws IOException, BitmovinApiException, UnirestException, URISyntaxException {
        S3Output output = new S3Output();
        output.setAccessKey(awsId);
        output.setSecretKey(awsKey);
        output.setBucketName(bucket);
        return instanceBitmovin().output.s3.create(output);
    }

    @Override
    public Encoding createEncoding() throws IOException, BitmovinApiException, UnirestException, URISyntaxException {
        Encoding encoding = new Encoding();
        encoding.setName("Conversão");
        encoding.setCloudRegion(CLOUD_REGION);
        return instanceBitmovin().encoding.create(encoding);
    }

    @Override
    public InputStream setupStreamVideo(Input input, String inputPath) throws BitmovinApiException, UnirestException, IOException, URISyntaxException {
        InputStream inputStreamVideo = new InputStream();
        inputStreamVideo.setInputPath(inputPath);
        inputStreamVideo.setInputId(input.getId());
        inputStreamVideo.setSelectionMode(StreamSelectionMode.AUTO);
        inputStreamVideo.setPosition(0);
        return inputStreamVideo;
    }

    @Override
    public InputStream setupStreamAudio(Input input, String inputPath) throws BitmovinApiException, UnirestException, IOException, URISyntaxException {
        InputStream inputStreamAudio = new InputStream();
        inputStreamAudio.setInputPath(inputPath);
        inputStreamAudio.setInputId(input.getId());
        inputStreamAudio.setSelectionMode(StreamSelectionMode.AUTO);
        inputStreamAudio.setPosition(0);
        return inputStreamAudio;
    }



}
