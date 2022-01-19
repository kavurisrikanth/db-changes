package d3e.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import store.Database;

@Service("local")
public class D3ELocalResourceHandler implements D3EResourceHandler {

    private static String PREFIX = "local";

    @Value("${server.localstore}")
    private String targetPath;

    @Autowired
    private D3ETempResourceHandler tempHandler;

    @Autowired
    private ImageResizeService resizer;

    @PostConstruct
    public void init() {
    	File ws = new File(targetPath);
    	ws.mkdirs();
    }
    
    @Override
    public DFile save(DFile file) {
        if (file.getId() == null || file.getId().isEmpty()) {
            return null;
        }
	    if(file.getId().startsWith(PREFIX)) {
	    	return file;
	    }
        return saveInternal(file);
    }

    @Override
    public DFile saveImage(DFile file, List<ImageDimension> resizes) {
        resizes.forEach(one -> this.resizer.resize(file.getId(), one.getWidth(), one.getHeight(), this));
        return save(file);
    }

    private DFile saveInternal(DFile file) {
        try {
            String fileName = file.getId();
            String withoutPrefix = FileUtils.stripPrefix(fileName);
            File targetFile = FileExt.fromParent(new File(targetPath), withoutPrefix);
            targetFile.createNewFile();
            Resource inTemp = tempHandler.get(fileName);
            Path existing = inTemp.getFile().toPath();
			DFile out = FileUtils.storeFile(Files.newInputStream(existing), targetFile, file.getName(),
					PREFIX + ":" + withoutPrefix);
			Database.get().saveDFile(out);
			return out;
        } catch (IOException e) {
            throw new RuntimeException("Saving file failed: " + e);
        }
    }

    // Keep
    @Override
    public Resource get(String name) {
        Path filePath = FileUtils.getFilePath(name, PREFIX, targetPath);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + name);
            }
        } catch (Exception e) {
            throw new RuntimeException("File not found " + name);
        }
    }
}
