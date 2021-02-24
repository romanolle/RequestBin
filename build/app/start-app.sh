#! /bin/sh
cd /opt/request-bin/
exec java -Djava.security.egd=file:/dev/./urandom -jar -Dfile.encoding=UTF-8 /opt/request-bin/request-bin-1.0.0.BETA-1.jar --logging.level.com.requestbin=DEBUG

