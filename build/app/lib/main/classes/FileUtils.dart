import 'dart:convert';

import 'Env.dart';
import 'FileUploadResult.dart';
import 'UploadFile.dart';
import 'package:http/http.dart' as http;
import '../classes/DFile.dart';

import 'dart:html' as darthtml;

import 'core.dart';

class FileUtils {
  static FileUtils _obj;
  factory FileUtils.get() {
    if (_obj == null) {
      _obj = FileUtils._init();
    }
    return _obj;
  }

  String _baseUrl;
  Uri _uploadUrl;
  FileUtils._init() {
    _baseUrl = Env.get().baseHttpUrl;
    _uploadUrl = Uri.parse(_baseUrl + '/api/upload');
  }

  Future<bool> upload(
      List<String> extns,
      OneFunction<UploadFile, bool> askForConfirm,
      Consumer<FileUploadResult> onDone,
      Consumer<FileUploadResult> onError,
      Runnable onCancel) async {
    darthtml.InputElement uploadInput = darthtml.FileUploadInputElement();
    uploadInput.multiple = false;
    if (extns != null) {
      extns = extns.map((e) {
        if (!e.startsWith('.')) {
          e = '.' + e;
        }
        return e;
      }).toList();
      uploadInput.accept = extns.join(',');
    }
    uploadInput.click();
    await uploadInput.onChange.asyncMap((e) => e).first.then((e) async {
      final files = uploadInput.files;
      if (files.isEmpty) {
        onCancel();
      } else {
        final file = files.first;
        UploadFile uFile = UploadFile(file);
        if (askForConfirm(uFile)) {
          final reader = new darthtml.FileReader();
          reader.readAsDataUrl(file);
          FileUploadResult result = await reader.onLoadEnd
              .asyncMap((event) => event)
              .first
              .then((event) => _doUpload(
                  reader.result.toString().split(',').last, file.name));
          if (result.success) {
            onDone(result);
          } else {
            onError(result);
          }
        }
      }
    });

    return true;
  }

  Future<bool> uploadMultiple(
      List<String> extns,
      OneFunction<List<UploadFile>, bool> askForConfirm,
      Consumer<List<FileUploadResult>> onDone,
      Consumer<List<FileUploadResult>> onError,
      Runnable onCancel) async {
    darthtml.InputElement uploadInput = darthtml.FileUploadInputElement();
    uploadInput.multiple = true;
    if (extns != null) {
      extns = extns.map((e) {
        if (!e.startsWith('.')) {
          e = '.' + e;
        }
        return e;
      }).toList();
      uploadInput.accept = extns.join(',');
    }
    uploadInput.click();
    await uploadInput.onChange.asyncMap((e) => e).first.then((e) async {
      final files = uploadInput.files;
      if (files.isEmpty) {
        onCancel();
      } else {
        List<UploadFile> uFiles = files.map((e) => UploadFile(e));
        if (askForConfirm(uFiles)) {
          final reader = new darthtml.FileReader();
          List<FileUploadResult> results = [];
          files.forEach((file) async {
            reader.readAsDataUrl(file);
            FileUploadResult result = await reader.onLoadEnd
                .asyncMap((event) => event)
                .first
                .then((event) => _doUpload(
                    reader.result.toString().split(',').last, file.name));
            results.add(result);
          });
          onDone(results.where((res) => res.success));
          onError(results.where((res) => !res.success));
        }
      }
    });

    return true;
  }

  Future<FileUploadResult> _doUpload(
      String fileAsString, String fileName) async {
    var request = new http.MultipartRequest("POST", _uploadUrl);
    request.files.add(http.MultipartFile.fromBytes(
        'file', Base64Decoder().convert(fileAsString),
        filename: fileName));
    return await request.send().then((response) {
      if (response.statusCode == 200) {
        return http.Response.fromStream(response).then((value) {
          return FileUploadResult(
              DFile.fromJson(json.decode(value.body)), true, '');
        });
      } else {
        return http.Response.fromStream(response).then((value) {
          var obj = json.decode(value.body);
          return FileUploadResult(null, false, obj['message']);
        });
      }
    });
  }

  String getDownloadUrl(DFile file, {int width, int height}) {
    if (file == null) {
      return 'https://secure.meetupstatic.com/photos/event/4/a/b/5/600_466219125.jpeg';
    }
    String uri = _baseUrl + '/api/download/' + file.id;
    bool hasOriginalName = (file.name != null && file.name.isNotEmpty);
    if (width != null || height != null || hasOriginalName) {
      uri += '?';
      if (width != null) {
        uri += 'width=' + width.toString();
      }
      if (height != null) {
        uri += 'height=' + height.toString();
      }
      if (hasOriginalName != null) {
        uri += 'originalName=' + file.name;
      }
    }
    return uri;
  }
}
