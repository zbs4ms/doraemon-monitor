
#!/bin/bash
git pull
mvn clean package -Dmaven.test.skip=true


docker rm -f doraemon-monitor-client-pro
docker rmi -f doraemon-monitor-client:pro
docker build -t doraemon-monitor-client:pro .
docker run -d -p 41801:41801 --add-host monitor-service:39.108.117.235  --name doraemon-monitor-client-pro doraemon-monitor-client:pro java -Duser.timezone=GMT+8  -jar /app/app.jar 39.108.117.235
