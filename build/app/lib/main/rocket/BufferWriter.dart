import 'dart:convert';

import 'dart:typed_data';

class BufferWriter {
  BytesBuilder buffer = BytesBuilder();
  Uint8List data = Uint8List(1024);
  int index = 0;
  static final Utf8Codec _utf8 = Utf8Codec();

  BufferWriter();

  void _writeByte(int val) {
    _ensureBytes(1);
    data[index++] = val;
  }

  void _writeInt(int value) {
    _ensureBytes(10);
    var i = index;
    var lo = value.toUnsigned(32).toInt();
    var hi = (value >> 32).toUnsigned(32).toInt();
    while (hi > 0 || lo >= 0x80) {
      data[i++] = 0x80 | (lo & 0x7f);
      lo = (lo >> 7) | ((hi & 0x7f) << 25);
      hi >>= 7;
    }
    data[i++] = lo;
    index = i;
  }

  void writeBool(bool val) {
    return _writeByte(val ? 1 : 0);
  }

  void _ensureBytes(int len) {
    if (index + len > data.length) {
      buffer.add(Uint8List.sublistView(data, 0, index));
      index = 0;
    }
  }

  Uint8List takeBytes() {
    if (index > 0) {
      buffer.add(Uint8List.sublistView(data, 0, index));
      index = 0;
    }
    return buffer.takeBytes();
  }

  void writeBytes(Uint8List val) {
    int toWrite = val.length;
    int dataSize = data.length;
    writeInt(toWrite);
    if (toWrite > dataSize) {
      // if val is too big to fit, some other arrangement has to be made
      _writeBytesOnOverflow(val);
      return;
    }

    _ensureBytes(toWrite);
    data.setAll(index, val);
    index += val.length;
  }

  void _writeBytesOnOverflow(Uint8List val) {
    int dataSize = data.length;

    int _stillToWrite = val.length;
    int _idx = 0;
    while (_stillToWrite > 0) {
      // There is still data to write

      // Take a chunk
      int _chunkSize;

      if (_stillToWrite >= dataSize) {
        // If the amount still left to write is more than data size, then chunk size will be data size
        _chunkSize = dataSize;
      } else {
        // What we need to write fits inside data. So, chunk size is same as _stillToWrite
        _chunkSize = _stillToWrite;
      }

      // Get proper end index
      int _endIdx = _idx + _chunkSize;

      // ensure space
      _ensureBytes(_chunkSize);

      // Splice and write
      Uint8List piece = Uint8List.sublistView(val, _idx, _endIdx);

      data.setAll(index, piece);
      index += piece.length;

      // Loop management
      _stillToWrite -= piece.length;
      _idx = _endIdx;
    }
  }

  ByteData _readByteData(int sizeInBytes) {
    _ensureBytes(sizeInBytes);
    index += sizeInBytes;
    return ByteData.sublistView(data, index, index + sizeInBytes);
  }

  int _encodeZigZag64(int value) => (value << 1) ^ (value >> 63);
  void writeByte(int value) => _writeByte(value);
  void writeInt(int value) => _writeInt(_encodeZigZag64(value));
  void writeString(String str) {
    if (str == null || str.isEmpty) {
      writeInt(-1);
    } else {
      writeBytes(_utf8.encode(str));
    }
  }

  void writeFloat(double value) => _readByteData(4).setFloat32(0, value);
  void writeDouble(double value) => _readByteData(8).setFloat64(0, value);
}
