class BlockString {
  String content;

  Object attachment;

  BlockString(String content) {
    this.content = content;
  }

  String getContent() {
    return content;
  }

  void setAttachment(Object attachment) {
    this.attachment = attachment;
  }

  Object getAttachment() {
    return attachment;
  }
}
