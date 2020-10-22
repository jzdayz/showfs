package io.github.jzdayz.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import io.github.jzdayz.service.AliyunOss;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
public class OssConfig {

    private final AliyunOss.AliyunOssProperties ossProperties;

    public OssConfig(AliyunOss.AliyunOssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    @Bean
    public OSS oss(){
        return new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );
    }

    @PreDestroy
    public void destroy(){
        oss().shutdown();
    }

}
