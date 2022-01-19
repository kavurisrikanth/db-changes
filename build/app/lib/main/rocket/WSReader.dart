import '../classes/DFile.dart';
import '../classes/Date.dart';
import '../classes/Time.dart';
import 'BufferReader.dart';
import 'D3ETemplate.dart';
import 'D3ETemplateTypes.dart';
import '../utils/DBObject.dart';
import '../utils/DBSaveStatus.dart';
import '../utils/ReferenceCatch.dart';

class WSReader {
  BufferReader _data;
  ReferenceCatch _catch;
  List<int> out = [];
  WSReader(this._catch, this._data);

  void done() {
    //print('r done');
  }

  int readByte() {
    int b = _data.readByte();
    //print('r byte: ' + b.toString());
    return b;
  }

  String readString() {
    String str = _data.readString();
    //print('r str: ' + (str == null ? 'null' : str));
    return str;
  }

  List<String> readStringList() {
    List<String> res = [];
    int size = _data.readInt();
    //print('r str list: ' + size.toString());
    for (int i = 0; i < size; i++) {
      res.add(_data.readString());
    }
    return res;
  }

  double readDouble() {
    double d = _data.readDouble();
    //print('r double: ' + d.toString());
    return d;
  }

  int readInteger() {
    int i = _data.readInt();
    //print('r int: ' + i.toString());
    return i;
  }

  List<int> readIntegerList() {
    List<int> res = [];
    int size = _data.readInt();
    //print('r int list: ' + size.toString());
    for (int i = 0; i < size; i++) {
      res.add(_data.readInt());
    }
    return res;
  }

  bool readBoolean() {
    bool b = _data.readBool();
    //print('r bool: ' + b.toString());
    return b;
  }

  List<bool> readBooleanList() {
    List<bool> res = [];
    int size = _data.readInt();
    //print('r bool list: ' + size.toString());
    for (int i = 0; i < size; i++) {
      res.add(_data.readBool());
    }
    return res;
  }

  T readEnum<T>() {
    int type = readInteger();
    if (type == -1) {
      //print('r enum null');
      return null;
    }
    int field = readInteger();
    T e = D3ETemplate.getEnumField(type, field);
    //print('r enum: ' + e.toString());
    return e;
  }

  List<T> readEnumList<T>() {
    List<T> list = [];
    //print('r enum list: ');
    int size = readInteger();
    for (int i = 0; i < size; i++) {
      list.add(readEnum());
    }
    return list;
  }

  DFile readDFile() {
    String id = readString();
    if (id == null) {
      //print('r dfile: null');
      return null;
    }
    //print('r dfile: ' + id);
    DFile file = new DFile();
    file.id = id;
    file.name = readString();
    file.size = readInteger();
    file.mimeType = readString();
    return file;
  }

  List<DFile> readDFileList() {
    List<DFile> list = [];
    int size = readInteger();
    //print('r dfile list: ' + size.toString());
    for (int i = 0; i < size; i++) {
      list.add(readDFile());
    }
    return list;
  }

  List<T> readRefList<T extends DBObject>() {
    List<T> list = [];
    int size = readInteger();
    //print('r dfile list: ' + size.toString());
    for (int i = 0; i < size; i++) {
      list.add(readRef(-1, null));
    }
    return list;
  }

  T readRef<T extends DBObject>(int fieldIdx, DBObject parent) {
    int typeIndex = readInteger();
    if (typeIndex == -1) {
      return null;
    }
    D3ETemplateType type = D3ETemplate.types[typeIndex];
    //print('r ref: ' + type.name);
    DBObject obj = type.embedded
        ? parent.get(fieldIdx)
        : _fromId(readInteger(), typeIndex);
    bool locked = obj.lockedChanges();
    obj.lock();
    int fieldIndex = readInteger();
    while (fieldIndex != -1) {
      //print('r fieldIndex: ' + fieldIndex.toString());
      D3ETemplateField field = type[fieldIndex];
      //print('r field: ' + field.name);
      if (field.collection) {
        if (obj.d3eChanges.contains(fieldIndex)) {
          List val = obj.d3eChanges.getValue(fieldIndex);
          List newList = _readList(field, fieldIndex, val, obj);
          obj.d3eChanges.replaceValue(fieldIndex, newList);
        } else {
          List val = obj.get(fieldIndex);
          List newList = _readList(field, fieldIndex, val, obj);
          obj.set(fieldIndex, newList);
        }
      } else {
        Object val = _readField(field, fieldIndex, obj);
        if (obj.d3eChanges.contains(fieldIndex)) {
          obj.d3eChanges.replaceValue(fieldIndex, val);
        } else {
          obj.set(fieldIndex, val);
        }
      }
      fieldIndex = readInteger();
    }
    if (!locked) {
      obj.unlock();
    }
    return obj;
  }

  List _readList(
      D3ETemplateField field, int fieldIdx, List list, DBObject parent) {
    List newList = List.from(list);
    int count = readInteger();
    //print('r list ${field.name} ' + count.toString());
    if (count == 0) {
      newList.clear();
    } else if (count > 0) {
      newList.clear();
      while (count > 0) {
        newList.add(_readField(field, fieldIdx, parent));
        count--;
      }
    } else if (count < 0) {
      //print('List Changes: size: ' + newList.length.toString());
      count = -count;
      //print('Total changes: ' + count.toString());
      while (count > 0) {
        int index = readInteger();
        if (index > 0) {
          index--;
          // added
          Object val = _readField(field, fieldIdx, parent);
          if (index == newList.length) {
            //print('Added at : ' + index.toString());
            newList.add(val);
          } else {
            //print('Insert at : ' + index.toString());
            newList.insert(index, val);
          }
        } else {
          // removed
          index = -index;
          index--;
          //print('Removed at : ' + index.toString());
          newList.removeAt(index);
        }
        count--;
      }
      //print('List changes done: ' + newList.length.toString());
    }
    return newList;
  }

  Object _readField(D3ETemplateField field, int fieldIdx, DBObject parent) {
    switch (field.fieldType) {
      case D3EFieldType.String:
        return readString();
      case D3EFieldType.Integer:
        return readInteger();
      case D3EFieldType.Double:
        return readDouble();
      case D3EFieldType.Boolean:
        return readBoolean();
      case D3EFieldType.Date:
        int year = readInteger();
        if (year == -1) {
          return null;
        }
        int month = readInteger();
        int dayOfMonth = readInteger();
        return Date.of(year, month, dayOfMonth);

      case D3EFieldType.DateTime:
        int millisecondsSinceEpoch = readInteger();
        if (millisecondsSinceEpoch == -1) {
          return null;
        }
        return DateTime.fromMillisecondsSinceEpoch(millisecondsSinceEpoch);
      case D3EFieldType.Time:
        int millisecondsSinceEpoch = readInteger();
        if (millisecondsSinceEpoch == -1) {
          return null;
        }
        return Time.fromDateTime(
            DateTime.fromMillisecondsSinceEpoch(millisecondsSinceEpoch));
      case D3EFieldType.Duration:
        int inMilliseconds = readInteger();
        if (inMilliseconds == -1) {
          return null;
        }
        return Duration(milliseconds: inMilliseconds);
      case D3EFieldType.DFile:
        return readDFile();
      case D3EFieldType.Enum:
        return readInteger();
      case D3EFieldType.Ref:
        return readRef(fieldIdx, parent);
    }
    return null;
  }

  DBObject _fromId(int id, int type) {
    DBObject obj = _catch.findObject(type, id);
    if (obj == null) {
      obj = D3ETemplate.types[type].creator();
      obj.id = id;
      if (id > 0) {
        obj.saveStatus = DBSaveStatus.Saved;
      }
      _catch.addObject(obj);
    }
    return obj;
  }
}
