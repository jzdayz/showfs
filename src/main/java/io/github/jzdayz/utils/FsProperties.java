package io.github.jzdayz.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "fs")
public class FsProperties {

    private String path = System.getProperty("user.dir");

    private Boolean safe = Boolean.FALSE;

}
