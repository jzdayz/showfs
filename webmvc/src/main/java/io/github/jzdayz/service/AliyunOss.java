package io.github.jzdayz.service;

import cn.hutool.crypto.SecureUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.oss.model.SimplifiedObjectMeta;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "SpringJavaInjectionPointsAutowiringInspection", "SpringJavaAutowiredFieldsWarningInspection"})
@Service
@EnableConfigurationProperties(AliyunOss.AliyunOssProperties.class)
public class AliyunOss implements OssService {

    @Autowired
    private AliyunOssProperties ossProperties;

    @Autowired
    private OSS client;

    @Override
    public List<FileObject> list() {

        final ListObjectsV2Result lr = client.listObjectsV2(ossProperties.getBucketName());
        List<FileObject> rs = lr.getObjectSummaries()
                .parallelStream()
                .filter(k -> !k.getKey().endsWith("/"))
                .map(k -> {
                    SimplifiedObjectMeta objectMeta = client.getSimplifiedObjectMeta(ossProperties.getBucketName(), k.getKey());
                    FileObject fo = new FileObject();
                    fo.setName(k.getKey());
                    fo.setDataSize(DataSize.ofBytes(objectMeta.getSize()));
                    fo.setTmpUrl(
                            client.generatePresignedUrl(
                                    ossProperties.getBucketName(),
                                    fo.getName(),
                                    new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)
                            ).toString()
                    );
                    return fo;
                }).collect(Collectors.toList());
        return rs;
    }

    @Override
    public void upload(MultipartFile multipartFile) throws IOException {
        try (
                InputStream inputStream = multipartFile.getInputStream()
                ){
            client.putObject(
                    ossProperties.getBucketName(),
                    multipartFile.getOriginalFilename(),
                    inputStream
            );
        }
    }

    @Override
    public void delete(String name) {
        client.deleteObject(ossProperties.getBucketName(),name);
    }

    @Setter
    @ConfigurationProperties("aliyun.oss")
    public static class AliyunOssProperties {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
        private String desKey;
        private static volatile boolean init = false;

        public String getEndpoint() {
            return endpoint;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        @SuppressWarnings("ConstantConditions")
        public String getAccessKeySecret() {
            if (desKey == null){
                Assert.state(false,"需要aliyun.oss.des-key属性!!");
                System.exit(1);
            }

            String rs = init ?
                    accessKeySecret :
                    (accessKeySecret = SecureUtil.des(desKey.getBytes(StandardCharsets.UTF_8)).decryptStr(accessKeySecret));
            init = true;
            return rs;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getDesKey() {
            return desKey;
        }

    }
}
