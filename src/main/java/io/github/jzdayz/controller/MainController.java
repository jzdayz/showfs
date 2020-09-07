package io.github.jzdayz.controller;

import io.github.jzdayz.service.FsService;
import io.github.jzdayz.utils.IpUtil;
import io.github.jzdayz.utils.R;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@RequestMapping("main")
@Controller
@AllArgsConstructor
public class MainController {

    private FsService fsService;

    private ServerProperties serverProperties;

    @ResponseBody
    @RequestMapping("list")
    public R list(String path, Boolean back) throws Exception {
        Map<String, Object> res = fsService.list(path, back);
        if (Objects.equals(res, Collections.emptyMap())) {
            return R.error();
        }
        return R.ok(res);
    }

//    @RequestMapping("delete")
//    public ResponseEntity<String> delete(String path) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.CONTENT_TYPE,"text/plain");
//        fsService.delete(path.replace("file://", ""));
//        return new ResponseEntity<>(
//                "OK",headers, HttpStatus.OK
//        );
//    }

    @RequestMapping("head")
    public ResponseEntity<String> head() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE,"text/plain");
        return new ResponseEntity<>(
                String.format("http://%s:%s", IpUtil.getIp(), serverProperties.getPort()),headers, HttpStatus.OK
        );
    }


    @RequestMapping("download")
    public ResponseEntity<Resource> download(String path) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        Resource resource = fsService.resource(path.replace("file://", ""));
        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s",
                URLEncoder.encode(Objects.requireNonNull(resource.getFilename()), "utf-8")));

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @RequestMapping("show")
    public ResponseEntity<byte[]> show(String path) {
        HttpHeaders headers = new HttpHeaders();
        Resource resource = fsService.resource(path.replace("file://", ""));
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html;charset=UTF-8");
        byte[] data;
        try (
                InputStream inputStream = resource.getInputStream();
        ) {
            data = StreamUtils.copyToByteArray(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping("upload")
    public Object upload(FilePart file) throws Exception {
        while (!fsService.createFile(file)) {
            FilePart finalFile = file;
            file = new FilePart() {
                @Override
                public String filename() {
                    String filename = finalFile.filename();
                    String[] f = filename.split("\\.");
                    if (f.length != 2) {
                        throw new RuntimeException();
                    }
                    return f[0] + "1" + "." + f[1];
                }

                @Override
                public Mono<Void> transferTo(Path dest) {
                    return finalFile.transferTo(dest);
                }

                @Override
                public String name() {
                    return finalFile.name();
                }

                @Override
                public HttpHeaders headers() {
                    return finalFile.headers();
                }

                @Override
                public Flux<DataBuffer> content() {
                    return finalFile.content();
                }
            };
        }
        return R.error("file exists");
    }
}
