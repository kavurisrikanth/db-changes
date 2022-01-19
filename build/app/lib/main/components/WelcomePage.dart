import '../utils/ObservableState.dart';
import '../utils/ObjectObservable.dart';
import '../classes/DBResult.dart';
import '../models/ChildModel.dart';
import '../models/Thing.dart';
import 'Button.dart';
import 'ThemeWrapper.dart';
import 'package:flutter/widgets.dart';

typedef void _ButtonOnPressed(WelcomePageRefs state);

class WelcomePage extends StatefulWidget {
  WelcomePage({Key key}) : super(key: key);
  @override
  _WelcomePageState createState() => _WelcomePageState();
}

/// To store state data for WelcomePage
class WelcomePageRefs {
  ButtonState button = ButtonState();
  WelcomePageRefs();
}

class ButtonState extends ObjectObservable {
  bool _hover = false;
  ButtonState();
  bool get hover {
    return _hover;
  }

  setHover(bool val) {
    bool isValChanged = _hover != val;

    if (!isValChanged) {
      return;
    }

    _hover = val;

    fire('hover', this);
  }
}

class ButtonWithState extends StatefulWidget {
  final WelcomePageRefs state;
  final _ButtonOnPressed onPressedbuttonHandler;
  ButtonWithState({Key key, this.state, this.onPressedbuttonHandler})
      : super(key: key);
  @override
  _ButtonWithState createState() => _ButtonWithState();
}

class _ButtonWithState extends ObservableState<ButtonWithState> {
  @override
  initState() {
    super.initState();

    updateObservable('button', null, button);

    initListeners();

    enableBuild = true;
  }

  void initListeners() {
    this.on(['button', 'button.hover'], rebuild);
  }

  void buttonOnEnter(event) => button.setHover(true);
  void buttonOnExit(event) => button.setHover(false);
  _ButtonOnPressed get onPressedbuttonHandler =>
      this.widget.onPressedbuttonHandler;
  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    var cStyle = ThemeWrapper.of(context);

    return MouseRegion(
        child: DefaultTextStyle(
            style: (button.hover
                    ? cStyle.tButtonPrimaryButtonTextStyleOnHover
                    : cStyle.tButtonPrimaryButtonTextStyleOn) ??
                TextStyle(),
            child: Container(
                constraints: cStyle.tButtonPrimaryButtonConstraintsOn,
                child: Button(
                    onPressed: () {
                      onPressedbuttonHandler(state);
                    },
                    backgroundColor: button.hover
                        ? cStyle.tButtonPrimaryButtonBackgroundColorOnHover
                        : cStyle.tButtonPrimaryButtonBackgroundColorOn,
                    child: Text('Create')))),
        onEnter: (event) {
          buttonOnEnter(event);
        },
        onExit: (event) {
          buttonOnExit(event);
        });
  }

  WelcomePageRefs get state => widget.state;
  ButtonState get button => widget.state.button;
}

class _WelcomePageState extends ObservableState<WelcomePage> {
  WelcomePageRefs state = WelcomePageRefs();
  Thing _thing;
  @override
  initState() {
    super.initState();

    initListeners();

    enableBuild = true;

    onInit();
  }

  void initListeners() {}
  Thing get thing {
    return _thing;
  }

  void setThing(Thing val) {
    bool isValChanged = _thing != val;

    if (!isValChanged) {
      return;
    }

    updateObservable('thing', _thing, val);

    _thing = val;

    this.fire('thing', this);
  }

  @override
  Widget build(BuildContext context) {
    var cStyle = ThemeWrapper.of(context);

    return Column(children: [
      Text('Welcome'),
      ButtonWithState(
          state: state, onPressedbuttonHandler: onPressedbuttonHandler)
    ]);
  }

  void onInit() {
    /*
Your code here. 
*/

    this.setThing(Thing(
        msg: 'Hello',
        child: ChildModel(num: 1),
        childColl: [ChildModel(num: 2), ChildModel(num: 3)]));
  }

  void onPressedbuttonHandler(WelcomePageRefs state) async {
    /*
Your code here. 
*/

    DBResult res = (await this.thing.save());

    res = (await this.thing.save());
  }

  ButtonState get button => state.button;
}
