import 'package:flutter_test/flutter_test.dart';
import 'package:task_app/main.dart';

void main() {
  testWidgets('App loads login screen', (WidgetTester tester) async {
    await tester.pumpWidget(const TaskApp());
    expect(find.text('Task Manager'), findsOneWidget);
    expect(find.text('Login'), findsOneWidget);
  });
}
