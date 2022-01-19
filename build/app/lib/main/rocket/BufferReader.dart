import 'dart:convert';
import 'dart:math';

import 'dart:typed_data';

class BufferReader {
  Uint8List data;
  int index = 0;
  static final Utf8Codec _utf8 = Utf8Codec();

  BufferReader(this.data);

  int _readByte() {
    return data[index++];
  }

  int _readInt() {
    var lo = 0;
    var hi = 0;

    // Read low 28 bits.
    for (var i = 0; i < 4; i++) {
      var byte = readByte();
      lo |= (byte & 0x7f) << (i * 7);
      if ((byte & 0x80) == 0) return _fromInts(hi, lo);
    }

    // Read middle 7 bits: 4 low belong to low part above,
    // 3 remaining belong to hi.
    var byte = readByte();
    lo |= (byte & 0xf) << 28;
    hi = (byte >> 4) & 0x7;
    if ((byte & 0x80) == 0) {
      return _fromInts(hi, lo);
    }

    // Read remaining bits of hi.
    for (var i = 0; i < 5; i++) {
      var byte = readByte();
      hi |= (byte & 0x7f) << ((i * 7) + 3);
      if ((byte & 0x80) == 0) return _fromInts(hi, lo);
    }
    return -1;
  }

  int _fromInts(int hi, int low) {
    return low + hi * pow(2, 32);
  }

  void _checkLimit(int len) {}
  Uint8List readBytes() {
    var length = readInt();
    _checkLimit(length);
    index += length;
    return Uint8List.sublistView(data, index - length, index);
  }

  ByteData _readByteData(int sizeInBytes) {
    _checkLimit(sizeInBytes);
    index += sizeInBytes;
    return ByteData.sublistView(data, index - sizeInBytes, index);
  }

  int _decodeZigZag64(int value) {
    return ((value & 1) == 1 ? -(value ~/ 2) - 1 : (value ~/ 2));
  }

  bool readBool() => _readByte() == 1;
  int readByte() => _readByte();
  int readInt() => _decodeZigZag64(_readInt());
  String readString() {
    var length = readInt();
    if (length == -1) {
      return null;
    }
    _checkLimit(length);
    String str =
        _utf8.decode(Uint8List.sublistView(data, index, index + length));
    index += length;
    return str;
  }

  double readFloat() => _readByteData(4).getFloat32(0, Endian.little);
  double readDouble() => _readByteData(8).getFloat64(0, Endian.little);
}
