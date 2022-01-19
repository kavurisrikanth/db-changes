class ExpressionString {
  String _content;

  Object _attachment;

  ExpressionString(String content) {
    this._content = content;
  }

  String get content {
    return _content;
  }

  set attachment(Object attachment) {
    this._attachment = attachment;
  }

  Object get attachment {
    return _attachment;
  }
}
