import '../classes/DFile.dart';
import '../classes/Date.dart';
import '../classes/Time.dart';
import 'BufferWriter.dart';
import 'D3ETemplate.dart';
import 'D3ETemplateTypes.dart';
import '../utils/D3EObjectChanges.dart';
import '../utils/DBObject.dart';
import '../utils/ReferenceCatch.dart';

class _Change {
  final Object obj;
  final int index;
  final bool added;
  _Change(this.added, this.index, this.obj);
}

class WSWriter {
  static int _localObjCount = -2;
  ReferenceCatch _cache;
  BufferWriter _out = BufferWriter();
  WSWriter(this._cache);

  void done() {}
  void clear<T extends DBObject>() {}
  void setChanges(D3EObjectChanges changes) {}
  int nextLocalId() {
    _localObjCount = _localObjCount - 1;
    return _localObjCount;
  }

  void writeObjStart<T extends DBObject>(T obj) {}
  int get fields => 0;
  int get nextField => 0;

  List<int> get out {
    return _out.takeBytes();
  }

  void writeByte(int byte) {
    // print('w byte: ' + byte.toString());
    _out.writeByte(byte);
  }

  void writeId(int id) {
    // print('w id: ' + id.toString());
    _out.writeInt(id);
  }

  void writeBoolean(bool val) {
    // print('w bool: ' + val.toString());
    _out.writeBool(val);
  }

  void writeInteger(int val) {
    // print('w int: ' + val?.toString());
    _out.writeInt(val);
  }

  void writeDouble(double val) {
    // print('w double: ' + val.toString());
    _out.writeDouble(val);
  }

  void writeRef<T extends DBObject>(T obj) {
    if (obj == null) {
      // print('w ref: null');
      writeInteger(-1);
      return;
    }
    _writeRef(obj);
    writeInteger(-1);
  }

  void _writeRef<T extends DBObject>(T obj) {
    String type = obj.d3eType;
    int typeIdx = D3ETemplate.typeInt(type);
    // print('w ref: ' + type);
    writeInteger(typeIdx);
    D3ETemplateType tt = D3ETemplate.types[typeIdx];
    if (tt.embedded) {
      return;
    }
    if (obj.id == 0) {
      obj.id = nextLocalId();
      _cache.addObject(obj);
    }
    if (obj.id == null) {
      obj.id = 0;
    }
    writeInteger(obj.id);
  }

  void writeDFile(DFile file) {
    if (file == null) {
      // print('w dfile: null');
      writeString(null);
      return;
    }
    writeString(file.id);
    writeString(file.name);
    writeInteger(file.size);
    writeString(file.mimeType);
  }

  void writeObj<T extends DBObject>(T obj) {
    if (obj == null) {
      // print('w obj: null');
      writeInteger(-1);
      return;
    }
    _writeRef(obj);
    String type = obj.d3eType;
    D3EObjectChanges changes = obj.d3eChanges;
    D3ETemplateType tType = D3ETemplate.types[D3ETemplate.typeInt(type)];
    if (tType.refType == D3ERefType.Struct) {
      int index = 0;
      for (D3ETemplateField field in tType.fields) {
        if (field.inverse || field.unknown) {
          continue;
        }
        writeInteger(index);
        if (field.collection) {
          List list = obj.get(index);
          writeInteger(list.length);
          for (Object val in list) {
            _writeListItem(field, val);
          }
        } else {
          _writeField(field, index, obj);
        }
        index++;
      }
    } else {
      if (changes.values != null) {
        changes.values.keys.forEach((key) {
          D3ETemplateField field = tType[key];
          if (field.inverse || field.unknown) {
            return;
          }
          if (field.collection) {
            //List oldList = obj.d3eChanges.values[key];
            List newList = obj.get(key);
            //if (oldList.isEmpty && newList.isNotEmpty) {
            writeInteger(key);
            writeInteger(newList.length);
            for (Object val in newList) {
              _writeListItem(field, val);
            }
            // } else {
            //   List<_Change> changes = computeListChanges(oldList, newList);
            //   if (changes.isNotEmpty) {
            //     writeInteger(key);
            //     _writeListChanges(field, changes);
            //   }
            // }
          } else {
            writeInteger(key);
            _writeField(field, key, obj);
          }
        });
      }
    }
    writeInteger(-1);
  }

  List<_Change> computeListChanges(List from, List to) {
    List<_Change> compiledResult = [];
    int x = 0;
    int xCount = to.length;
    int y = 0;
    int yCount = from.length;
    int lookAhead = 1;
    while (x < xCount) {
      Object xobj = to[x];
      if (y == yCount) {
        compiledResult.add(_Change(true, x, xobj));
      } else {
        int temp = 0;
        bool found = false;
        while (temp <= lookAhead && (y + temp) < yCount) {
          Object yobj = from[y + temp];
          if (xobj == yobj) {
            found = true;
            while (temp > 0) {
              temp--;
              yobj = from[y + temp];
              compiledResult.add(_Change(false, y, yobj));
              y++;
            }
            y++;
            break;
          }
          temp++;
        }
        if (!found) {
          compiledResult.add(_Change(true, x, xobj));
        }
      }
      x++;
    }
    while (y < yCount) {
      Object yobj = from[y];
      compiledResult.add(_Change(false, x, yobj));
      x++;
      y++;
    }

    return compiledResult;
  }

  void _writeListChanges(D3ETemplateField field, List<_Change> changes) {
    writeInteger(-changes.length); // We are sending changes only so -ve
    for (_Change change in changes) {
      if (change.added) {
        writeInteger(change.index + 1);
        _writeListItem(field, change.obj);
      } else {
        writeInteger(-change.index - 1);
      }
    }
  }

  void _writeListItem(D3ETemplateField field, Object obj) {
    switch (field.fieldType) {
      case D3EFieldType.String:
        {
          writeString(obj as String);
          break;
        }
      case D3EFieldType.Integer:
        {
          writeInteger(obj as int);
          break;
        }
      case D3EFieldType.Double:
        {
          writeDouble(obj as double);
          break;
        }
      case D3EFieldType.Boolean:
        {
          writeBoolean(obj as bool);
          break;
        }
      case D3EFieldType.Date:
        {
          Date val = obj as Date;
          if (val == null) {
            writeInteger(-1);
          } else {
            writeInteger(val.year);
            writeInteger(val.month);
            writeInteger(val.day);
          }
          break;
        }

      case D3EFieldType.DateTime:
        DateTime val = obj as DateTime;
        if (val == null) {
          writeInteger(-1);
        } else {
          writeInteger(val.millisecondsSinceEpoch);
        }
        break;
      case D3EFieldType.Time:
        Time val = obj as Time;
        if (val == null) {
          writeInteger(-1);
        } else {
          writeInteger(val.toDateTime().millisecondsSinceEpoch);
        }
        break;
      case D3EFieldType.Duration:
        Duration val = obj as Duration;
        if (val == null) {
          writeInteger(-1);
        } else {
          writeInteger(val.inMilliseconds);
        }
        break;
      case D3EFieldType.DFile:
        DFile val = obj as DFile;
        writeDFile(val);
        break;
      case D3EFieldType.Enum:
        int val = obj as int;
        writeInteger(val);
        break;
      case D3EFieldType.Ref:
        DBObject val = obj as DBObject;
        if (field.child) {
          writeObj(val);
        } else {
          writeRef(val);
        }
        break;
    }
  }

  void _writeField(D3ETemplateField field, int key, DBObject obj) {
    switch (field.fieldType) {
      case D3EFieldType.String:
        {
          String val = obj.get(key);
          writeString(val);
          break;
        }
      case D3EFieldType.Integer:
        {
          int val = obj.get(key);
          writeInteger(val);
          break;
        }
      case D3EFieldType.Double:
        {
          double val = obj.get(key);
          writeDouble(val);
          break;
        }
      case D3EFieldType.Boolean:
        {
          bool val = obj.get(key);
          writeBoolean(val);
          break;
        }
      case D3EFieldType.Date:
        {
          Date val = obj.get(key);
          if (val == null) {
            writeInteger(-1);
          } else {
            writeInteger(val.year);
            writeInteger(val.month);
            writeInteger(val.day);
          }
          break;
        }

      case D3EFieldType.DateTime:
        DateTime val = obj.get(key);
        if (val == null) {
          writeInteger(-1);
        } else {
          writeInteger(val.millisecondsSinceEpoch);
        }
        break;
      case D3EFieldType.Time:
        Time val = obj.get(key);
        if (val == null) {
          writeInteger(-1);
        } else {
          writeInteger(val.toDateTime().millisecondsSinceEpoch);
        }
        break;
      case D3EFieldType.Duration:
        Duration val = obj.get(key);
        if (val == null) {
          writeInteger(-1);
        } else {
          writeInteger(val.inMilliseconds);
        }
        break;
      case D3EFieldType.DFile:
        DFile val = obj.get(key);
        writeDFile(val);
        break;
      case D3EFieldType.Enum:
        int val = obj.get(key);
        writeInteger(val);
        break;
      case D3EFieldType.Ref:
        DBObject val = obj.get(key);
        if (field.child) {
          writeObj(val);
        } else {
          writeRef(val);
        }
        break;
    }
  }

  void writeObjFull<T extends DBObject>(T obj) {
    writeObj(obj);
  }

  void writeObjFullList<T extends DBObject>(List<T> list) {
    // print('w obj list: ' + list.length.toString());
    writeInteger(list.length);
    for (T obj in list) {
      writeObjFull(obj);
    }
  }

  void writeObjUnion<T extends DBObject>(T obj) {
    return writeObjFull(obj);
  }

  void writeObjUnionList<T extends DBObject>(List<T> list) {
    writeObjFullList(list);
  }

  void writeRefList<T extends DBObject>(List<T> list) {
    // print('w ref list: ' + list.length.toString());
    writeInteger(list.length);
    for (T obj in list) {
      writeRef(obj);
    }
  }

  void writeSubRefList<T extends DBObject>(List<T> list) {
    writeRefList(list);
  }

  void writeObjList<T extends DBObject>(List<T> list) {
    writeObjFullList(list);
  }

  void writeSubRef<T extends DBObject>(T obj) {
    writeRef(obj);
  }

  void writeBool(bool val) {
    // print('w bool: ' + val.toString());
    _out.writeBool(val);
  }

  void writeString(String str) {
    // print('w str: ' + (str == null ? 'null' : str));
    _out.writeString(str);
  }

  void writeStringList(List<String> list) {
    // print('w str list: ' + list.length.toString());
    writeInteger(list.length);
    for (String str in list) {
      writeString(str);
    }
  }

  void writeDateTime(DateTime val) {
    writeString(val?.toString());
  }

  void writeTime(Time val) {
    writeString(val?.toString());
  }

  void writeEnum<T>(T en) {
    if (en == null) {
      // print('w enum: null');
      return;
    }
    // print('w enum: ' + en.toString());
    List<String> parts = en.toString().split('.');
    int type = D3ETemplate.typeInt(parts.first);
    writeInteger(type);
    D3ETemplateType t = D3ETemplate.types[type];
    int f = t.fieldMap[parts.last];
    D3ETemplateField tf = t.fields[f];
    if (tf.unknown) {
      // Current value is not known to server, what to do?
      // For now lets write the first value known to server
      for (int x = 0; x < t.fields.length; x++) {
        if (!t.fields[x].unknown) {
          writeInteger(x);
          break;
        }
      }
    } else {
      writeInteger(f);
    }
  }

  void writeEnumList<T>(List<T> list) {
    // print('w enum list: ' + list.length.toString());
    writeInteger(list.length);
    for (T e in list) {
      writeEnum(e);
    }
  }
}
