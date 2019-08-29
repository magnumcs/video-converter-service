package com.portfolio.magnum;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.common.io.Files;
import com.portfolio.magnum.domain.FlvFile;
import com.portfolio.magnum.service.Imp.S3ServiceImp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VideoconverterApplicationTests {

	@Autowired
	private S3ServiceImp s3Service;

	@Autowired
	private AmazonS3Client amazonS3Client;

	@Test
	public void deveConverterMkvParaFLV() throws Exception{

		File source = new File("source.mkv");
		File test = new ClassPathResource("/sample.mkv").getFile();

		byte[] bytes = Files.toByteArray(test);

		try (FileOutputStream fos = new FileOutputStream(source)) {
			fos.write(bytes);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		File target = new File("target.flv");
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("libmp3lame");
		audio.setBitRate(64000);
		audio.setChannels(1);
		audio.setSamplingRate(22050);
		VideoAttributes video = new VideoAttributes();
		video.setCodec("flv");
		video.setBitRate(160000);
		video.setFrameRate(15);
//		video.setSize(new VideoSize(1280, 720));
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("flv");
		attrs.setAudioAttributes(audio);
		attrs.setVideoAttributes(video);
		Encoder encoder = new Encoder();
		encoder.encode(new MultimediaObject(source), target, attrs);
		source.delete();
		Assert.assertTrue(target.exists());
	}

	@Test
	public void deveConverterFileParaMultiPartFile() throws Exception {
		File file = new ClassPathResource("/sample.mkv").getFile();
		MultipartFile multipartFile = new MockMultipartFile("source.mkv",
				new FileInputStream(file));
		Assert.assertNotNull(multipartFile.getBytes());
	}

	@Test
	public void deveConverterFileParaMultiPartFileEConverterParaFlv() throws Exception {
		File file = new ClassPathResource("/sample.mkv").getFile();
		MultipartFile multipartFile = new MockMultipartFile("source.mkv",
				new FileInputStream(file));

		File source = new File("source.mkv");
		multipartFile.transferTo(source);

		File target = new File(FlvFile.getFileName());
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec(FlvFile.getAudioCodec());
		audio.setBitRate(FlvFile.getAudioBitRate());
		audio.setChannels(FlvFile.getAudioChannels());
		audio.setSamplingRate(FlvFile.getAudioSamplingRate());
		VideoAttributes video = new VideoAttributes();
		video.setCodec(FlvFile.getVideoCodec());
		video.setBitRate(FlvFile.getVideoBitRate());
		video.setFrameRate(FlvFile.getVideoFrameRate());
//		video.setSize(new VideoSize(1280, 720));
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat(FlvFile.getFormat());
		attrs.setAudioAttributes(audio);
		attrs.setVideoAttributes(video);
		Encoder encoder = new Encoder();
		encoder.encode(new MultimediaObject(source), target, attrs);
		source.delete();
		Assert.assertTrue(target.exists());
	}

	@Test
	public void deveConverterFileParaMultiPartFileEConverterParaFlvEArmazenarNoBucket() throws Exception {
		File file = new ClassPathResource("/sample.mkv").getFile();
		MultipartFile multipartFile = new MockMultipartFile("source.mkv",
				new FileInputStream(file));

		File source = new File("source.mkv");
		multipartFile.transferTo(source);

		File target = new File(FlvFile.getFileName());
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec(FlvFile.getAudioCodec());
		audio.setBitRate(FlvFile.getAudioBitRate());
		audio.setChannels(FlvFile.getAudioChannels());
		audio.setSamplingRate(FlvFile.getAudioSamplingRate());
		VideoAttributes video = new VideoAttributes();
		video.setCodec(FlvFile.getVideoCodec());
		video.setBitRate(FlvFile.getVideoBitRate());
		video.setFrameRate(FlvFile.getVideoFrameRate());
//		video.setSize(new VideoSize(1280, 720));
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat(FlvFile.getFormat());
		attrs.setAudioAttributes(audio);
		attrs.setVideoAttributes(video);
		Encoder encoder = new Encoder();
		encoder.encode(new MultimediaObject(source), target, attrs);
		source.delete();
		Assert.assertTrue(target.exists());
		MultipartFile mpfTarget = new MockMultipartFile("target.flv",
				new FileInputStream(target));
		s3Service.uploadFile("target.flv", mpfTarget);
	}

	@Test
	public void deveRetornarUrlDoArquivo() {
		String url = amazonS3Client.getResourceUrl("magnum-bucket-2019", "target.flv");
		Assert.assertEquals("https://magnum-bucket-2019.s3.us-east-2.amazonaws.com/target.flv", url);
	}

}
