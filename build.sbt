val scala3Version = "3.2.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "roulette1",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= {
      Seq(
        "org.scalactic" %% "scalactic" % "3.2.14",
        "org.scalatest" %% "scalatest" % "3.2.14" % "test",
        "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
        "org.scalameta" %% "munit" % "0.7.29" % Test,
        "com.lihaoyi" %% "os-lib" % "0.9.0",
        "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
        ("com.typesafe.play" %% "play-json" % "2.9.3").cross(CrossVersion.for3Use2_13)
      )
    }
  )

  jacocoReportSettings := JacocoReportSettings(
  "Jacoco Coverage Report",
  None,
  JacocoThresholds(),
  Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML), // note XML formatter
  "utf-8")