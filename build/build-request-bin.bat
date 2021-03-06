@echo off
set requestbin=request-bin
set name=%requestbin%.zip

if exist %requestbin% rmdir %requestbin% /S /Q
mkdir %requestbin%
if exist %name% del %name%

cd ..\backend
call mvn clean install -DskipTests
copy target\%requestbin%-1.0.0.BETA-1.jar ..\build\%requestbin%

cd ..\build
xcopy ..\frontend %requestbin%\%requestbin% /E /Y /I

cd app
copy daemon.sh ..\%requestbin%
copy start-app.sh ..\%requestbin%
cd ..
::call jar -cf %requestbin%.zip %requestbin%
::call 7z.exe -t7z -bd a %requestbin%.zip %requestbin%
call 7z a -tzip %name% %requestbin%

rmdir %requestbin% /S /Q
pause
