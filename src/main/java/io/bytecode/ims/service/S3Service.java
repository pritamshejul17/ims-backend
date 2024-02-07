package io.bytecode.ims.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.InputStream;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3;

    public String uploadFile(String bucketName, String key, File file) {
        amazonS3.putObject(new PutObjectRequest(bucketName, key, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucketName, key).toString();
    }

    public void deleteFile(String bucketName, String key) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
    }

    public S3Object downloadFile(String bucketName, String key) {
        return amazonS3.getObject(new GetObjectRequest(bucketName, key));
    }
}
