scalaVersion := "2.11.8"

organization := "s_mach"

scalacOptions ++= Seq(
  "-feature",
  "-unchecked",
  "-deprecation",
  //  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  //  "-Ywarn-unused-import",
//  "-Xfatal-warnings",
  "-Xlint",
  "-language:higherKinds"
)

