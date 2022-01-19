package d3e.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class FileExt {

  public static File fromParent(File file, String path) {
    return new File(file, path);
  }
  
  public static File getParent(File file) {
	return file.getParentFile();
  }

  public static void createNewFile(File file) {
    try {
      file.createNewFile();
    } catch (IOException e) {
      D3ELogger.printStackTrace(e);
    }
  }

  public static File fromUri(String uri) {
    try {
      return new File(new URI(uri));
    } catch (URISyntaxException e) {
      return null;
    }
  }

  public static String toUriString(File file) {
    return file.toURI().toString();
  }

  public static void writeString(File file, String data) {
    try {
      com.google.common.io.Files.asCharSink(file, StandardCharsets.UTF_8).write(data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<String> readAllLines(String path) {
    try {
      return Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<File> listFiles(File file) {
    return Arrays.asList(file.listFiles());
  }

  public static String readString(String uri) {
    try {
      return Files.readString(Paths.get(new URI(uri)));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void copy(String source, File dest, StandardCopyOption option) {
    try {
      Files.copy(Paths.get(source), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void writeLines(File source, List<String> lines, StandardOpenOption option) {
    try {
      Files.write(source.toPath(), lines, StandardOpenOption.WRITE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean isAbsolutePath(String path) {
    return Path.of(path).isAbsolute();
  }

  public static Set<String> getAllFilePaths(File root, String buildPath, List<String> excludes) {
    Set<String> result = SetExt.Set();
    if (root == null || !root.exists()) {
      return result;
    }
    List<Predicate<String>> filters = ListExt.List();
    if (excludes != null) {
      for (String one : excludes) {
        filters.add(str -> {
          return !(str.startsWith(one) || str.matches(Pattern.quote(one)));
        });
      }
    }
    Predicate<String> filter = filters.stream().reduce(x -> true, Predicate::and);
    return FileUtils.listFiles(root, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).stream()
        .map(one -> getRelativePath(one.getAbsolutePath(), buildPath)).filter(filter).collect(Collectors.toSet());
  }

  public static List<String> readLines(File exclude, String header) {
    if (exclude == null) {
      return null;
    }
    try {
      return FileUtils.readLines(exclude, Charset.defaultCharset()).stream()
          .filter(one -> !one.isEmpty() && !one.startsWith("#")).collect(Collectors.toList());
    } catch (IOException e) {
      return null;
    }
  }
  
  public static String getRelativePath2(String path, String rootPath) {
	  try {
		  URI pathUri;
		  if(path.startsWith("file:")) {
			  pathUri=new URI(path);
		  } else {
			  pathUri = new File(path).toURI();
		  }
		  URI rootPathUri;
		  if(rootPath.startsWith("file:")) {
			  rootPathUri=new URI(rootPath);
		  } else {
			  rootPathUri = new File(rootPath).toURI();
		  }
		  return rootPathUri.relativize(pathUri).getPath();
	  } catch (Exception e) {
		  return path;
	  }
  }

  public static String getRelativePath(String path, String rootPath) {
    StringBuilder sb = new StringBuilder();
    sb.append(rootPath == null ? path : windowsToLinuxPath(path.substring(rootPath.length() + 1)));
    return sb.toString();
  }

  public static String windowsToLinuxPath(String path) {
    return StringExt.replaceAll(path, "\\\\", "/");
  }

  public static File downloadFile(String url, File targetDir, String storeAt) {
    try {
      URL source = new URL(url);
      File target = FileExt.fromParent(targetDir, "__" + storeAt);
      ReadableByteChannel readableByteChannel = Channels.newChannel(source.openStream());
      FileOutputStream fileOutputStream = new FileOutputStream(target.getAbsolutePath());
      FileChannel fileChannel = fileOutputStream.getChannel();
      fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
      fileOutputStream.flush();
      fileOutputStream.close();
      return target;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public static boolean isTarGz(File file) {
    try {
      InputStream stream = new FileInputStream(file);
      byte[] byteArray = stream.readNBytes(2);
      stream.close();
      return byteArray[0] == (byte) 0x1F && byteArray[1] == (byte) 0x8B;
    } catch (IOException e) {
      return false;
    }
  }

  public static File toTarGz(File source, File target, FilenameFilter filter) {
    FileOutputStream fos;
    if (!target.exists()) {
      createNewFile(target);
    }
    try {
      fos = new FileOutputStream(target);
      GZIPOutputStream gos = new GZIPOutputStream(new BufferedOutputStream(fos));
      TarArchiveOutputStream tarOs = new TarArchiveOutputStream(gos);
      for (File f : source.listFiles(filter)) {
        addFilesToTarGZ(f, "", tarOs, filter);
      }
      tarOs.close();
      return target;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private static void addFilesToTarGZ(File file, String parent, TarArchiveOutputStream tarArchive, FilenameFilter filter) throws IOException {
    // Create entry name relative to parent file path
    String entryName = parent + file.getName();
    // add tar ArchiveEntry
    tarArchive.putArchiveEntry(new TarArchiveEntry(file, entryName));
    if (file.isFile()) {
      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(fis);
      // Write file content to archive
      IOUtils.copy(bis, tarArchive);
      tarArchive.closeArchiveEntry();
      bis.close();
    } else if (file.isDirectory()) {
      // no need to copy any content since it is
      // a directory, just close the outputstream
      tarArchive.closeArchiveEntry();
      // for files in the directories
      for (File f : file.listFiles(filter)) {
        // recursively call the method for all the subdirectories
        addFilesToTarGZ(f, entryName + File.separator, tarArchive, filter);
      }
    }
  }

  public static File gunzip(File source, File destination) throws FileNotFoundException, IOException {
    GZIPInputStream in = new GZIPInputStream(new FileInputStream(source));
    File tarFile = FileExt.fromParent(destination, source.getName() + "_unzipped");
    final FileOutputStream out = new FileOutputStream(tarFile);
    IOUtils.copy(in, out);
    in.close();
    out.close();

    return tarFile;
  }

  public static File untar(File tarFile, File destination) throws ArchiveException, IOException {
    final List<File> untaredFiles = new LinkedList<File>();
    final InputStream is = new FileInputStream(tarFile);
    final TarArchiveInputStream debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory()
        .createArchiveInputStream("tar", is);
    TarArchiveEntry entry = null;
    while ((entry = (TarArchiveEntry) debInputStream.getNextEntry()) != null) {
      final File outputFile = FileExt.fromParent(destination, entry.getName());
      if (entry.isDirectory()) {
        if (!outputFile.exists()) {
          if (!outputFile.mkdirs()) {
            throw new IllegalStateException(
                String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
          }
        }
      } else {
        final OutputStream outputFileStream = new FileOutputStream(outputFile);
        IOUtils.copy(debInputStream, outputFileStream);
        outputFileStream.close();
      }
      untaredFiles.add(outputFile);
    }
    debInputStream.close();

    deleteFile(tarFile);
    return destination;
  }

  public static File extractTarGz(File source, File destination) {
    // source is a tar.gz file. destination is a directory.
    try {
      File tarFile = gunzip(source, destination);
      return untar(tarFile, destination);
    } catch (IOException | ArchiveException e) {
      throw new IllegalStateException(e);
    }
  }

  public static void deleteFile(File file) {
    if (file == null) {
      return;
    }
    try {
      Files.delete(file.toPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static File getUserHome() {
    return new File(System.getProperty("user.home"));
  }

  public static String detectMimeType(File f) {
	return d3e.core.FileUtils.detectMimeType(f);
  }
}
