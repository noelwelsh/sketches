package sketches

object Pulsing {
  import cats.instances.all._
  import doodle.core._
  import doodle.effect.Writer.Gif
  import doodle.syntax._
  import doodle.svg._
  import doodle.interact.syntax._
  import monix.reactive.Observable
  import scala.concurrent.duration._
  import scala.scalajs.js.annotation._

  val frame = Frame("canvas").size(600,600).background(Color.midnightBlue)

  val strokeWidth = 9.0
  val sampleRate = 200.millis
  val gapWidth = 6.0
  val minimumDiameter = gapWidth + strokeWidth
  val maxNumberOfCircles = 15

  def disk(count: Int): Picture[Unit] =
    circle[Algebra,Drawing]((count.toDouble * 2.0 * (strokeWidth + gapWidth) + minimumDiameter))
      .noFill
      .strokeWidth(strokeWidth)

  def background(count: Int): Picture[Unit] = {
    def iter(count: Int): Picture[Unit] =
      count match {
        case 0 =>
          circle[Algebra,Drawing](count)
        case n =>
          circle[Algebra,Drawing](count).on(iter(n - 1))
      }

    iter(count).strokeWidth(strokeWidth.toDouble).strokeColor(Color.darkGray)
  }

  def pulse(count: Int): Picture[Unit] =
    count match {
      case 0 => disk(0).strokeColor(Color.crimson)
      case 1 =>
        disk(1)
          .strokeColor(Color.crimson)
          .on(circle(0).strokeColor(Color.crimson.spin(30.degrees)))
      case n =>
        disk(n)
          .strokeColor(Color.crimson)
          .on(circle(n - 1)
            .strokeColor(Color.crimson.spin(30.degrees)))
          .on(circle(n - 2)
            .strokeColor(Color.crimson.spin(60.degrees)))
    }

  val animation: Observable[Picture[Unit]] =
    Observable
      .repeat(1)
      .sample(sampleRate)
      .scan((1, 0)) { (state, _) =>
        val (inc, count) = state
        if (count >= maxNumberOfCircles) (-1, maxNumberOfCircles - 1)
        else if (count <= 0) (1, 1)
        else (inc, count + inc)
      }
      .map { case (_, c) => pulse(c).on(background(maxNumberOfCircles)) }

  @JSExportTopLevel("Pulse")
  def go() =
    animation.animate(frame)
}
