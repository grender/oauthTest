import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  lazy val JavaNet = "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
  override def libraryDependencies = Set(
    "com.ning" % "async-http-client"% "1.6.2"
  ) ++ super.libraryDependencies
}
