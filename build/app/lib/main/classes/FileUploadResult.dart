import 'core.dart';
import 'DFile.dart';

class FileUploadResult {
  DFile file;
  bool success;
  String errorMessage;

  FileUploadResult(this.file, this.success, this.errorMessage);

  factory FileUploadResult.failed(String failedtoupload) =>
      FileUploadResult(null, false, failedtoupload);
}
