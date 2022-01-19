class IdGenerator {
  static int _id = 0;
  static IdGenerator ins;

  IdGenerator._init();

  factory IdGenerator.get() {
    if (ins == null) {
      ins = IdGenerator._init();
    }
    return ins;
  }

  int next() {
    _id++;
    return _id;
  }
}
