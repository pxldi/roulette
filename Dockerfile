#Dockerfile von SoftwareArchitektur
FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1
RUN apt-get update && apt-get install -y libxrender1 libxtst6 libxi6 libgl1-mesa-glx libgtk-3-0 openjfx libgl1-mesa-dri libgl1-mesa-dev libcanberra-gtk-module libcanberra-gtk3-module
WORKDIR /roulette2
ADD . /roulette2
CMD ["sbt", "run"]

#ip 192.168.56.1
#docker build C:\Users\niels\roulette2 -t roulette2:v1
#XLaunch
#Docker login
#docker run -e DISPLAY=192.168.56.1:0.0 -v /tmp/.X11-unix:/tmp/.X11-unix -ti roulette2:v1
#docker run -e DISPLAY=192.168.56.1:0.0 -v /tmp/.X11-unix:/tmp/.X11-unix -ti v2:latest



