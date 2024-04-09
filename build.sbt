val scala3Version = "3.2.0"

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
    ("com.typesafe.play" %% "play-json" % "2.9.3").cross(CrossVersion.for3Use2_13)
  ),
  jacocoReportSettings := JacocoReportSettings(
    "Jacoco Coverage Report",
    None,
    JacocoThresholds(),
    Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML), // note XML formatter
    "utf-8"
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

lazy val controller = project
  .in(file("modules/controller"))
  .dependsOn(core, utility, fileIO)
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
  .aggregate(core, fileIO, userInterface, controller, utility)
  .dependsOn(core, fileIO, userInterface, controller, utility)
  .settings(
    commonSettings,
    name := "roulette",
  )