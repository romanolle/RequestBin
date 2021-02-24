#! /bin/sh


NAME=request-bin
JAR=$NAME-1.0.0.BETA-1.jar
VIEW_FOLDER=/var/www/$NAME/
BACKEND_FOLDER=/opt/$NAME/
DAEMON=daemon.sh
RUNNER=start-app.sh



unzip -o ./$NAME.zip
cp ./$NAME/$DAEMON /etc/init.d/$NAME -f
chown root /etc/init.d/$NAME
chmod 775 /etc/init.d/$NAME

ln -sf /etc/init.d/$NAME /etc/rc0.d/K02$NAME
ln -sf /etc/init.d/$NAME /etc/rc1.d/K02$NAME
ln -sf /etc/init.d/$NAME /etc/rc6.d/K02$NAME
ln -sf /etc/init.d/$NAME /etc/rc2.d/S17$NAME
ln -sf /etc/init.d/$NAME /etc/rc3.d/S17$NAME
ln -sf /etc/init.d/$NAME /etc/rc4.d/S17$NAME
ln -sf /etc/init.d/$NAME /etc/rc5.d/S17$NAME

#systemctl daemon-reload
/etc/init.d/$NAME stop

mkdir $BACKEND_FOLDER -p

#delete all files (only files not subfolders) in folder 
find $BACKEND_FOLDER -maxdepth 1 -type f -delete

cp -R $NAME/$JAR $BACKEND_FOLDER$JAR -f

cp $NAME/$RUNNER $BACKEND_FOLDER$NAME -f

chmod -R 775 $BACKEND_FOLDER

rm -rf $VIEW_FOLDER$NAME
mkdir $VIEW_FOLDER -p
cp -ar $NAME/$NAME/. $VIEW_FOLDER
chmod -R 775 $VIEW_FOLDER

rm -rf ./$NAME


#systemctl daemon-reload
/etc/init.d/$NAME start


