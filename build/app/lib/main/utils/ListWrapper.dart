import 'dart:math';

typedef void ListChangeListener();

class ListWrapper<T> implements List<T> {
  List<T> _base = [];
  ListChangeListener _listener;
  ListWrapper(this._listener);
  @override
  T get first {
    return _base.first;
  }

  @override
  T get last {
    return _base.last;
  }

  @override
  int get length {
    return _base.length;
  }

  @override
  List<T> operator +(List<T> other) {
    return _base + other;
  }

  @override
  T operator [](int index) {
    return _base[index];
  }

  @override
  void operator []=(int index, T value) {
    _listener();
    _base[index] = value;
  }

  @override
  void add(T value) {
    _listener();
    _base.add(value);
  }

  @override
  void addAll(Iterable<T> iterable) {
    _listener();
    _base.addAll(iterable);
  }

  @override
  bool any(bool Function(T element) test) {
    return _base.any(test);
  }

  @override
  Map<int, T> asMap() {
    return _base.asMap();
  }

  @override
  List<R> cast<R>() {
    return _base.cast();
  }

  @override
  void clear() {
    _listener();
    _base.clear();
  }

  @override
  bool contains(Object element) {
    return _base.contains(element);
  }

  @override
  T elementAt(int index) {
    return _base.elementAt(index);
  }

  @override
  bool every(bool Function(T element) test) {
    return _base.every(test);
  }

  @override
  Iterable<E> expand<E>(Iterable<E> Function(T element) f) {
    return _base.expand((x) => f(x));
  }

  @override
  void fillRange(int start, int end, [T fillValue]) {
    _listener();
    _base.fillRange(start, end, fillValue);
  }

  @override
  T firstWhere(bool Function(T element) test, {T Function() orElse}) {
    return _base.firstWhere(test, orElse: orElse);
  }

  @override
  E fold<E>(E initialValue, E Function(E previousValue, T element) combine) {
    return _base.fold(initialValue, combine);
  }

  @override
  Iterable<T> followedBy(Iterable<T> other) {
    return _base.followedBy(other);
  }

  @override
  void forEach(void Function(T element) f) {
    _base.forEach(f);
  }

  @override
  Iterable<T> getRange(int start, int end) {
    return _base.getRange(start, end);
  }

  @override
  int indexOf(T element, [int start = 0]) {
    return _base.indexOf(element);
  }

  @override
  int indexWhere(bool Function(T element) test, [int start = 0]) {
    return _base.indexWhere(test, start);
  }

  @override
  void insert(int index, T element) {
    _listener();
    _base.insert(index, element);
  }

  @override
  void insertAll(int index, Iterable<T> iterable) {
    _listener();
    _base.insertAll(index, iterable);
  }

  @override
  bool get isEmpty => _base.isEmpty;

  @override
  bool get isNotEmpty => _base.isNotEmpty;

  @override
  Iterator<T> get iterator => _base.iterator;

  @override
  String join([String separator = ""]) {
    return _base.join(separator);
  }

  @override
  int lastIndexOf(T element, [int start]) {
    return _base.lastIndexOf(element, start);
  }

  @override
  int lastIndexWhere(bool Function(T element) test, [int start]) {
    return _base.lastIndexWhere(test, start);
  }

  @override
  T lastWhere(bool Function(T element) test, {T Function() orElse}) {
    return _base.lastWhere(test, orElse: orElse);
  }

  @override
  Iterable<E> map<E>(E Function(T e) f) {
    return _base.map(f);
  }

  @override
  T reduce(T Function(T value, T element) combine) {
    return _base.reduce(combine);
  }

  @override
  bool remove(Object value) {
    _listener();
    return _base.remove(value);
  }

  @override
  T removeAt(int index) {
    _listener();
    return _base.removeAt(index);
  }

  @override
  T removeLast() {
    _listener();
    return _base.removeLast();
  }

  @override
  void removeRange(int start, int end) {
    _listener();
    _base.removeRange(start, end);
  }

  @override
  void removeWhere(bool Function(T element) test) {
    _listener();
    _base.removeWhere(test);
  }

  @override
  void replaceRange(int start, int end, Iterable<T> replacements) {
    _listener();
    _base.replaceRange(start, end, replacements);
  }

  @override
  void retainWhere(bool Function(T element) test) {
    _listener();
    _base.retainWhere(test);
  }

  @override
  Iterable<T> get reversed => _base.reversed;

  @override
  void setAll(int index, Iterable<T> iterable) {
    _listener();
    _base.setAll(index, iterable);
  }

  @override
  void setRange(int start, int end, Iterable<T> iterable, [int skipCount = 0]) {
    _listener();
    _base.setRange(start, end, iterable, skipCount);
  }

  @override
  void shuffle([Random random]) {
    _listener();
    _base.shuffle(random);
  }

  @override
  T get single => _base.single;

  @override
  T singleWhere(bool Function(T element) test, {T Function() orElse}) {
    return _base.singleWhere(test, orElse: orElse);
  }

  @override
  Iterable<T> skip(int count) {
    return _base.skip(count);
  }

  @override
  Iterable<T> skipWhile(bool Function(T value) test) {
    return _base.skipWhile(test);
  }

  @override
  void sort([int Function(T a, T b) compare]) {
    _listener();
    _base.sort(compare);
  }

  @override
  List<T> sublist(int start, [int end]) {
    return _base.sublist(start, end);
  }

  @override
  Iterable<T> take(int count) {
    return _base.take(count);
  }

  @override
  Iterable<T> takeWhile(bool Function(T value) test) {
    return _base.takeWhile(test);
  }

  @override
  List<T> toList({bool growable = true}) {
    return _base.toList(growable: growable);
  }

  @override
  Set<T> toSet() {
    return _base.toSet();
  }

  @override
  Iterable<T> where(bool Function(T element) test) {
    return _base.where(test);
  }

  @override
  Iterable<T> whereType<T>() {
    return _base.whereType();
  }

  @override
  set first(T value) {
    _listener();
    _base.first = value;
  }

  @override
  set last(T value) {
    _listener();
    _base.last = value;
  }

  @override
  set length(int newLength) {
    _listener();
    _base.length = newLength;
  }
}
