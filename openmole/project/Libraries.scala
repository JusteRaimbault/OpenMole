import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt.Keys._
import sbt._

object Libraries {

  lazy val gridscaleVersion = "2.28"
  lazy val sshjVersion = "0.27.0"
  lazy val containerVersion = "1.11"
  lazy val mgoVersion = "3.43"
  lazy val bouncyCastleVersion = "1.64"
  lazy val d3Version = "3.5.12"
  lazy val tooltipserVersion = "3.3.0"
  lazy val netLogo5Version = "5.3.1"
  lazy val netLogo6Version = "6.1.1"
  lazy val scalaTagsVersion = "0.8.3"
  lazy val scalaJSDomVersion = "0.9.3"
  lazy val rxVersion = "0.4.0"
  lazy val boopickleVersion = "1.3.1"
  lazy val scalaAutowireVersion = "0.2.6"
  lazy val sourcecodeVersion = "0.2.1"
  lazy val scaladgetVersion = "1.2.7"
  lazy val sortableVersion = "0.2.1"
  lazy val json4sVersion = "3.6.7"
  lazy val circeVersion = "0.13.0"
  lazy val catsVersion = "2.1.0"
  lazy val scalajsVersion = "0.6.31"
  lazy val squantsVersion = "1.6.0"
  lazy val xstreamVersion = "1.4.12"
  lazy val scalatraVersion = "2.7.0-RC1"
  lazy val scalaURIVersion = "1.1.1"
  lazy val scoptVersion = "3.7.1"
  lazy val spatialsamplingVersion = "0.1"

  lazy val bootstrapnative = libraryDependencies += "fr.iscpif.scaladget" %%% "bootstrapnative" % scaladgetVersion
  lazy val lunr = libraryDependencies += "fr.iscpif.scaladget" %%% "lunr" % scaladgetVersion

  lazy val sortable = libraryDependencies += "net.scalapro" %%% "sortable-js-facade" % sortableVersion
  lazy val ace = libraryDependencies += "fr.iscpif.scaladget" %%% "ace" % scaladgetVersion
  lazy val scaladgetTools = libraryDependencies += "fr.iscpif.scaladget" %%% "tools" % scaladgetVersion
  lazy val scalajsDomJS = libraryDependencies += "org.scala-js" %%% "scalajs-dom" % scalaJSDomVersion
  lazy val rxJS = libraryDependencies += "com.lihaoyi" %%% "scalarx" % rxVersion
  lazy val scalaTagsJS = libraryDependencies += "com.lihaoyi" %%% "scalatags" % scalaTagsVersion
  lazy val autowireJS = libraryDependencies += "com.lihaoyi" %%% "autowire" % scalaAutowireVersion
  lazy val boopickleJS = libraryDependencies += "io.suzaku" %%% "boopickle" % boopickleVersion
  lazy val sourcecodeJS = libraryDependencies += "com.lihaoyi" %%% "sourcecode" % sourcecodeVersion
  lazy val scalajsMarked = libraryDependencies += "com.github.karasiq" %%% "scalajs-marked" % "1.0.2"
  lazy val htmlparser2 = libraryDependencies += "com.definitelyscala" %%% "scala-js-htmlparser2" % "1.0.2"
  lazy val plotlyJS =  libraryDependencies += "com.definitelyscala" %%% "scala-js-plotlyjs" % "1.1.10"

  lazy val scalatest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"


  /** ------- Bundles -------------- */

  def addScalaLang(scalaVersion: String) =
    libraryDependencies ++= Seq(
      "org.openmole.library" %% "org-scala-lang-scala-library" % scalaVersion,
      "org.scalameta" %% "scalameta" % "4.1.0"
    )

  lazy val scalatra = "org.openmole.library" %% "org-scalatra" % scalatraVersion
  lazy val logback = "org.openmole.library" %% "ch-qos-logback" % "1.2.3"
  lazy val h2 = "org.openmole.library" %% "org-h2" % "1.4.200"
  lazy val bonecp = "org.openmole.library" %% "com-jolbox-bonecp" % "0.8.0.RELEASE"
  lazy val slick = "org.openmole.library" %% "com-typesafe-slick" % "3.3.2"
  lazy val slf4j = "org.openmole.library" %% "org-slf4j" % "1.7.30"
  lazy val xstream = "org.openmole.library" %% "com-thoughtworks-xstream" % xstreamVersion
  lazy val jasypt = "org.openmole.library" %% "org-jasypt-encryption" % "1.9.3"
  lazy val opencsv = "org.openmole.library" %% "au-com-bytecode-opencsv" % "2.3"
  lazy val arm = "org.openmole.library" %% "com-jsuereth-scala-arm" % "2.0"
  lazy val scalajHttp = "org.openmole.library" %% "org-scalaj-scalaj-http" % "2.4.2"
  lazy val scopt = "org.openmole.library" %% "com-github-scopt" % scoptVersion
  lazy val scalabc = "org.openmole.library" %% "fr-iscpif-scalabc" % "0.4"
  lazy val scalatexSite = "org.openmole.library" %% "com-lihaoyi-scalatex-site" % "0.4.5"
  lazy val math = "org.openmole.library" %% "org-apache-commons-math" % "3.6.1"
  lazy val collections = "org.openmole.library" %% "org-apache-commons-collections" % "4.4"
  lazy val exec = "org.openmole.library" %% "org-apache-commons-exec" % "1.3"
  lazy val log4j = "org.openmole.library" %% "org-apache-log4j" % "1.2.17"
  lazy val logging = "org.openmole.library" %% "org-apache-commons-logging" % "1.2"
  lazy val lang3 = "org.openmole.library" %% "org-apache-commons-lang3" % "3.9"
  lazy val ant = "org.openmole.library" %% "org-apache-ant" % "1.10.7"
  lazy val codec = "org.openmole.library" %% "org-apache-commons-codec" % "1.14"
  lazy val async = "org.openmole.library" %% "scala-async" % "0.10.0"
  lazy val jgit = "org.openmole.library" %% "org-eclipse-jgit" % "5.6.0"
  lazy val cats = "org.openmole.library" %% "cats" % catsVersion
  lazy val squants = "org.openmole.library" %% "squants" % squantsVersion
  lazy val clapper = "org.openmole.library" %% "org-clapper" % "1.5.1"
  lazy val asm = "org.openmole.library" %% "org-objectweb-asm" % "7.2"
  lazy val configuration = "org.openmole.library" %% "org-apache-commons-configuration2" % "2.6"
  lazy val json4s = "org.openmole.library" %% "org-json4s" % json4sVersion
  lazy val circe = "org.openmole.library" %% "io-circe" % circeVersion
  lazy val scalajsTools = "org.openmole.library" %% "scalajs-tools" % scalajsVersion
  lazy val scalajs = "org.openmole.library" %% "scalajs" % scalajsVersion
  lazy val mgo = "org.openmole.library" %% "mgo" % mgoVersion
  lazy val monocle = Seq("org.openmole.library" %% "monocle" % "2.0.0", scalaz)
  lazy val container = "org.openmole.library" %% "container" % containerVersion
  lazy val boopickle = "org.openmole.library" %% "boopickle" % boopickleVersion
  lazy val autowire = "org.openmole.library" %% "autowire" % scalaAutowireVersion
  lazy val scalaTags = "org.openmole.library" %% "com-scalatags" % scalaTagsVersion
  lazy val rx = "org.openmole.library" %% "rx" % rxVersion
  lazy val netlogo5 = "org.openmole.library" % "ccl-northwestern-edu-netlogo5" % netLogo5Version
  lazy val netlogo6 = "org.openmole.library" % "ccl-northwestern-edu-netlogo6" % netLogo6Version
  lazy val sourceCode = "org.openmole.library" %% "sourcecode" % sourcecodeVersion
  lazy val txtmark = "org.openmole.library" %% "com-github-rjeschke-txtmark" % "0.13"
  lazy val spatialsampling = "org.openmole.library" %% "org-openmole-spatialsampling" % spatialsamplingVersion
  lazy val xzJava = "org.openmole.library" %% "xzjava" % "1.8"
  
  def httpClientVersion = "4.5.3"
  lazy val httpClient =
    Seq(
      "org.apache.httpcomponents" % "httpclient-osgi" % httpClientVersion,
      "org.apache.httpcomponents" % "httpmime" % httpClientVersion,
      "org.apache.httpcomponents" % "httpcore-osgi" % "4.4.7"
    )

  lazy val toolxitBibtex = "org.openmole" %% "toolxit-bibtex" % "0.2"

  lazy val gridscale = "org.openmole.library" %% "gridscale" % gridscaleVersion
  lazy val gridscaleSSH = 
    Seq(
      "org.openmole.library" %% "gridscale-ssh" % gridscaleVersion,
      "org.openmole.library" %% "com-hierynomus-sshj" % sshjVersion
    )
  
  lazy val gridscalePBS = "org.openmole.library" %% "gridscale-pbs" % gridscaleVersion
  lazy val gridscaleSGE = "org.openmole.library" %% "gridscale-sge" % gridscaleVersion
  lazy val gridscaleCondor = "org.openmole.library" %% "gridscale-condor" % gridscaleVersion
  lazy val gridscaleSLURM = "org.openmole.library" %% "gridscale-slurm" % gridscaleVersion

  lazy val gridscaleEGI = Seq(
    "org.openmole.library" %% "gridscale-egi" % gridscaleVersion,
    "org.openmole.library" %% "gridscale-webdav" % gridscaleVersion,
    "org.openmole.library" %% "gridscale-dirac" % gridscaleVersion)

  lazy val gridscaleHTTP = httpClient ++ Seq("org.openmole.library" %% "gridscale-http" % gridscaleVersion)
  lazy val gridscaleLocal = "org.openmole.library" %% "gridscale-local" % gridscaleVersion
  lazy val gridscaleOAR = "org.openmole.library" %% "gridscale-oar" % gridscaleVersion

  lazy val scalaz = "org.scalaz" %% "scalaz-core" % "7.2.30"
  lazy val guava = "com.google.guava" % "guava" % "27.1-jre"
  lazy val spray = "io.spray" %% "spray-json" % "1.3.2"
  lazy val bouncyCastle = "org.bouncycastle" % "bcpkix-jdk15on" % bouncyCastleVersion
  lazy val equinoxOSGi = "org.eclipse.platform" % "org.eclipse.osgi" % "3.13.200"
  lazy val osgiCompendium = "org.osgi" % "org.osgi.compendium" % "4.3.1"

  lazy val shapeless = "org.openmole.library" %% "com-chuusai-shapeless" % "2.3.3"

}
