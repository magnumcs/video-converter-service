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
import com.bitmovin.api.encoding.encodings.muxing.FMP4Muxing;
import com.bitmovin.api.encoding.encodings.muxing.Muxing;
import com.bitmovin.api.encoding.encodings.muxing.MuxingStream;
import com.bitmovin.api.encoding.encodings.muxing.TSMuxing;
import com.bitmovin.api.encoding.encodings.muxing.enums.MuxingType;
import com.bitmovin.api.encoding.encodings.streams.Stream;
import com.bitmovin.api.encoding.enums.CloudRegion;
import com.bitmovin.api.encoding.enums.DashMuxingType;
import com.bitmovin.api.encoding.enums.StreamSelectionMode;
import com.bitmovin.api.encoding.inputs.S3Input;
import com.bitmovin.api.encoding.manifest.dash.*;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VideoconverterBitmovinTests {

	@Test
	public void contextLoads() {
	}

	private static String S3_INPUT_ACCESSKEY = "";
	private static String S3_INPUT_SECRET_KEY = "";
	private static String S3_INPUT_BUCKET_NAME = "magnum-bucket-east1";
	private static String S3_INPUT_PATH = "SampleVideo_360x240_2mb.mkv";

	private static String S3_OUTPUT_ACCESSKEY = "";
	private static String S3_OUTPUT_SECRET_KEY = "";
	private static String S3_OUTPUT_BUCKET_NAME = "magnum-bucket-east1";
	private static String S3_OUTPUT_PATH = "output/audio/128_aac_fmp4/";

	private static final String API_KEY = "bf6ef996-8bd8-4429-bded-9d49bbcd83f7";
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
	public void testCreateDashEncoding() throws IOException, BitmovinApiException, UnirestException,
			URISyntaxException, RestException, InterruptedException {

		bitmovinApi = new BitmovinApi(API_KEY);

		Encoding encoding = new Encoding();
		encoding.setName("Teste Convert video para dash S3 Bucket");
		encoding.setCloudRegion(CLOUD_REGION);
		encoding = bitmovinApi.encoding.create(encoding);

		S3Input input = new S3Input();
		input.setAccessKey(S3_INPUT_ACCESSKEY);
		input.setSecretKey(S3_INPUT_SECRET_KEY);
		input.setBucketName(S3_INPUT_BUCKET_NAME);
		input = bitmovinApi.input.s3.create(input);

		S3Output output = new S3Output();
		output.setAccessKey(S3_OUTPUT_ACCESSKEY);
		output.setSecretKey(S3_OUTPUT_SECRET_KEY);
		output.setBucketName(S3_OUTPUT_BUCKET_NAME);
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

		MuxingType[] muxingTypes = new MuxingType[] { MuxingType.FMP4 };

		for (VideoProfile videoProfile : VIDEO_ENCODING_PROFILES) {
			VideoConfiguration videoConfig = createVideoConfiguration(videoProfile);
			Stream videoStream = createStream(encoding, inputStreamVideo, videoConfig.getId());
			videoProfile.setStream(videoStream);
			for (MuxingType type : muxingTypes) {
				Muxing muxing = this.createMuxing(type, "video/%d_%s_%s", encoding, output, videoProfile, videoStream);
				videoProfile.getMuxings().add(muxing);
			}
		}

		for (AACAudioProfile audioProfile : AUDIO_ENCODING_PROFILES) {
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

		DashManifest manifest = createDashManifest(encoding, output);

		bitmovinApi.manifest.dash.startGeneration(manifest);

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

		return this.createFMP4Muxing(encoding, output, path, stream);
	}

	private FMP4Muxing createFMP4Muxing(Encoding encoding, Output output, String path, Stream stream)
			throws BitmovinApiException, IOException, RestException, URISyntaxException, UnirestException
	{
		EncodingOutput encodingOutput = new EncodingOutput();
		encodingOutput.setOutputId(output.getId());
		encodingOutput.setOutputPath(S3_OUTPUT_PATH + path);
		encodingOutput.setAcl(Arrays.asList(new AclEntry(AclPermission.PUBLIC_READ)));

		FMP4Muxing muxing = new FMP4Muxing();
		MuxingStream list = new MuxingStream();
		list.setStreamId(stream.getId());
		muxing.addStream(list);
		muxing.setSegmentLength(MUXING_SEGMENT_DURATION);
		muxing.setOutputs(Collections.singletonList(encodingOutput));
		muxing = bitmovinApi.encoding.muxing.addFmp4MuxingToEncoding(encoding, muxing);
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
			manifestStatus = bitmovinApi.manifest.dash.getGenerationStatus((DashManifest) manifest);

		} while (manifestStatus != Status.FINISHED && manifestStatus != Status.ERROR);
		return manifestStatus;
	}

	private DashManifest createDashManifest(Encoding encoding, Output output)
			throws BitmovinApiException, UnirestException, IOException, URISyntaxException, RestException {
		EncodingOutput manifestOutput = new EncodingOutput();
		manifestOutput.setOutputId(output.getId());
		manifestOutput.setOutputPath(S3_OUTPUT_PATH);
		manifestOutput.setAcl(Collections.singletonList(new AclEntry(AclPermission.PUBLIC_READ)));

		DashManifest dashManifest = new DashManifest();
		dashManifest.setName("stream.mpd");
		dashManifest.setOutputs(Collections.singletonList(manifestOutput));
		dashManifest = bitmovinApi.manifest.dash.create(dashManifest);

		Period period = bitmovinApi.manifest.dash.createPeriod(dashManifest, new Period());

		Set<ConfigType> codecsForDash = this.getVideoCodecs();
		for (ConfigType codec : codecsForDash) {
			VideoAdaptationSet videoAdaptationSet = bitmovinApi.manifest.dash
					.addVideoAdaptationSetToPeriod(dashManifest, period, new VideoAdaptationSet());

			for (VideoProfile videoProfile : VIDEO_ENCODING_PROFILES) {
				if (videoProfile.getCodecType() == codec) {
					for (Muxing muxing : videoProfile.getMuxings()) {
						if (muxing.getType() == MuxingType.FMP4)
						{
							String path = muxing.getOutputs().get(0).getOutputPath().replaceAll(S3_OUTPUT_PATH, "");

							this.addDashRepresentationToAdaptationSet(DashMuxingType.TEMPLATE, encoding.getId(),
									videoProfile.getStream().getId(), muxing, path, dashManifest, period,
									videoAdaptationSet);
						}
					}
				}
			}
		}

		for (AACAudioProfile audioProfile : AUDIO_ENCODING_PROFILES) {
			AudioAdaptationSet audioAdaptationSet = new AudioAdaptationSet();
//			audioAdaptationSet.setLang(audioProfile.getLanguage());

			audioAdaptationSet = bitmovinApi.manifest.dash.addAudioAdaptationSetToPeriod(dashManifest, period,
					audioAdaptationSet);

			for (Muxing muxing : audioProfile.getMuxings()) {
				if (muxing.getType() == MuxingType.FMP4) {
					String path = muxing.getOutputs().get(0).getOutputPath().replaceAll(S3_OUTPUT_PATH, "");

					this.addDashRepresentationToAdaptationSet(DashMuxingType.TEMPLATE, encoding.getId(),
							audioProfile.getStream().getId(), muxing, path, dashManifest, period, audioAdaptationSet);
				}
			}
		}

		return dashManifest;
	}

	private Set<ConfigType> getVideoCodecs() {
		Set<ConfigType> setOfCodecs = new LinkedHashSet<>();
		for (VideoProfile videoProfile : VIDEO_ENCODING_PROFILES) {
			setOfCodecs.add(videoProfile.getCodecType());
		}

		return setOfCodecs;
	}

	private void addDashRepresentationToAdaptationSet(DashMuxingType type, String encodingId, String streamId,
													  Muxing muxing, String segmentPath, DashManifest manifest, Period period, AdaptationSet adaptationSet)
			throws BitmovinApiException, URISyntaxException, RestException, UnirestException, IOException {

		DashSegmentedRepresentation r = new DashFmp4Representation();
		r.setType(type);
		r.setEncodingId(encodingId);
		r.setStreamId(streamId);
		r.setMuxingId(muxing.getId());
		r.setSegmentPath(segmentPath);
		bitmovinApi.manifest.dash.addRepresentationToAdaptationSet(manifest, period, adaptationSet, r);
	}


}
