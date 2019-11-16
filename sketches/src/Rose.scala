package sketches

object Particles {
  import doodle.core._
  import doodle.image._
  import doodle.image.syntax._
  import doodle.image.syntax.core._
  import doodle.svg._
  import doodle.reactor._
  import cats.implicits._
  import scala.scalajs.js.annotation._

  val frame = Frame("canvas").size(600,600).background(Color.midnightBlue)

  val fireFly = Image.circle(20)
    .noStroke
    .fillGradient(Gradient.dichromaticRadial(Color.yellow, Color.crimson))

  def rose(angle: Angle): Point =
    Point((angle * 7).cos * angle.cos, (angle * 7).cos * angle.sin)

  val animation =
    Reactor.init(0.degrees)
      .onTick(angle => angle + 1.degrees)
      .render(angle => fireFly.at(rose(angle)))

  @JSExportTopLevel("Rose")
  def go() =
    animation.run(frame)
}
