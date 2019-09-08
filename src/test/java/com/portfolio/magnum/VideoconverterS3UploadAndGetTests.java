package com.portfolio.magnum;

import com.amazonaws.services.s3.model.S3Object;
import com.portfolio.magnum.service.Imp.S3ServiceImp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class VideoconverterS3UploadAndGetTests {

	@Autowired
	private S3ServiceImp s3Service;


	//@Test
	public void deveConverterFileParaMultiPartFile() throws Exception {
		File file = new ClassPathResource("/sample.mkv").getFile();
		MultipartFile multipartFile = new MockMultipartFile("source.mkv",
				new FileInputStream(file));
		Assert.assertNotNull(multipartFile.getBytes());
	}

	//@Test
	public void deveConverterFileParaMultiPartFileEArmazenarNoBucket() throws Exception {
		File file = new ClassPathResource("/sample.mkv").getFile();
		MultipartFile mpfSource = new MockMultipartFile("source.mkv",
				new FileInputStream(file));

		File source = new File("source.mkv");
		mpfSource.transferTo(source);

		s3Service.uploadFile("target.flv", mpfSource);
	}

	//@Test
	public void deveRetornarUrlDoArquivo() throws Exception {
		File file = new ClassPathResource("/sample.mkv").getFile();
		MultipartFile mpfSource = new MockMultipartFile("source.mkv",
				new FileInputStream(file));

		File source = new File("source.mkv");
		mpfSource.transferTo(source);

		String s3Object = s3Service.uploadFile("target.flv", mpfSource);
	}

	//@Test
	public void deveConverterURLParaMultiPartFileEConverterParaFlv() throws Exception {
		URL website = new URL("http://dinamica-sambatech.s3.amazonaws.com/sample.mkv");
		File source = new File("sample.mkv");
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(source);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();

		MultipartFile multipartFile = new MockMultipartFile("source.mkv",
				new FileInputStream(source));

		multipartFile.transferTo(source);

		Assert.assertTrue(source.exists());
	}

}
