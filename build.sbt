lazy val root = (project in file(".")).
  settings(
    name := "ing-gh-event-info",
    version := "0.1.0",
    scalaVersion := "2.10.4",
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-jackson" % "3.2.9",
      "org.apache.spark" %% "spark-core" % "1.4.1" % "provided",
      "org.elasticsearch" %% "elasticsearch-spark" % "2.1.0"
    )
  )

lazy val meta = """META.INF(.)*""".r

assemblyMergeStrategy in assembly := {
  case meta(_) | "UnusedStubClass.class"
  => MergeStrategy.discard
  case x
  => MergeStrategy.first
}
