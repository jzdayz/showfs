package io.github.jzdayz;

import io.github.jzdayz.utils.FsProperties;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(FsProperties.class)
public class ShowfsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShowfsApplication.class, args);
	}

	@Bean
	public FileSystemManager vfs() throws FileSystemException {
		return VFS.getManager();
	}

}
