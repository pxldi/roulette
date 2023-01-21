FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1
RUN apt-get update && apt-get install -y libxrender1 libxtst6 libxi6 libgl1-mesa-glx libgtk-3-0 openjfx libgl1-mesa-dri libgl1-mesa-dev libcanberra-gtk-module libcanberra-gtk3-module
WORKDIR /roulette
ADD . /roulette
CMD ["sbt", "run"]

#docker build C:\Users\lsl\IdeaProjects\roulette -t roulette:v1
#docker build /mnt/c/Users/lsl/IdeaProjects/roulette -t roulette:v1
#docker run -e DISPLAY=172.19.144.1:0.0 -v /tmp/.X11-unix:/tmp/.X11-unix -ti roulette:v1