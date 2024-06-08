FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1

WORKDIR /app

# Kopieren der Projektdateien
COPY . /app

# Ausführen von SBT-Befehlen
RUN sbt clean update compile

# Exponieren des Ports auf dem das Backend laufen wird
EXPOSE 8080

# Starten des Backends
CMD ["sbt", "runMain Roulette.controller.ControllerApi"]
