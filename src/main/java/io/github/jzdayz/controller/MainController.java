package io.github.jzdayz.controller;

import io.github.jzdayz.utils.R;
import io.github.jzdayz.service.FsService;
import lombok.AllArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@RequestMapping("main")
@RestController
@AllArgsConstructor
public class MainController {

    private FsService fsService;

    @RequestMapping("list")
    public R list(String path) throws Exception{
        Map<String, Object> res = fsService.list(path);
        if (Objects.equals(res, Collections.emptyMap())){
            return R.error();
        }
        return R.ok(res);
    }

    @RequestMapping("download")
    public Object download(String path){
        if (StringUtils.isEmpty(path)){
            return R.error();
        }
        return fsService.resource(path);
    }

    @RequestMapping("upload")
    public Object upload(FilePart file) throws Exception{
        if (fsService.createFile(file)) {
            return R.ok();
        }
        return R.error("file exists");
    }
}
