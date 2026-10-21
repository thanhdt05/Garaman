@echo off
title Tomcat 9 - GaraMan
set "CATALINA_HOME=E:\java\apache-tomcat-9.0.86"
set "CATALINA_BASE=E:\java\apache-tomcat-9.0.86"
set "JAVA_HOME=C:\Program Files\Java\jdk-25.0.2"
set "JAVA_OPTS=-Dfile.encoding=UTF-8"
cd /d "%CATALINA_HOME%\bin"
call catalina.bat run
pause
