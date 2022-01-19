package d3e.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Objects;

@RestController
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private static final int TIMEOUT = 365 * 24 * 60 * 60;

    @Autowired
    private Map<String, D3EResourceHandler> handlers;

    @Autowired
    private D3ETempResourceHandler saveHandler;

    @PostMapping("/api/upload")
    public DFile uploadFile(@RequestParam("file") MultipartFile multiFile) {
        String fileName = multiFile.getOriginalFilename();
        try {
            DFile file = saveHandler.save(fileName, multiFile.getInputStream());
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", e);
        }
    }

    @PostMapping("/api/uploads")
    public List<DFile> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files).stream().map(file -> uploadFile(file)).collect(Collectors.toList());
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/api/download/{fileName:.+}")
    public ResponseEntity<Resource> download(@PathVariable String fileName,
            @RequestParam(required = false) String originalName, @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height,
            @RequestParam(required = false) String inline,
            HttpServletRequest request) {
        D3EResourceHandler loadHandler = handlers.getOrDefault(getPrefix(fileName), null);
        if (loadHandler == null) {
            throw new RuntimeException("Resource not found.");
        }

        // Load file as Resource
        Resource resource = loadFileAsResource(loadHandler, fileName, width == null ? 0 : width,
                height == null ? 0 : height);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        String headerFileName = (originalName != null && !originalName.isEmpty()) ? originalName
                : resource.getFilename();
       BodyBuilder builder = ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType));
    	if(!Objects.equal(inline, "true")) {
    		builder = builder.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + headerFileName + "\"");
    	}
        return builder.header(HttpHeaders.CACHE_CONTROL, "max-age=" + TIMEOUT).body(resource);
    }

    private String getPrefix(String fileName) {
        int first = fileName.indexOf(':');
        if (first == -1) {
            return null;
        }
        return fileName.substring(0, first);
    }

    public Resource loadFileAsResource(D3EResourceHandler loadHandler, String fileName, int width, int height) {
        String resizedName = FileUtils.getResizedName(fileName, width, height);
        return loadHandler.get(resizedName);
    }
}