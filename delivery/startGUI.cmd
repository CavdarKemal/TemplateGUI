@ECHO OFF
REM SET DEBUG_OPT=
SET DEBUG_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
SET JVM_ARGS=-Dfile.encoding=UTF8 -Xms512m -Xmx2g -XX:+HeapDumpOnOutOfMemoryError -classpath ".;lib/*"
SET MAIN_CLASS=de.cavdar.gui.Main
SET AUFRUF=java %DEBUG_OPTS% %JVM_ARGS% %MAIN_CLASS% %CONFIG_FILE%
ECHO Starting %MAIN_CLASS%...
%AUFRUF%
IF ERRORLEVEL 1 (
  ECHO.
  ECHO Fehler beim Starten! Druecken Sie eine Taste...
  PAUSE >nul
)
