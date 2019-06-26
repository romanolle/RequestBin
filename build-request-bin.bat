@echo off
set ucloud=ucloud
set name=%ucloud%.zip

if exist %ucloud% rmdir %ucloud% /S /Q
mkdir %ucloud%
if exist %name% del %name%
cd ..\backend
call mvn clean install -DskipTests
copy target\ucloud-1.4.0.DEV.jar ..\build\ucloud

cd ..\frontend
call grunt build
xcopy build\ucloud ..\build\ucloud\ucloud /E /Y /I

cd ..\build
copy daemon.sh %ucloud%
copy daemon-test.sh %ucloud%
copy daemon-prelife.sh %ucloud%
copy start-app.sh %ucloud%
copy start-app-test.sh %ucloud%
copy start-app-prelife.sh %ucloud%
copy functions %ucloud%
::call jar -cf %ucloud%.zip %ucloud%
::call 7z.exe -t7z -bd a %ucloud%.zip %ucloud%
call 7z a -tzip %name% %ucloud%

rmdir %ucloud% /S /Q
pause
