#! /bin/sh
cd /opt/request-bin/
#exec java -Dcom.datastax.driver.USE_NATIVE_CLOCK=false -jar -Dfile.encoding=UTF-8 ./ucloud-1.4.0.DEV.jar --server.url=https://remote.uui-platform.com/ --logging.level.com.unitron.ucloud=WARN --ajp.port=8011 --db.schema=uui_remote_access_gen_2 --resource.external.location.producttools=https://remote.uui-platform.com/webtool/
exec java -Djava.security.egd=file:/dev/./urandom -jar -Dfile.encoding=UTF-8 /opt/request-bin/request-bin-1.0.0.DEV.jar --logging.level.com.requestbin=DEBUG

