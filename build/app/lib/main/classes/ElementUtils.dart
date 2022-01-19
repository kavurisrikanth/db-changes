import 'dart:html';

class ElementUtils {
  static void removeInitialLoader() {
    removeElemetntWithId('wrapper');
  }

  static void removeElemetntWithId(String id) {
    final divElement = document.getElementsByClassName(id);
    if (divElement.isNotEmpty) {
      divElement.first.remove();
    }
  }
}
