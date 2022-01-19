import '../rocket/D3ETemplate.dart';
import 'WSReader.dart';

class Channels {
  static void onMessage(WSReader reader) {
    int channel = reader.readInteger();

    switch (channel) {
    }
  }

  static void reset() {}
}
