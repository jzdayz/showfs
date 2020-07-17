package io.github.jzdayz.controller;

import io.github.jzdayz.utils.R;
import io.github.jzdayz.service.FsService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@RequestMapping("main")
@Controller
@AllArgsConstructor
public class MainController {

    private FsService fsService;

    @ResponseBody
    @RequestMapping("list")
    public R list(String path,Boolean back) throws Exception{
        Map<String, Object> res = fsService.list(path,back);
        if (Objects.equals(res, Collections.emptyMap())){
            return R.error();
        }
        return R.ok(res);
    }


    @RequestMapping("download")
    public ResponseEntity<Resource> download(String path){
        HttpHeaders headers = new HttpHeaders();
        Resource resource = fsService.resource(path.replace("file://",""));
        headers.add(HttpHeaders.CONTENT_TYPE,"application/octet-stream");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s",resource.getFilename()));

        return new ResponseEntity<>(resource,headers, HttpStatus.OK);
    }

    @RequestMapping("show")
    public ResponseEntity<byte[]> show(String path){
        HttpHeaders headers = new HttpHeaders();
        Resource resource = fsService.resource(path.replace("file://",""));
        headers.add(HttpHeaders.CONTENT_TYPE,"text/html;charset=UTF-8");
        byte[] data;
        try (
                InputStream inputStream = resource.getInputStream();
                ){
            data = StreamUtils.copyToByteArray(inputStream);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(data,headers, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping("upload")
    public Object upload(FilePart file) throws Exception{
        if (fsService.createFile(file)) {
            return R.ok();
        }
        return R.error("file exists");
    }
}
