@echo off

if not exist "%JAVA_HOME%\bin\java.exe" echo Please set the JAVA_HOME variable in your environment, We need java(x64)! jdk8 or later is better! & EXIT /B 1
set "JAVA=%JAVA_HOME%\bin\java.exe"

setlocal enabledelayedexpansion

set BASE_DIR=%~dp0
rem added double quotation marks to avoid the issue caused by the folder names containing spaces.
rem removed the last 5 chars(which means \bin\) to get the base DIR.
set BASE_DIR="%BASE_DIR:~0,-5%"

set CUSTOM_SEARCH_LOCATIONS=file:%BASE_DIR%/conf/


set SERVER=tcc-transaction-dashboard
set SERVER_INDEX=-1


set i=0
for %%a in (%*) do (
    if "%%a" == "-s" ( set /a SERVER_INDEX=!i!+1 )
    set /a i+=1
)

set i=0
for %%a in (%*) do (
    if %SERVER_INDEX% == !i! (set SERVER="%%a")
    set /a i+=1
)

set "JAVA_OPTS=-server -Xms2g -Xmx2g -Xmn1g -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=%BASE_DIR%\logs\java_heapdump.hprof -XX:-UseLargePages"
set "JAVA_OPTS=%JAVA_OPTS% -Dtcc.home=%BASE_DIR%"
set "JAVA_OPTS=%JAVA_OPTS% -jar %BASE_DIR%\lib\%SERVER%.jar"
set "JAVA_OPTS=%JAVA_OPTS% --spring.config.additional-location=%CUSTOM_SEARCH_LOCATIONS%"
set "JAVA_OPTS=%JAVA_OPTS% --logging.config=%BASE_DIR%/conf/logback.xml"

set COMMAND="%JAVA%" %JAVA_OPTS% tcc.dashboard %*

%COMMAND%
