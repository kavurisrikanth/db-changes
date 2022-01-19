import 'dart:html' hide Storage;
import 'dart:async';

typedef void EventHandler(KeyboardEvent e);

class BrowserUtil {
  static bool enabled = true;
  static StreamSubscription listener;
  static StreamSubscription wheelListener;
  static void preventBrowserEvents() {
    listener?.cancel();
    listener = window.onKeyDown.listen((event) {
      if (enabled && event.ctrlKey || event.altKey) {
        event.preventDefault();
      }
    });
  }

  static void preventMouseWheelEvents() {
    wheelListener?.cancel();
    wheelListener = window.onWheel.listen((event) {
      if (enabled && event.ctrlKey) {
        event.preventDefault();
      }
    });
  }

  static set enable(bool val) {
    enabled = val;
  }

  static void restoreBrowserEvents() {
    listener?.cancel();
  }

  static void restoreMouseWheelEvents() {
    wheelListener?.cancel();
  }
}
