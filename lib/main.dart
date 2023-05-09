import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) => const MaterialApp(home: RootPage());
}

const channel = 'com.ucg.channel';
const platformChannel = MethodChannel(channel);

class RootPage extends StatefulWidget {
  const RootPage({Key? key}) : super(key: key);

  @override
  State<RootPage> createState() => _RootPageState();
}

class _RootPageState extends State<RootPage> {
  String? param = '';

  Widget currentScreen = Container(height: 200.0, width: 200.0, color: Colors.amber);
  String title = 'Default Page';

  @override
  void initState() {
    super.initState();
    platformChannel.setMethodCallHandler(_triggerFromNative);
  }

  Future<void> _triggerFromNative(MethodCall call) async {
    log(call.method, name: 'METHOD');
    if (call.method == 'notifyNavToFlutter') {
      title = call.arguments.toString();
      SystemChrome.setSystemUIOverlayStyle(const SystemUiOverlayStyle(statusBarColor: Colors.transparent));
      _getParam();
      setState(() {});
    }
  }

  void _exitFlutter() {
    platformChannel.invokeMethod('exitFlutter', 'Exit the flutter app by ucg');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(title),
        centerTitle: true,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: _exitFlutter,
        ),
      ),
      body: currentScreen,
    );
  }

  Future<void> _getParam() async {
    try {
      final int result = await platformChannel.invokeMethod('getUserId');
      param = result.toString();
      log(param.toString());
    } on PlatformException catch (e) {
      param = 'Failed to get param: ${e.message}';
      log(param.toString());
    }

    setState(() {
      currentScreen = Container(
        height: 200.0,
        width: 200.0,
        color: Colors.amber,
        child: Center(child: Text(param ?? 'null')),
      );
    });
  }
}
