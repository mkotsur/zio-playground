ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "zio-playground-2_13"
  )

libraryDependencies += "dev.zio" %% "zio"      % "2.0.13"
libraryDependencies += "dev.zio" %% "zio-http" % "3.0.0-RC1"
