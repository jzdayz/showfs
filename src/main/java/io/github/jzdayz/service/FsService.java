package io.github.jzdayz.service;

import com.google.common.collect.Maps;
import io.github.jzdayz.utils.ExceptionUtils;
import io.github.jzdayz.utils.FsProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class FsService {

    private FsProperties fsProperties;
    private FileSystemManager fileSystemManager;
    private ResourceLoader resourceLoader;

    public Map<String,Object> list(String path) throws Exception{
        path = StringUtils.isEmpty(path) ? fsProperties.getPath() : path;
        FileObject fileObject =
                fileSystemManager.resolveFile(path);
        if (!fileObject.isFolder()){
            return Collections.emptyMap();
        }
        FileObject[] childrenFiles = fileObject.getChildren();
        Map<String, Object> maps =
                Maps.newHashMap();
        maps.put("files",
                Stream
                        .of(childrenFiles)
                        .filter(k->ExceptionUtils.ex(()->!k.isHidden(),true))
                        .map(FileObj::trans)
                        .toArray());
        maps.put("path",path);
        return maps;
    }

    public Resource resource(String path){
        return resourceLoader.getResource("file:"+path);
    }

    public boolean createFile(FilePart file) throws IOException {
        String targetPath = fsProperties.getPath() + File.separator + file.filename();
        Path path = Paths.get(targetPath);
        if (Files.exists(path)){
            return false;
        }
        Files.createFile(path);
        file.transferTo(new File(targetPath));
        return true;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FileObj{
        private String name;
        private String path;
        private long size;
        private boolean folder;

        public static FileObj trans(FileObject fileObject){
            return FileObj.builder()
                    .name(fileObject.getName().getBaseName())
                    .path(fileObject.getName().getPath())
                    .folder(ExceptionUtils.ex(fileObject::isFolder,Boolean.TRUE))
                    .size(ExceptionUtils.ex(()->fileObject.getContent().getSize(),0L))
                    .build();
        }
    }

}
