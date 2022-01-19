class WrappedType extends Type {
  Type _outer;
  List<Type> _subs;
  WrappedType(Type outer, List<Type> args) : super(outer.name) {
    this._outer = outer;
    this._subs = args;
  }

  Type get outer {
    return _outer;
  }

  List<Type> get subs {
    return _subs;
  }
}

class MethodType extends Type {
  Type _on;
  Type _gen;

  MethodType(Type _on, String name, Type _gen) : super(name) {
    this._on = _on;
    this._gen = _gen;
  }

  Type get onValue {
    return _on;
  }

  Type get gen {
    return _gen;
  }
}

class Type {
  String _name;
  Type(this._name);

  String get name {
    return _name;
  }

  static Type wrap(Type outer, List<Type> args) {
    return WrappedType(outer, args);
  }

  static Type find(String name) {
    return Type(name);
  }

  static Type methodType(Type on, String name, Type gen) {
    return MethodType(on, name, gen);
  }
}
