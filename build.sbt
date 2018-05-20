name := "panetime"

version := "0.1"

scalaVersion := "2.12.4"

javaOptions += "-Xmx2G"

lazy val `panetime` = (project in file("."))
  .aggregate(backend, frontend, modelJvm, modelJs)
  .settings({
    val stage = taskKey[Unit]("Stage task")

    stage := {
      IO.copyFile(
        (assembly in backend in Compile).value,
        baseDirectory.value / "target" / "webapp-runner.jar"
      )
    }
  })

lazy val backend =
  (project in file("backend"))
    .dependsOn(modelJvm)
    .settings(
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-http" % "10.0.11",
        "com.typesafe.slick" %% "slick" % "3.2.1",
        "org.slf4j" % "slf4j-nop" % "1.6.4",
        "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
        "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
      ),
      resourceGenerators in Compile += Def.task {
        Seq((fullOptJS in Compile in frontend).value.data)
      }.taskValue,
      watchSources ++= (watchSources in frontend).value,
      mainClass in assembly := Option("com.panetime.backend.Backend")
    )

lazy val frontend =
  (project in file("frontend"))
    .enablePlugins(ScalaJSPlugin)
    .disablePlugins(AssemblyPlugin)
    .settings(
      scalaJSUseMainModuleInitializer := true,
      libraryDependencies ++= Seq(
        "com.lihaoyi" %%% "scalatags" % "0.6.7",
        "org.scala-js" %%% "scalajs-dom" % "0.9.2"
      )
    )
    .dependsOn(modelJs)

lazy val model = (crossProject.crossType(CrossType.Pure) in file("model"))
  .settings(
    libraryDependencies ++= Seq("com.github.benhutchison" %%% "prickle" % "1.1.13")
  )

lazy val modelJs = model.js.disablePlugins(AssemblyPlugin)
lazy val modelJvm = model.jvm