@echo off


set "destination=H:\ITUniversity\INFORMATIQUE\S4\Spring\Sprint\Sprint0\Test\lib"
set "LIB_SERVLET=C:\Program Files\Java\jdk1.8.0_361\Tomcat9x\lib"

set "java_path=H:\ITUniversity\INFORMATIQUE\S4\Spring\Sprint\Sprint0\FrameWork\src"
set "classes=H:\ITUniversity\INFORMATIQUE\S4\Spring\Sprint\Sprint0\FrameWork\classes"

cd "%java_path%"
javac -cp "%LIB_SERVLET%\*" -d "%classes%" *.java 

cd "%classes%"
jar cf frst_framework.jar ./*

copy "*.jar" "%destination%"


cd "../"

pause
