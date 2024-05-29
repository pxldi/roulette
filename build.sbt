val scala3Version = "3.3.1"

// Common Set of settings for all modules
lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0",
  scalaVersion := scala3Version,
  libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic" % "3.2.14",
    "org.scalatest" %% "scalatest" % "3.2.14" % "test",
    "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
    "org.scalameta" %% "munit" % "0.7.29" % Test,
    "com.lihaoyi" %% "os-lib" % "0.9.0",
    "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
    "com.typesafe.slick" %% "slick" % "3.5.0",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.5.1",
    "com.zaxxer" % "HikariCP" % "5.1.0",
    "org.slf4j" % "slf4j-nop" % "2.0.13",
    "com.h2database" % "h2" % "2.2.224" % Test,
    "org.postgresql" % "postgresql" % "42.2.23",
    "com.typesafe.slick" %% "slick-codegen" % "3.5.0", // Updated version
    "com.typesafe" % "config" % "1.4.1",
    "com.typesafe.play" %% "play-json" % "2.9.3" cross CrossVersion.for3Use2_13,
    "org.reactivemongo" %% "reactivemongo" % "1.1.0-RC10",
    "io.github.leviysoft" %% "oolong-mongo" % "0.4.0"
  ),
  jacocoReportSettings := JacocoReportSettings(
    "Jacoco Coverage Report",
    None,
    JacocoThresholds(),
    Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML), // note XML formatter
    "utf-8"
  ),
  javaOptions ++= Seq(
    "-Xms512M",
    "-Xmx2G"
  )
)

lazy val core = project
  .in(file("modules/core"))
  .settings(
    commonSettings,
    name := "core",
  )

lazy val fileIO = project
  .in(file("modules/fileIO"))
  .dependsOn(core)
  .settings(
    commonSettings,
    name := "fileIO",
  )

lazy val utility = project
  .in(file("modules/utility"))
  .settings(
    commonSettings,
    name := "utility",
  )

lazy val db = project
  .in(file("modules/db"))
  .dependsOn(core)
  .settings(
    commonSettings,
    name := "db",
  )

lazy val controller = project
  .in(file("modules/controller"))
  .dependsOn(core, utility, fileIO, db)
  .settings(
    commonSettings,
    name := "controller",
  )

lazy val userInterface = project
  .in(file("modules/userInterface"))
  .dependsOn(core, controller, utility)
  .settings(
    commonSettings,
    name := "userInterface",
  )

lazy val root = project
  .in(file("."))
  .aggregate(core, fileIO, userInterface, controller, utility, db)
  .dependsOn(core, fileIO, userInterface, controller, utility, db)
  .settings(
    commonSettings,
    name := "roulette",
    fork / run := true
  )

//sbt 'set fork in run := true' run