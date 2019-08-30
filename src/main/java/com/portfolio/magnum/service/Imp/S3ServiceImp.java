package com.portfolio.magnum.service.Imp;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.portfolio.magnum.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3ServiceImp implements S3Service {

    private static Logger logger = LoggerFactory.getLogger(S3ServiceImp.class);

    private final AmazonS3 s3client;

    private final AmazonS3Client amazonS3Client;

    @Value("${storage.s3.bucket}")
    private String bucket;

    @Autowired
    public S3ServiceImp(AmazonS3 s3client, AmazonS3Client amazonS3Client) {
        this.s3client = s3client;
        this.amazonS3Client = amazonS3Client;
    }

    @Override
    public String uploadFile(String keyName, MultipartFile file) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3client.putObject("magnum-bucket-2019", keyName, file.getInputStream(), metadata);
        } catch(IOException ioe) {
            logger.error("IOException: " + ioe.getMessage());
        } catch (AmazonServiceException ase) {
            logger.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
            logger.info("Error Message:    " + ase.getMessage());
            logger.info("HTTP Status Code: " + ase.getStatusCode());
            logger.info("AWS Error Code:   " + ase.getErrorCode());
            logger.info("Error Type:       " + ase.getErrorType());
            logger.info("Request ID:       " + ase.getRequestId());
            throw ase;
        } catch (AmazonClientException ace) {
            logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + ace.getMessage());
            throw ace;
        }
        return amazonS3Client.getResourceUrl(bucket, keyName);
    }

}
