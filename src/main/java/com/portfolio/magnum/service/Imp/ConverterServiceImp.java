package com.portfolio.magnum.service.Imp;

import com.bitmovin.api.AbstractApiResponse;
import com.bitmovin.api.encoding.AclEntry;
import com.bitmovin.api.encoding.AclPermission;
import com.bitmovin.api.encoding.EncodingOutput;
import com.bitmovin.api.encoding.InputStream;
import com.bitmovin.api.encoding.codecConfigurations.AACAudioConfig;
import com.bitmovin.api.encoding.codecConfigurations.H264VideoConfiguration;
import com.bitmovin.api.encoding.codecConfigurations.VideoConfiguration;
import com.bitmovin.api.encoding.codecConfigurations.enums.ConfigType;
import com.bitmovin.api.encoding.encodings.Encoding;
import com.bitmovin.api.encoding.encodings.muxing.Muxing;
import com.bitmovin.api.encoding.encodings.muxing.MuxingStream;
import com.bitmovin.api.encoding.encodings.muxing.TSMuxing;
import com.bitmovin.api.encoding.encodings.muxing.enums.MuxingType;
import com.bitmovin.api.encoding.encodings.streams.Stream;
import com.bitmovin.api.encoding.inputs.Input;
import com.bitmovin.api.encoding.manifest.hls.HlsManifest;
import com.bitmovin.api.encoding.manifest.hls.MediaInfo;
import com.bitmovin.api.encoding.manifest.hls.MediaInfoType;
import com.bitmovin.api.encoding.manifest.hls.StreamInfo;
import com.bitmovin.api.encoding.outputs.Output;
import com.bitmovin.api.encoding.status.Task;
import com.bitmovin.api.enums.Status;
import com.bitmovin.api.exceptions.BitmovinApiException;
import com.bitmovin.api.http.RestException;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.portfolio.magnum.config.BitmovinConfig;
import com.portfolio.magnum.domain.wrapper.*;
import com.portfolio.magnum.service.ConverterService;
import com.portfolio.magnum.utils.FileUtil;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ConverterServiceImp implements ConverterService {

    private static Logger logger = LoggerFactory.getLogger(ConverterServiceImp.class);

    private final S3ServiceImp s3Service;

    private final BitmovinServiceImp bitmovinServiceImp;

    private final BitmovinConfig bitmovinConfig;

    private static String S3_OUTPUT_PATH = "output/";

    private static final double MUXING_SEGMENT_DURATION = 4.0;

    private String fileName;

    @Autowired
    public ConverterServiceImp(S3ServiceImp s3Service, BitmovinServiceImp bitmovinServiceImp, BitmovinConfig bitmovinConfig) {
        this.s3Service= s3Service;
        this.bitmovinServiceImp= bitmovinServiceImp;
        this.bitmovinConfig = bitmovinConfig;
    }

    @Override
    public S3ObjectWrapper getVideoFileConvertedFile(MultipartFile file) throws BitmovinApiException, UnirestException, IOException, URISyntaxException, RestException, InterruptedException {
        String filePath = getVideoFileUploaded(file);
        this.fileName = filePath.substring(filePath.lastIndexOf('/')+1);
        Encoding encoding = bitmovinServiceImp.createEncoding();
        Input input = bitmovinServiceImp.createInput();
        Output output = bitmovinServiceImp.createOutput();
        InputStream inputStreamVideo = bitmovinServiceImp.setupStreamVideo(input, this.fileName);
        InputStream inputStreamAudio = bitmovinServiceImp.setupStreamAudio(input, this.fileName);

        for (VideoProfile videoProfile : BitmovinServiceImp.VIDEO_ENCODING_PROFILES) {
            VideoConfiguration videoConfig = createVideoConfiguration(videoProfile);
            Stream videoStream = createStream(encoding, inputStreamVideo, videoConfig.getId());
            videoProfile.setStream(videoStream);
            for (MuxingType type : BitmovinServiceImp.MUXING_TYPES) {
                Muxing muxing = this.createMuxing(type, "video/%d_%s_%s", encoding, output, videoProfile, videoStream);
                videoProfile.getMuxings().add(muxing);
            }
        }

        for (AACAudioProfile audioProfile : BitmovinServiceImp.AUDIO_ENCODING_PROFILES) {
            AACAudioConfig audioConfig = createAACAudioConfig(audioProfile);
            Stream audioStream = createStream(encoding, inputStreamAudio, audioConfig.getId());
            audioProfile.setStream(audioStream);
            for (MuxingType type : BitmovinServiceImp.MUXING_TYPES) {
                Muxing muxing = this.createMuxing(type, "audio/%d_%s_%s", encoding, output, audioProfile, audioStream);
                audioProfile.getMuxings().add(muxing);
            }
        }

        bitmovinConfig.instanceBitmovin().encoding.start(encoding);

        Assert.assertTrue(waitUntilFinished(encoding));

        HlsManifest manifest = createHlsManifest(encoding, output);

        bitmovinConfig.instanceBitmovin().manifest.hls.startGeneration(manifest);

        Status manifestStatus = waitUnilManifesStatusFinished(manifest);

        Assert.assertEquals(Status.FINISHED, manifestStatus);

        System.out.println("Encoding completed successfully");
        return new S3ObjectWrapper(manifest.getName(), s3Service.urlFileConverted(S3_OUTPUT_PATH+manifest.getName()));
    }

    private VideoConfiguration createVideoConfiguration(VideoProfile videoProfile)
            throws BitmovinApiException, UnirestException, IOException, URISyntaxException {
        return createH264VideoConfiguration((H264VideoProfile) videoProfile);
    }

    private H264VideoConfiguration createH264VideoConfiguration(H264VideoProfile videoProfile)
            throws BitmovinApiException, UnirestException, IOException, URISyntaxException {
        H264VideoConfiguration videoConfig = new H264VideoConfiguration();
        videoConfig.setName(String.format("StreamDemoH264%dp", videoProfile.getHeight()));
        videoConfig.setBitrate(videoProfile.getBitrate() * 1000);
        videoConfig.setRate(videoProfile.getFps());
        videoConfig.setWidth(videoProfile.getWidth());
        videoConfig.setHeight(videoProfile.getHeight());
        videoConfig.setProfile(videoProfile.getProfile());
        videoConfig = bitmovinConfig.instanceBitmovin().configuration.videoH264.create(videoConfig);
        return videoConfig;
    }

    private Stream createStream(Encoding encoding, InputStream inputStream, String codecConfigId)
            throws BitmovinApiException, IOException, RestException, URISyntaxException, UnirestException {
        Stream stream = new Stream();
        stream.setCodecConfigId(codecConfigId);
        stream.setInputStreams(Collections.singleton(inputStream));
        stream = bitmovinConfig.instanceBitmovin().encoding.stream.addStream(encoding, stream);
        return stream;
    }

    private Muxing createMuxing(MuxingType type, String format, Encoding encoding, Output output,
                                MediaProfile profile, Stream stream)
            throws BitmovinApiException, IOException, RestException, URISyntaxException, UnirestException {
        String path = String.format(format, profile.getBitrate(), profile.getCodecType().toString().toLowerCase(),
                type.toString().toLowerCase());

        return this.createTSMuxing(encoding, output, path, stream);
    }

    private TSMuxing createTSMuxing(Encoding encoding, Output output, String path, Stream stream)
            throws BitmovinApiException, IOException, RestException, URISyntaxException, UnirestException {
        EncodingOutput encodingOutput = new EncodingOutput();
        encodingOutput.setOutputId(output.getId());
        encodingOutput.setOutputPath(S3_OUTPUT_PATH+this.fileName + path);
        encodingOutput.setAcl(Collections.singletonList(new AclEntry(AclPermission.PUBLIC_READ)));

        TSMuxing muxing = new TSMuxing();
        MuxingStream list = new MuxingStream();
        list.setStreamId(stream.getId());
        muxing.addStream(list);
        muxing.setSegmentLength(MUXING_SEGMENT_DURATION);
        muxing.setOutputs(Collections.singletonList(encodingOutput));
        muxing = bitmovinConfig.instanceBitmovin().encoding.muxing.addTSMuxingToEncoding(encoding, muxing);
        return muxing;
    }

    private AACAudioConfig createAACAudioConfig(AACAudioProfile audioProfile)
            throws BitmovinApiException, UnirestException, IOException, URISyntaxException {
        AACAudioConfig audioConfig = new AACAudioConfig();
        audioConfig.setBitrate(audioProfile.getBitrate() * 1000);
        audioConfig.setRate(audioProfile.getRate());
        audioConfig = bitmovinConfig.instanceBitmovin().configuration.audioAAC.create(audioConfig);
        return audioConfig;
    }

    private boolean waitUntilFinished(Encoding encoding) throws BitmovinApiException, IOException, RestException,
            URISyntaxException, UnirestException, InterruptedException {
        Task status = bitmovinConfig.instanceBitmovin().encoding.getStatus(encoding);

        while (status.getStatus() != Status.FINISHED && status.getStatus() != Status.ERROR) {
            status = bitmovinConfig.instanceBitmovin().encoding.getStatus(encoding);
            Thread.sleep(2500);
        }

        return status.getStatus() == Status.FINISHED;
    }

    private Status waitUnilManifesStatusFinished(AbstractApiResponse manifest) throws InterruptedException,
            BitmovinApiException, IOException, RestException, URISyntaxException, UnirestException {
        Status manifestStatus = null;

        do {
            Thread.sleep(2500);
            manifestStatus = bitmovinConfig.instanceBitmovin().manifest.hls.getGenerationStatus((HlsManifest) manifest);

        } while (manifestStatus != Status.FINISHED && manifestStatus != Status.ERROR);
        return manifestStatus;
    }

    private HlsManifest createHlsManifest(Encoding encoding, Output output)
            throws BitmovinApiException, IOException, RestException, URISyntaxException, UnirestException {
        EncodingOutput manifestDestination = new EncodingOutput();
        manifestDestination.setOutputId(output.getId());
        manifestDestination.setOutputPath(S3_OUTPUT_PATH);
        manifestDestination.setAcl(Collections.singletonList(new AclEntry(AclPermission.PUBLIC_READ)));

        HlsManifest hlsManifest = new HlsManifest();
        hlsManifest.setName("master.m3u8");
        hlsManifest.addOutput(manifestDestination);
        hlsManifest = bitmovinConfig.instanceBitmovin().manifest.hls.create(hlsManifest);

        List<String> audioGroupIds = new ArrayList<>();

        for (AACAudioProfile audioProfile : BitmovinServiceImp.AUDIO_ENCODING_PROFILES)
        {
            for (Muxing muxing : audioProfile.getMuxings())
            {
                if (muxing.getType() == MuxingType.TS)
                {
                    String path = muxing.getOutputs().get(0).getOutputPath().replaceAll(S3_OUTPUT_PATH, "");
                    String audioGroupId = String.format("audio_%d", audioProfile.getBitrate());

                    audioGroupIds.add(audioGroupId);

                    MediaInfo audioMediaInfo = new MediaInfo();
                    audioMediaInfo.setName(audioProfile.getLanguage());
                    audioMediaInfo.setUri(String.format("audio_%s_%d.m3u8",
                            audioProfile.getCodecType().toString().toLowerCase(), audioProfile.getBitrate()));
                    audioMediaInfo.setGroupId(audioGroupId);
                    audioMediaInfo.setType(MediaInfoType.AUDIO);
                    audioMediaInfo.setEncodingId(encoding.getId());
                    audioMediaInfo.setStreamId(audioProfile.getStream().getId());
                    audioMediaInfo.setMuxingId(muxing.getId());
                    audioMediaInfo.setLanguage(audioProfile.getLanguage());
                    audioMediaInfo.setAssocLanguage(audioProfile.getLanguage());
                    audioMediaInfo.setAutoselect(false);
                    audioMediaInfo.setIsDefault(false);
                    audioMediaInfo.setForced(false);
                    audioMediaInfo.setSegmentPath(path);
                    bitmovinConfig.instanceBitmovin().manifest.hls.createMediaInfo(hlsManifest, audioMediaInfo);
                }
            }
        }

        for (String audioGroupId : audioGroupIds)
        {
            for (VideoProfile videoProfile : reverseList())
            {
                for (Muxing muxing : videoProfile.getMuxings())
                {
                    if (muxing.getType() == MuxingType.TS
                            || (muxing.getType() == MuxingType.FMP4 && videoProfile.getCodecType() == ConfigType.H265))
                    {
                        String path = muxing.getOutputs().get(0).getOutputPath().replaceAll(S3_OUTPUT_PATH, "");

                        this.addStreamInfoToHlsManifest(
                                String.format("video_%s_%dp_%d.m3u8",
                                        videoProfile.getCodecType().toString().toLowerCase(), videoProfile.getHeight(),
                                        videoProfile.getBitrate()),
                                encoding.getId(), videoProfile.getStream().getId(), muxing.getId(), audioGroupId, path,
                                hlsManifest);
                    }
                }
            }
        }

        return hlsManifest;
    }

    private VideoProfile[] reverseList() {
        List<VideoProfile> list = Arrays.asList(BitmovinServiceImp.VIDEO_ENCODING_PROFILES);
        Collections.reverse(list);
        return list.toArray(new VideoProfile[BitmovinServiceImp.VIDEO_ENCODING_PROFILES.length]);
    }

    private StreamInfo addStreamInfoToHlsManifest(String uri, String encodingId, String streamId, String muxingId,
                                                  String audioGroupId, String segmentPath, HlsManifest manifest)
            throws URISyntaxException, BitmovinApiException, RestException, UnirestException, IOException {
        StreamInfo s = new StreamInfo();
        s.setUri(uri);
        s.setEncodingId(encodingId);
        s.setStreamId(streamId);
        s.setMuxingId(muxingId);
        s.setAudio(audioGroupId);
        s.setSegmentPath(segmentPath);
        s = bitmovinConfig.instanceBitmovin().manifest.hls.createStreamInfo(manifest, s);
        return s;
    }

    @Override
    public S3ObjectWrapper getVideoFileConvertedFileURL(VideoWrapper videoWrapper) throws InterruptedException {
        try {
            URL website = new URL(videoWrapper.getUrl());
            File target = new File(website.getFile()
                    .substring(website.getFile().lastIndexOf('/')+1));
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(target);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            S3ObjectWrapper s3Object =
                    getVideoFileConvertedFile(FileUtil.convertFileToMultipartfile(target, target.getName()));
            target.delete();
            return s3Object;
        } catch (IOException | BitmovinApiException | UnirestException | URISyntaxException | RestException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    private String getVideoFileUploaded(MultipartFile file) {
        if(file.getName().equals("")) {
            return s3Service.uploadFile(file.getOriginalFilename(), file);
        } else {
            return s3Service.uploadFile(file.getName(), file);
        }
    }
}
