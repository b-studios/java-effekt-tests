lazy val root = project
  .in(file("."))
  .settings(moduleName := "effekt-tests")
  .settings(name := "java-effekt-tests")
  .enablePlugins(JavaAgent, BuildInfoPlugin)
  .settings(commonSettings:_*)
  .settings(effektSettings:_*)
  .settings(testingSettings:_*)

lazy val commonSettings = Seq(
  scalaVersion := "2.12.4"
)

lazy val effektSettings = Seq(

  // runtime dependencies of instrumented code
  libraryDependencies ++= Seq(
    "de.b-studios" %% "effekt-core" % "0.1.1-SNAPSHOT",
    "de.b-studios" %% "effekt-instrumentation" % "0.1.1-SNAPSHOT"
  ),

  // java agent that performs the instrumentation
  javaAgents += JavaAgent("de.b-studios" % "effekt-instrumentation_2.12" % "0.1.1-SNAPSHOT" % "compile;test;run", arguments = "java_agent_argument_string"),

  // sadly currently necessary since some stack maps generated by
  // OPAL appear to be faulty
  javaOptions += "-noverify"
)

lazy val testingSettings = Seq(
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.4" % "test",
    "org.scala-sbt" %% "io" % "1.1.3" % "test"
  ),

  parallelExecution in Test := false,

  // we use the BuildInfo plugin to access sbt path-information at runtime
  // to search for tests and execute them
  buildInfoKeys := Seq[BuildInfoKey](
    BuildInfoKey.map(dependencyClasspath in Compile) {
      case (name, cp) => (name, cp.map { attr => attr.data })
    },
    classDirectory in Compile,
    classDirectory in Test,
    target,
    javaSource in Test
  ),
  buildInfoPackage := "effekt"
)
