package d3e.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.tika.Tika;

public class FileUtils {
	static Tika tika = new Tika();
    static Path getFilePath(String name, String prefix, String targetPath) {
        Path filePath;
        if (name.startsWith(prefix)) {
            name = name.substring(prefix.length() + 1);
            File targetLocation = new File(targetPath + File.separator + name);
            filePath = targetLocation.toPath();
            return filePath;
        } else {
            throw new RuntimeException("File not found " + name);
        }
    }
    
    public static String detectMimeType(File file) {
    	try {
    		return tika.detect(file);
    	}catch (Exception e) {
    		return "";
    	}
    }

    static DFile storeFile(InputStream fileStream, File file, String name, String id) throws IOException {
        Path filePath = file.toPath();
        Files.copy(fileStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        DFile out = new DFile();
        out.setMimeType(detectMimeType(file));
        out.setId(id);
        out.setName(name);
        out.setSize(file.length());
        return out;
    }

    static String getResizedName(String name, int width, int height) {
        StringBuilder nameBuilder = new StringBuilder(name);
        String resPart = "";
        if (width != 0 && height != 0) {
            resPart = "_" + width + "_" + height;
        }
        int lastDot = name.lastIndexOf(".");
        nameBuilder.insert(lastDot, resPart);
        return nameBuilder.toString();
    }

    static String stripPrefix(String id) {
        int prefixEnd = id.indexOf(":");
        if (prefixEnd == -1) {
            return id;
        }
        return id.substring(prefixEnd + 1);
    }
}
