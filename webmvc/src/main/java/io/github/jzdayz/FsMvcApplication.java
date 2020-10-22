package io.github.jzdayz;

import io.github.jzdayz.utils.SocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class FsMvcApplication {

    static {
        int port = SocketUtils.availablePort();
        System.setProperty("server.port", String.valueOf(port));
        log.info("use port {} ", port);
    }

    public static void main(String[] args) {
        SpringApplication.run(FsMvcApplication.class, args);
    }

}
