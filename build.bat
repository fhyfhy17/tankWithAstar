

set  MAVEN_HOME=D:\apache-maven-3.3.9-bin\apache-maven-3.3.9
set  JAVA_HOME=C:\Program Files\Java\jdk1.7.0_75
set  PATH=%PATH%;%MAVEN_HOME%\bin;%JAVA_HOME%\bin
call mvn-package.bat


XCOPY target\tank-0.0.1-SNAPSHOT-distribution.zip ..\..\deploy1\ /Y
pause