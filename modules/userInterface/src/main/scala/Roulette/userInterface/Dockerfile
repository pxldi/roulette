FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1

WORKDIR /app

# Kopieren der Projektdateien
COPY . /app

# Ausführen von SBT-Befehlen
RUN sbt clean update compile

# Exponieren des Ports
EXPOSE 8081

# Starten des Frontends
CMD ["sbt", "runMain TuiClient"]
