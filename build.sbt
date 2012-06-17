import AssemblyKeys._ // put this at the top of the file


//
// プロジェクト情報
//

name := "poormans-service-manager"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.9.1"


//
// 依存性
//

libraryDependencies ++= Seq(
	"com.twitter" % "util-core_2.9.1" % "3.0.0",
	"com.twitter" % "util-eval_2.9.1" % "3.0.0",
	"com.twitter" % "finagle-core_2.9.1" % "3.0.0",
	"com.twitter" % "finagle-http_2.9.1" % "3.0.0",
	"org.specs2" %% "specs2" % "1.11" % "test",
	"org.mockito" % "mockito-all" % "1.9.0" % "test"
)

resolvers ++= Seq(
	"Twitter Scala Library Repositories" at "http://maven.twttr.com/",
	"codahale" at "http://repo.codahale.com",
	"sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
	"sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases"
)


//
// その他設定
//

assemblySettings

excludedFiles in assembly := { (bases: Seq[File]) =>
	bases flatMap { base =>
		(base / "META-INF" * "*").get collect {
			case f if f.getName.toLowerCase == "notice.txt" => f
			case f if f.getName.toLowerCase == "license.txt" => f
			case f if f.getName.toLowerCase == "manifest.mf" => f
		}
	}
}
