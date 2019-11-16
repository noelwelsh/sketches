// build.sc
import mill._
import mill.scalalib._
import mill.scalalib.scalafmt._
import mill.scalajslib._


object sketches extends ScalaJSModule with ScalafmtModule {
  def scalaVersion = "2.12.8"
  def scalaJSVersion = "0.6.28"
  def platformSegment = "js"

  def ivyDeps = Agg(
    ivy"org.creativescala::doodle::0.9.5"
  )
}

object console extends ScalaModule with ScalafmtModule {
  def scalaVersion = "2.12.8"

  def ivyDeps = Agg(
    ivy"org.creativescala::doodle::0.9.5"
  )
}
