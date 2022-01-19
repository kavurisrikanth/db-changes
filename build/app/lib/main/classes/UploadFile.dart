import 'dart:html' as darthtml;

class UploadFile {
  darthtml.File _file;

  UploadFile(this._file);

  int get lastModified => _file.lastModified;

  DateTime get lastModifiedDate => _file.lastModifiedDate;

  String get name => _file.name;

  String get relativePath => _file.relativePath;

  int get size => _file.size;

  String get type => _file.type;

  String toString() => _file.toString();
}
