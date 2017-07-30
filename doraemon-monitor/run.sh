
#!/bin/bash
git pull
mvn clean package -Dmaven.test.skip=true


docker rm -f doraemon-monitor-pro
docker rmi -f doraemon-monitor:pro
docker build -t doraemon-monitor:pro .
docker run -d -p 50001:50001 --name doraemon-monitor-pro doraemon-monitor:pro java -Duser.timezone=GMT+8  -jar /app/app.jar
