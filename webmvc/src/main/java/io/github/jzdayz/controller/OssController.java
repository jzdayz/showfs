package io.github.jzdayz.controller;

import io.github.jzdayz.service.OssService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("oss")
@AllArgsConstructor
public class OssController {

    private OssService ossService;

    @RequestMapping("list")
    public List<OssService.FileObject> list(){
        return ossService.list();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("upload")
    public void upload(MultipartFile file) throws Exception {
        File f = new File("/Users/huqingfeng/Downloads/a.zip");
        f.createNewFile();
        System.out.println(111111);
        StreamUtils.copy(file.getInputStream(),new FileOutputStream(f));
        System.out.println(1);
    }


}
