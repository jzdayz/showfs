package io.github.jzdayz.service;

import lombok.Data;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface OssService {

    @Data
    class FileObject{
        private String name;
        private DataSize dataSize;
        private String tmpUrl;
    }

    List<FileObject> list();

    void upload(MultipartFile multipartFile) throws IOException;

    void delete(String name);

}
