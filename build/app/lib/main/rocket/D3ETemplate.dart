import 'D3ETemplateTypes.dart';
import '../classes/ConnectionStatus.dart';
import '../classes/DBResult.dart';
import '../classes/DBResultStatus.dart';
import '../classes/LoginResult.dart';
import '../models/ChildModel.dart';
import '../models/Thing.dart';

const int BOOLEAN = 0;

const int CHILDMODEL = 1;

const int CONNECTIONSTATUS = 2;

const int DBRESULT = 3;

const int DBRESULTSTATUS = 4;

const int DFILE = 5;

const int DATE = 6;

const int DATETIME = 7;

const int DOUBLE = 8;

const int DURATION = 9;

const int INTEGER = 10;

const int LOGINRESULT = 11;

const int STRING = 12;

const int THING = 13;

const int TIME = 14;

const int TYPE = 15;

const int USER = 16;

class UsageConstants {}

class ChannelConstants {
  static const int TOTAL_CHANNEL_COUNT = 0;
  static final List<D3ETemplateClass> channels = [];
}

class RPCConstants {
  static const int UniqueChecker = 0;
  static const int UNIQUECHECKER_PROCEDURE_COUNT = 0;
  static const int TOTAL_RPC_CLASS_COUNT = 1;
  static final List<D3ETemplateClass> classes = [
    D3ETemplateClass('UniqueChecker', 'be5c4e6b105f24c88457b0e6ca8f4db4', [])
  ];
}

class D3ETemplate {
  static String HASH = '3b098dc2d46ee9b944b60afa2d098e62';
  static List<D3EUsage> _usages = [];
  static List<D3ETemplateType> _types = [
    D3ETemplateType('Boolean', '27226c864bac7454a8504f8edb15d95b', []),
    D3ETemplateType('ChildModel', 'e7a135b18bdcb93da8c6081d473ff388',
        [D3ETemplateField('num', INTEGER, D3EFieldType.Integer)],
        refType: D3ERefType.Model, creator: () => ChildModel()),
    D3ETemplateType(
        'ConnectionStatus',
        '0d5c2bfbc6b6e414981c0c67321165d5',
        [
          D3ETemplateField('Connecting', 0, D3EFieldType.Enum),
          D3ETemplateField('Connected', 0, D3EFieldType.Enum),
          D3ETemplateField('ConnectionBusy', 0, D3EFieldType.Enum),
          D3ETemplateField('ConnectionNormal', 0, D3EFieldType.Enum),
          D3ETemplateField('ConnectionFailed', 0, D3EFieldType.Enum),
          D3ETemplateField('RestoreFailed', 0, D3EFieldType.Enum),
          D3ETemplateField('AuthFailed', 0, D3EFieldType.Enum)
        ],
        refType: D3ERefType.Enum),
    D3ETemplateType(
        'DBResult',
        '359ac3e2a501e2aa59756379c87e99b5',
        [
          D3ETemplateField('errors', STRING, D3EFieldType.String,
              collection: true),
          D3ETemplateField('status', DBRESULTSTATUS, D3EFieldType.Enum)
        ],
        refType: D3ERefType.Struct,
        creator: () => DBResult()),
    D3ETemplateType(
        'DBResultStatus',
        'b7ade0f723488459cca2566a1b4959b5',
        [
          D3ETemplateField('Success', 0, D3EFieldType.Enum),
          D3ETemplateField('Errors', 0, D3EFieldType.Enum)
        ],
        refType: D3ERefType.Enum),
    D3ETemplateType('DFile', '71a781845a8ebe8adf67352a573af199', [
      D3ETemplateField('id', STRING, D3EFieldType.String),
      D3ETemplateField('name', STRING, D3EFieldType.String),
      D3ETemplateField('size', INTEGER, D3EFieldType.Integer),
      D3ETemplateField('mimeType', STRING, D3EFieldType.String)
    ]),
    D3ETemplateType('Date', '44749712dbec183e983dcd78a7736c41', []),
    D3ETemplateType('DateTime', '8cf10d2341ed01492506085688270c1e', []),
    D3ETemplateType('Double', 'd909d38d705ce75386dd86e611a82f5b', []),
    D3ETemplateType('Duration', 'e02d2ae03de9d493df2b6b2d2813d302', []),
    D3ETemplateType('Integer', 'a0faef0851b4294c06f2b94bb1cb2044', []),
    D3ETemplateType(
        'LoginResult',
        '43b15c92fa28924318ec2dd9b20d65d3',
        [
          D3ETemplateField('failureMessage', STRING, D3EFieldType.String),
          D3ETemplateField('success', BOOLEAN, D3EFieldType.Boolean),
          D3ETemplateField('token', STRING, D3EFieldType.String),
          D3ETemplateField('userObject', USER, D3EFieldType.Ref)
        ],
        refType: D3ERefType.Struct,
        creator: () => LoginResult()),
    D3ETemplateType('String', '27118326006d3829667a400ad23d5d98', []),
    D3ETemplateType(
        'Thing',
        '84e9abc88f40a1ae744ed7508031aa5e',
        [
          D3ETemplateField('child', CHILDMODEL, D3EFieldType.Ref, child: true),
          D3ETemplateField('childColl', CHILDMODEL, D3EFieldType.Ref,
              child: true, collection: true)
        ],
        refType: D3ERefType.Model,
        creator: () => Thing()),
    D3ETemplateType('Time', 'a76d4ef5f3f6a672bbfab2865563e530', []),
    D3ETemplateType('Type', 'a1fa27779242b4902f7ae3bdd5c6d508', []),
    D3ETemplateType('User', '8f9bfe9d1345237cb3b2b205864da075', [],
        abstract: true, refType: D3ERefType.Model)
  ];
  static final Map<String, int> _typeMap = Map.fromIterables(
      _types.map((x) => x.name),
      List.generate(_types.length, (index) => index));
  static List<int> allFields(int type) {
    return List.generate(_types[type].fields.length, (index) => index);
  }

  static List<D3ETemplateType> get types {
    return _types;
  }

  static List<D3EUsage> get usages {
    return _usages;
  }

  static String typeString(int val) {
    return _types[val].name;
  }

  static int typeInt(String val) {
    return _typeMap[val];
  }

  static D3ETemplateField _getField(int type, int val) {
    D3ETemplateType _type = _types[type];

/*
_type will have fields with index starting from _type.parentFields.
Anything less needs to be looked up in _type.parent.
*/

    if (val < _type.parentFields) {
      return _getField(_type.parent, val);
    }

/*
The field cannot be in _type's child, so subtract _type.parentField from val, and use that as index.
*/

    int adjustedIndex = val - _type.parentFields;

    return _type.fields[adjustedIndex];
  }

  static String fieldString(int type, int val) {
    return _getField(type, val).name;
  }

  static int fieldType(int type, int val) {
    return _getField(type, val).type;
  }

  static bool isChild(int type, int val) {
    return _getField(type, val).child;
  }

  static int fieldInt(int type, String val) {
    D3ETemplateType _type = _types[type];

    if (_type.fieldMap.containsKey(val)) {
      return _type.fieldMap[val];
    }

    if (_type.parent != null) {
      return fieldInt(_type.parent, val);
    }

    return null;
  }

  static bool isEmbedded(int type) {
    return _types[type].embedded;
  }

  static bool isAbstract(int type) {
    return _types[type].abstract;
  }

  static bool hasParent(int type) {
    return _types[type].parent != null;
  }

  static int parent(int type) {
    return _types[type].parent;
  }

  static T getEnumField<T>(int type, int field) {
    switch (type) {
      case CONNECTIONSTATUS:
        {
          return ConnectionStatus.values[field] as T;
        }

      case DBRESULTSTATUS:
        {
          return DBResultStatus.values[field] as T;
        }
    }
  }
}
