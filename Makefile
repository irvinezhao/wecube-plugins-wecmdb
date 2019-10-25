current_dir=$(shell pwd)
version=$(shell bash ./build/version.sh)
date=$(shell date +%Y%m%d%H%M%S)
project_name=$(shell basename "${current_dir}")
remote_docker_image_registry=ccr.ccs.tencentyun.com/webankpartners/wecube-plugins-wecmdb


clean:
	rm -rf $(current_dir)/target

.PHONY:build
build:
	mkdir -p repository
	docker run --rm --name wecube-plugins-wecmdb-build -v /data/repository:/usr/src/mymaven/repository   -v $(current_dir)/build/maven_settings.xml:/usr/share/maven/ref/settings-docker.xml  -v $(current_dir):/usr/src/mymaven -w /usr/src/mymaven maven:3.3-jdk-8 mvn -U clean install -Dmaven.test.skip=true -s /usr/share/maven/ref/settings-docker.xml dependency:resolve

image: 
	docker build -t $(project_name):$(version) .

push:
	docker tag  $(project_name):$(version) $(remote_docker_image_registry):$(date)-$(version)
	docker push $(remote_docker_image_registry):$(date)-$(version)

s3_server_url=http://10.10.10.1:9000
s3_access_key=access_key
s3_secret_key=secret_key

.PHONY:package
package:
	rm -rf package
	mkdir -p package
	cd package && docker save $(project_name):$(version) -o image.tar
	cd package && cp ../register.xml .
	cd we-cmdb && git checkout 364_cmdb_ui_plugin && git pull
	cd we-cmdb && make build-plugin-ui
	cd we-cmdb/cmdb-ui/dist && zip -r ui.zip .
	cd package && cp ../we-cmdb/cmdb-ui/dist/ui.zip .
	cd package && zip -r $(project_name)-$(version).zip .
	docker stop minio-client
	docker rm minio-client
	docker run --name minio-client -v `pwd`/package:/package -itd --entrypoint=/bin/sh minio/mc
	docker exec minio-client mc config host add wecubeS3 $(s3_server_url) $(s3_access_key) $(s3_secret_key) wecubeS3
	docker exec minio-client mc cp /package/$(project_name)-$(version).zip wecubeS3/wecube-plugin-package-bucket
