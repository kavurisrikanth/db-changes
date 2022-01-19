import 'D3ETemplate.dart';

import '../classes/core.dart';
import '../utils/DBObject.dart';

enum D3EFieldType {
  String,
  Integer,
  Double,
  Boolean,
  Date,
  DateTime,
  Time,
  Duration,
  DFile,
  Enum,
  Ref,
}

enum D3ERefType { Model, Struct, Enum }

class D3ETemplateField {
  final String name;
  final int type;
  final bool child;
  final bool collection;
  final bool inverse;
  bool unknown = false;
  final D3EFieldType fieldType;
  D3ETemplateField(this.name, this.type, this.fieldType,
      {this.child = false, this.collection = false, this.inverse = false});
}

class D3EUsage {
  String name;
  String hash;
  List<D3ETypeUsage> types;
  D3EUsage(this.name, this.types, this.hash);
}

class D3ETypeUsage {
  int type;
  List<D3EFieldUsage> fields;
  D3ETypeUsage(this.type, this.fields);
}

class D3EFieldUsage {
  int field;
  List<D3ETypeUsage> types;
  D3EFieldUsage(this.field, this.types);
}

class D3ETemplateType {
  final String name;
  final List<D3ETemplateField> fields;
  Map<String, int> fieldMap;
  final bool embedded;
  final int parent;
  final bool abstract;
  final int parentFields;
  final String hash;
  final D3ERefType refType;
  final Supplier<DBObject> creator;
  bool unknown = false;
  D3ETemplateType(this.name, this.hash, this.fields,
      {this.embedded = false,
      this.parent = 0,
      this.abstract = false,
      this.refType,
      this.parentFields = 0,
      this.creator}) {
    fieldMap = Map.fromIterables(fields.map((x) => x.name),
        List.generate(fields.length, (index) => index + this.parentFields));
  }

  D3ETemplateField operator [](int index) {
    if (index < parentFields) {
      return D3ETemplate.types[parent][index];
    }
    return fields[index - parentFields];
  }
}

class D3ETemplateClass {
  final String name;
  final String hash;
  final List<D3ETemplateMethod> methods;
  D3ETemplateClass(this.name, this.hash, this.methods);
}

class D3ETemplateMethod {
  final String name;
  final List<D3ETemplateParam> params;
  D3ETemplateMethod(this.name, this.params);
}

class D3ETemplateMethodWithReturn extends D3ETemplateMethod {
  final int returnType;
  final bool returnCollection;

  D3ETemplateMethodWithReturn(
      String name, List<D3ETemplateParam> params, this.returnType,
      {this.returnCollection = false})
      : super(name, params);

  // void will be -1
  bool isVoidReturn() => this.returnType == -1;
}

class D3ETemplateParam {
  final int type;
  final bool collection;
  D3ETemplateParam(this.type, {this.collection = false});
}
