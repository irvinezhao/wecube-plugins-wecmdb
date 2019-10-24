from platten/alpine-oracle-jre8-docker
LABEL maintainer = "Webank CTB Team"
ADD wecube-plugins-wecmdb/target/wecube-plugins-wecmdb-*-SNAPSHOT.jar /application/wecube-plugins-wecmdb.jar
ADD build/start.sh /scripts/start.sh
RUN chmod +x /scripts/start.sh
CMD ["/bin/sh","-c","/scripts/start.sh"]

