SET scriptpath=%~dp0

java --class-path %scriptpath%..\app\build\intermediates\javac\debug\classes alexman.yamca.server.Server %1 %2 %3 %4 %5 %6 %7 %8 %9
