package io.github.jzdayz.controller;

import io.github.jzdayz.service.OssService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("list")
    public List<OssService.FileObject> list(){
        return ossService.list();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("upload")
    public void upload(MultipartFile file) throws Exception {
        ossService.upload(file);
    }
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("delete")
    public void delete(String name){
        ossService.delete(name);
    }


}
