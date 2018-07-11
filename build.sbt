name := "fileprocessorForDummies"

version := "0.1"

scalaVersion := "2.11.11"

scalacOptions += "-Ypartial-unification"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.13"
libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1"
