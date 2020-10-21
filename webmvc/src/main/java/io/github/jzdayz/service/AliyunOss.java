package io.github.jzdayz.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.SimplifiedObjectMeta;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "SpringJavaInjectionPointsAutowiringInspection", "SpringJavaAutowiredFieldsWarningInspection"})
@Service
@EnableConfigurationProperties(AliyunOss.AliyunOssProperties.class)
public class AliyunOss implements OssService {

    @Autowired
    private AliyunOssProperties ossProperties;

    @Override
    public List<FileObject> list() {
        OSS client = new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );
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
        client.shutdown();
        return rs;
    }

    @Override
    public void upload() {

    }

    @Data
    @ConfigurationProperties("aliyun.oss")
    public static class AliyunOssProperties {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
    }
}
