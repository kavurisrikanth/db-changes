import '../classes/DFile.dart';
import '../classes/Date.dart';
import '../classes/Time.dart';
import '../rocket/D3ETemplate.dart';
import '../rocket/D3ETemplateTypes.dart';
import '../utils/DBObject.dart';
import '../utils/ReferenceCatch.dart';

import '../classes/ExpressionString.dart';
import '../classes/BlockString.dart';

class JSONUtils {
  static ExpressionString stringToExpressionString(String eString) {
    if (eString == null) {
      return null;
    }

    return ExpressionString(eString);
  }

  static String expressionStringToString(ExpressionString eString) {
    return eString?.content;
  }

  static BlockString stringToBlockString(String bString) {
    if (bString == null) {
      return null;
    }

    return BlockString(bString);
  }

  static String blockStringToString(BlockString bString) {
    return bString?.content;
  }

  static Map toJson(DBObject obj) {
    if (obj == null) {
      return null;
    }
    Map<String, Object> result = Map();
    String typeStr = obj.d3eType;
    result['__typename'] = obj.d3eType;
    result['id'] = obj.id;
    int typeIndex = D3ETemplate.typeInt(typeStr);
    D3ETemplateType type = D3ETemplate.types[typeIndex];
    for (int i = 0; i < type.fields.length + type.parentFields; i++) {
      D3ETemplateField tf = type[i];
      Object val = obj.get(i);
      switch (tf.fieldType) {
        case D3EFieldType.String:
        case D3EFieldType.Integer:
        case D3EFieldType.Double:
        case D3EFieldType.Boolean:
        case D3EFieldType.Enum:
          result[tf.name] = val;
          break;
        case D3EFieldType.Date:
          if (tf.collection) {
            List list = val;
            result[tf.name] = list.map((e) => _toDateJson(e)).toList();
          } else {
            result[tf.name] = _toDateJson(val);
          }
          break;
        case D3EFieldType.DateTime:
          if (tf.collection) {
            List list = val;
            result[tf.name] = list.map((e) => _toDateTimeJson(e)).toList();
          } else {
            result[tf.name] = _toDateTimeJson(val);
          }
          break;
        case D3EFieldType.Time:
          if (tf.collection) {
            List list = val;
            result[tf.name] = list.map((e) => _toTimeJson(e)).toList();
          } else {
            result[tf.name] = _toTimeJson(val);
          }
          break;
        case D3EFieldType.Duration:
          if (tf.collection) {
            List list = val;
            result[tf.name] = list.map((e) => _toDurationJson(e)).toList();
          } else {
            result[tf.name] = _toDurationJson(val);
          }
          break;
        case D3EFieldType.DFile:
          if (tf.collection) {
            List list = val;
            result[tf.name] = list.map((e) => _toDFileJson(e)).toList();
          } else {
            result[tf.name] = _toDFileJson(val);
          }
          break;
        case D3EFieldType.Ref:
          if (tf.child) {
            if (tf.collection) {
              List list = val;
              result[tf.name] = list.map((e) => toJson(e)).toList();
            } else {
              result[tf.name] = toJson(val);
            }
          } else {
            if (tf.collection) {
              List list = val;
              result[tf.name] = list.map((e) => _toRefJson(e)).toList();
            } else {
              result[tf.name] = _toRefJson(val);
            }
          }
          break;
      }
    }
    return result;
  }

  static Map _toRefJson(DBObject obj) {
    if (obj == null) {
      return null;
    }
    Map<String, Object> result = Map();
    result['__typename'] = obj.d3eType;
    result['id'] = obj.id;
    return result;
  }

  static DBObject _fromRefJson(Map obj) {
    if (obj == null) {
      return null;
    }
    ReferenceCatch catche = ReferenceCatch.get();
    int type = D3ETemplate.typeInt(obj['__typename']);
    DBObject res = catche.findObject(type, obj['id']);
    if (res == null) {
      res = D3ETemplate.types[type].creator();
      res.id = obj['id'];
      catche.addObject(res);
    }
    return res;
  }

  static Map _toDFileJson(DFile obj) {
    if (obj == null) {
      return null;
    }
    Map<String, Object> result = Map();
    result['id'] = obj.id;
    result['name'] = obj.name;
    result['size'] = obj.size;
    return result;
  }

  static DFile _fromDFileJson(Map obj) {
    if (obj == null) {
      return null;
    }
    DFile dfile = DFile();
    dfile.id = obj['id'];
    dfile.name = obj['name'];
    dfile.size = obj['size'];
    return dfile;
  }

  static String _toDateTimeJson(DateTime val) {
    return val?.toIso8601String();
  }

  static DateTime _fromDateTimeJson(String val) {
    return DateTime.parse(val + 'Z');
  }

  static String _toTimeJson(Time val) {
    return val?.toString();
  }

  static Time _fromTimeJson(String val) {
    return Time.parse(val);
  }

  static String _toDateJson(Date val) {
    return val?.toString();
  }

  static Date _fromDateJson(String val) {
    return Date.parse(val);
  }

  static int _toDurationJson(Duration val) {
    return val?.inMilliseconds;
  }

  static Duration _fromDurationJson(int val) {
    return Duration(milliseconds: val);
  }

  static DBObject fromJson(Map map) {
    if (map == null) {
      return null;
    }
    DBObject obj = _fromRefJson(map);
    String typeStr = map['__typename'];
    int typeIndex = D3ETemplate.typeInt(typeStr);
    D3ETemplateType type = D3ETemplate.types[typeIndex];
    for (int i = 0; i < type.fields.length + type.parentFields; i++) {
      D3ETemplateField tf = type[i];
      Object val = map[tf.name];
      if (val == null) {
        obj.set(i, null);
        continue;
      }
      Object res = val;
      switch (tf.fieldType) {
        case D3EFieldType.String:
        case D3EFieldType.Integer:
        case D3EFieldType.Double:
        case D3EFieldType.Boolean:
        case D3EFieldType.Enum:
          res = val;
          break;
        case D3EFieldType.Date:
          if (tf.collection) {
            List list = val;
            res = list.map((e) => _fromDateJson(e)).toList();
          } else {
            res = _toDateJson(val);
          }
          break;
        case D3EFieldType.DateTime:
          if (tf.collection) {
            List list = val;
            res = list.map((e) => _fromDateTimeJson(e)).toList();
          } else {
            res = _toDateTimeJson(val);
          }
          break;
        case D3EFieldType.Time:
          if (tf.collection) {
            List list = val;
            res = list.map((e) => _fromTimeJson(e)).toList();
          } else {
            res = _toTimeJson(val);
          }
          break;
        case D3EFieldType.Duration:
          if (tf.collection) {
            List list = val;
            res = list.map((e) => _fromDurationJson(e)).toList();
          } else {
            res = _fromDurationJson(val);
          }
          break;
        case D3EFieldType.DFile:
          if (tf.collection) {
            List list = val;
            res = list.map((e) => _fromDFileJson(e)).toList();
          } else {
            res = _fromDFileJson(val);
          }
          break;
        case D3EFieldType.Ref:
          if (tf.child) {
            if (tf.collection) {
              List list = val;
              res = list.map((e) => fromJson(e)).toList();
            } else {
              res = fromJson(val);
            }
          } else {
            if (tf.collection) {
              List list = val;
              res = list.map((e) => _fromRefJson(e)).toList();
            } else {
              res = _fromRefJson(val);
            }
          }
          break;
      }
      obj.set(i, res);
    }
    return obj;
  }
}
