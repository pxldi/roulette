FROM adoptopenjdk/openjdk11:alpine-jre

# Installieren notwendiger Pakete
RUN apk add --no-cache bash wget

# Herunterladen und Installieren von sbt
RUN wget -O- "https://github.com/sbt/sbt/releases/download/v1.6.2/sbt-1.6.2.tgz" | tar xz -C /usr/local
RUN ln -s /usr/local/sbt/bin/* /usr/local/bin/

# Setzen des Arbeitsverzeichnisses
WORKDIR /app

# Kopieren der Projektdateien
COPY . /app

# Installieren der sbt-Abhängigkeiten und Kompilieren des Projekts
# Hier wird `-Dsbt.rootdir=true` verwendet, um sicherzustellen, dass sbt ausführen sicher ist
RUN sbt -Dsbt.rootdir=true clean update compile

# Exponieren des Ports, auf dem das Backend laufen wird
EXPOSE 8080

# Starten des Backends
CMD ["sbt", "-Dsbt.rootdir=true", "runMain Roulette.controller.ControllerApi"]
