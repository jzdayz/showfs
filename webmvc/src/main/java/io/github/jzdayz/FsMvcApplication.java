package io.github.jzdayz;

import io.github.jzdayz.utils.SocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class FsMvcApplication {

	static {
		int port = SocketUtils.availablePort();
		System.setProperty("server.port", String.valueOf(port));
		log.info("use port {} ",port);
	}

	public static void main(String[] args) {
		SpringApplication.run(FsMvcApplication.class, args);
	}

	@Bean
	public FileSystemManager vfs() throws FileSystemException {
		return VFS.getManager();
	}

}
