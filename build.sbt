val zioVersion = "1.0.1"

name := "chess-game"

organization := "org.sigurdthor"
version := "1.0"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "org.scalatest" %% "scalatest" % "3.2.2" % Test,
  "dev.zio" %% "zio-test"     % zioVersion % "test",
  "dev.zio" %% "zio-test-sbt" % zioVersion % "test"
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
