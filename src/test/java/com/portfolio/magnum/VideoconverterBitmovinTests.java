package com.portfolio.magnum;

import com.bitmovin.api.AbstractApiResponse;
import com.bitmovin.api.BitmovinApi;
import com.bitmovin.api.encoding.AclEntry;
import com.bitmovin.api.encoding.AclPermission;
import com.bitmovin.api.encoding.EncodingOutput;
import com.bitmovin.api.encoding.InputStream;
import com.bitmovin.api.encoding.codecConfigurations.AACAudioConfig;
import com.bitmovin.api.encoding.codecConfigurations.H264VideoConfiguration;
import com.bitmovin.api.encoding.codecConfigurations.VideoConfiguration;
import com.bitmovin.api.encoding.codecConfigurations.enums.ConfigType;
import com.bitmovin.api.encoding.codecConfigurations.enums.ProfileH264;
import com.bitmovin.api.encoding.encodings.Encoding;
import com.bitmovin.api.encoding.encodings.muxing.Muxing;
import com.bitmovin.api.encoding.encodings.muxing.MuxingStream;
import com.bitmovin.api.encoding.encodings.muxing.TSMuxing;
import com.bitmovin.api.encoding.encodings.muxing.enums.MuxingType;
import com.bitmovin.api.encoding.encodings.streams.Stream;
import com.bitmovin.api.encoding.enums.CloudRegion;
import com.bitmovin.api.encoding.enums.StreamSelectionMode;
import com.bitmovin.api.encoding.inputs.S3Input;
import com.bitmovin.api.encoding.manifest.hls.HlsManifest;
import com.bitmovin.api.encoding.manifest.hls.MediaInfo;
import com.bitmovin.api.encoding.manifest.hls.MediaInfoType;
import com.bitmovin.api.encoding.manifest.hls.StreamInfo;
import com.bitmovin.api.encoding.outputs.Output;
import com.bitmovin.api.encoding.outputs.S3Output;
import com.bitmovin.api.encoding.status.Task;
import com.bitmovin.api.enums.Status;
import com.bitmovin.api.exceptions.BitmovinApiException;
import com.bitmovin.api.http.RestException;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.portfolio.magnum.domain.wrapper.AACAudioProfile;
import com.portfolio.magnum.domain.wrapper.H264VideoProfile;
import com.portfolio.magnum.domain.wrapper.MediaProfile;
import com.portfolio.magnum.domain.wrapper.VideoProfile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VideoconverterBitmovinTests {

    @Value("${storage.aws.access_key_id}")
    private String awsId;

    @Value("${storage.aws.secret_access_key}")
    private String awsKey;

    @Value("${storage.s3.bucket}")
    private String bucket;

    @Value("${spring.api.key}")
    private String bitmovinKey;

	@Test
	public void contextLoads() {
	}

	private static String S3_INPUT_PATH = "SampleVideo_360x240_2mb.mkv";
	private static String S3_OUTPUT_PATH = "output/";

	private static final CloudRegion CLOUD_REGION = CloudRegion.AWS_US_EAST_2;

	private static final double MUXING_SEGMENT_DURATION = 4.0;

	private static final VideoProfile[] VIDEO_ENCODING_PROFILES = new VideoProfile[]
			{
					new H264VideoProfile(640, 360, 1200, 30.0f, ProfileH264.MAIN),
					new H264VideoProfile(854, 480, 1750, 30.0f, ProfileH264.MAIN),
					new H264VideoProfile(1280, 720, 2350, 30.0f, ProfileH264.MAIN),
			};

	private static final AACAudioProfile[] AUDIO_ENCODING_PROFILES = new AACAudioProfile[]
			{
					new AACAudioProfile(128, 48000f, "en"),
			};

	private static BitmovinApi bitmovinApi;

	@Test
	public void testCreateHLSEncoding() throws IOException, BitmovinApiException, UnirestException,
			URISyntaxException, RestException, InterruptedException {

		bitmovinApi = new BitmovinApi(bitmovinKey);

		Encoding encoding = new Encoding();
		encoding.setName("Teste Convert video para hls S3 Bucket");
		encoding.setCloudRegion(CLOUD_REGION);
		encoding = bitmovinApi.encoding.create(encoding);

		S3Input input = new S3Input();
		input.setAccessKey(awsId);
		input.setSecretKey(awsKey);
		input.setBucketName(bucket);
		input = bitmovinApi.input.s3.create(input);

		S3Output output = new S3Output();
		output.setAccessKey(awsId);
		output.setSecretKey(awsKey);
		output.setBucketName(bucket);
		output = bitmovinApi.output.s3.create(output);

		InputStream inputStreamVideo = new InputStream();
		inputStreamVideo.setInputPath(S3_INPUT_PATH);
		inputStreamVideo.setInputId(input.getId());
		inputStreamVideo.setSelectionMode(StreamSelectionMode.AUTO);
		inputStreamVideo.setPosition(0);

		InputStream inputStreamAudio = new InputStream();
		inputStreamAudio.setInputPath(S3_INPUT_PATH);
		inputStreamAudio.setInputId(input.getId());
		inputStreamAudio.setSelectionMode(StreamSelectionMode.AUTO);
		inputStreamAudio.setPosition(0);

		MuxingType[] muxingTypes = new MuxingType[] { MuxingType.TS };

		for (VideoProfile videoProfile : VIDEO_ENCODING_PROFILES) {
		    videoProfile.getMuxings().clear();
			VideoConfiguration videoConfig = createVideoConfiguration(videoProfile);
			Stream videoStream = createStream(encoding, inputStreamVideo, videoConfig.getId());
			videoProfile.setStream(videoStream);
			for (MuxingType type : muxingTypes) {
				Muxing muxing = this.createMuxing(type, "video/%d_%s_%s", encoding, output, videoProfile, videoStream);
				videoProfile.getMuxings().add(muxing);
			}
		}

		for (AACAudioProfile audioProfile : AUDIO_ENCODING_PROFILES) {
		    audioProfile.getMuxings().clear();
			AACAudioConfig audioConfig = createAACAudioConfig(audioProfile);
			Stream audioStream = createStream(encoding, inputStreamAudio, audioConfig.getId());
			audioProfile.setStream(audioStream);
			for (MuxingType type : muxingTypes) {
				Muxing muxing = this.createMuxing(type, "audio/%d_%s_%s", encoding, output, audioProfile, audioStream);
				audioProfile.getMuxings().add(muxing);
			}
		}

		bitmovinApi.encoding.start(encoding);

		Assert.assertTrue(waitUntilFinished(encoding));

		HlsManifest manifest = createHlsManifest(encoding, output);

		bitmovinApi.manifest.hls.startGeneration(manifest);

		Status manifestStatus = waitUnilManifesStatusFinished(manifest);

		System.out.println(String.format("%s generation finished with status %s",
				manifest.getClass().getName(), manifestStatus.toString()));
		Assert.assertEquals(Status.FINISHED, manifestStatus);

		System.out.println("Encoding completed successfully");

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
		videoConfig = bitmovinApi.configuration.videoH264.create(videoConfig);
		return videoConfig;
	}

	private Stream createStream(Encoding encoding, InputStream inputStream, String codecConfigId)
			throws BitmovinApiException, IOException, RestException, URISyntaxException, UnirestException {
		Stream stream = new Stream();
		stream.setCodecConfigId(codecConfigId);
		stream.setInputStreams(Collections.singleton(inputStream));
		stream = bitmovinApi.encoding.stream.addStream(encoding, stream);
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
		encodingOutput.setOutputPath(S3_OUTPUT_PATH + path);
		encodingOutput.setAcl(Collections.singletonList(new AclEntry(AclPermission.PUBLIC_READ)));

		TSMuxing muxing = new TSMuxing();
		MuxingStream list = new MuxingStream();
		list.setStreamId(stream.getId());
		muxing.addStream(list);
		muxing.setSegmentLength(MUXING_SEGMENT_DURATION);
		muxing.setOutputs(Collections.singletonList(encodingOutput));
		muxing = bitmovinApi.encoding.muxing.addTSMuxingToEncoding(encoding, muxing);
		return muxing;
	}

	private AACAudioConfig createAACAudioConfig(AACAudioProfile audioProfile)
			throws BitmovinApiException, UnirestException, IOException, URISyntaxException {
		AACAudioConfig audioConfig = new AACAudioConfig();
		audioConfig.setBitrate(audioProfile.getBitrate() * 1000);
		audioConfig.setRate(audioProfile.getRate());
		audioConfig = bitmovinApi.configuration.audioAAC.create(audioConfig);
		return audioConfig;
	}

	private boolean waitUntilFinished(Encoding encoding) throws BitmovinApiException, IOException, RestException,
			URISyntaxException, UnirestException, InterruptedException {
		Task status = bitmovinApi.encoding.getStatus(encoding);

		while (status.getStatus() != Status.FINISHED && status.getStatus() != Status.ERROR) {
			status = bitmovinApi.encoding.getStatus(encoding);
			Thread.sleep(2500);
		}

		return status.getStatus() == Status.FINISHED;
	}

	private Status waitUnilManifesStatusFinished(AbstractApiResponse manifest) throws InterruptedException,
			BitmovinApiException, IOException, RestException, URISyntaxException, UnirestException {
		Status manifestStatus = null;

		do {
			Thread.sleep(2500);
			manifestStatus = bitmovinApi.manifest.hls.getGenerationStatus((HlsManifest) manifest);

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
		hlsManifest = bitmovinApi.manifest.hls.create(hlsManifest);

		List<String> audioGroupIds = new ArrayList<>();

		for (AACAudioProfile audioProfile : AUDIO_ENCODING_PROFILES)
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
					bitmovinApi.manifest.hls.createMediaInfo(hlsManifest, audioMediaInfo);
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
		List<VideoProfile> list = Arrays.asList(VideoconverterBitmovinTests.VIDEO_ENCODING_PROFILES);
		Collections.reverse(list);
		return list.toArray(new VideoProfile[VideoconverterBitmovinTests.VIDEO_ENCODING_PROFILES.length]);
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
		s = bitmovinApi.manifest.hls.createStreamInfo(manifest, s);
		return s;
	}

}
