class File {
  static String separator;

  File(String path) {}
  File.fromParent(File parent, String path) {}
  File.fromUri(String rootUri) {}

  String get name {}
  String get path {}
  File get parent {}
  String getAbsolutePath() {}
  File getParentFile() {}
  List<File> listFiles() {}
  int lastModified() {}
  void delete() {}
  bool exists() {}
  String toUriString() {}
  bool isDirectory() {}
  bool isFile() {}
  void mkdirs() {}
  int length() {}
  void writeString(String data) {}

  static List<String> readAllLines(String path) {
    //TODO
  }
  static String readString(String uri) {
    //TODO
  }
  static void copy(String source, File dest, StandardCopyOption option) {
    //TODO
  }
  static void writeLines(
      File source, List<String> lines, StandardOpenOption option) {
    //TODO
  }
  static bool isAbsolutePath(String path) {
    //TODO
  }
}

enum StandardCopyOption { REPLACE_EXISTING, COPY_ATTRIBUTES, ATOMIC_MOVE }

enum StandardOpenOption {
  READ,
  WRITE,
  APPEND,
  TRUNCATE_EXISTING,
  CREATE,
  CREATE_NEW,
  DELETE_ON_CLOSE,
  SPARSE,
  SYNC,
  DSYNC
}
