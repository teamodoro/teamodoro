name := "teamodoro"
organization := "teamodoro"

version := "0.3-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

packageName in Universal := "teamodoro-dist"

scalaVersion := "2.13.1"

libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.0.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
libraryDependencies += "com.h2database" % "h2" % "1.4.200"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.specs2" %% "specs2-core" % "4.9.2" % Test