name := "workdayAssignment"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(

	"com.typesafe.akka" %% "akka-http-experimental" % "1.0",

	"org.twitter4j" % "twitter4j-core" % "3.0.5",

	"com.hunorkovacs" %% "koauth" % "1.1.0",

	"org.json4s" %% "json4s-native" % "3.3.0",

	"org.scalaj" %% "scalaj-http" % "2.2.1",

	"io.spray" %%  "spray-json" % "1.3.2"
)