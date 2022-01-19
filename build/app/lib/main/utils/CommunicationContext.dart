import 'D3EObjectChanges.dart';
import 'DBObject.dart';

abstract class CommunicationContext {
  void done();
  void clear<T extends DBObject>();

  int get fields;
  int get nextField;
}
