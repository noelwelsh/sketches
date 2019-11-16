package sketches

object Particles {
  import cats.implicits._
  import doodle.core._
  import doodle.effect.Writer.Gif
  import doodle.random._
  import doodle.syntax._
  import doodle.svg._
  import doodle.interact.syntax._
  import monix.reactive.Observable
  import scala.concurrent.duration._
  import scala.scalajs.js.annotation._

  val nParticles = 1
  val maxAge = 40

  val frame = Frame("canvas").size(600,600).background(Color.midnightBlue)

  final case class Particle(location: Point,
                            velocity: Vec,
                            color: Color,
                            history: List[Point],
                            age: Int)
  object Particle {
    def initial: Random[Particle] =
      (emitter, colour).mapN{ (pt, c) =>
        Particle(pt, pt - Point(0, 200), c, List(pt), 0)}
  }

  val colour: Random[Color] =
    Random.double.map(d => Color.hsla((d * 0.2 - 0.1).turns, 0.9, 0.7, 1.0))

  val emitter: Random[Point] =
    (Random.normal(0, 5), Random.normal(200, 5))
      .mapN{ (x, y) => Point(x, y) }

  def emit(count: Int): Random[List[Particle]] =
    Particle.initial.replicateA(count)

  val gravity: Vec = Vec(0, -0.5)

  def step(particle: Particle): Particle = {
    val newVelocity = particle.velocity + gravity
    particle.copy(
      location = particle.location + newVelocity,
      velocity = newVelocity,
      history = particle.location :: (particle.history.take(4)),
      age = particle.age + 1
    )
  }

  def cull(particles: List[Particle]): List[Particle] =
    particles.filter(_.age < maxAge)

  def iterate(particles: List[Particle]): Random[List[Particle]] =
    emit(nParticles).map{ newParticles =>
      println(cull(particles.map(step _)).length)
      newParticles ++ cull(particles.map(step _))
    }

  def draw(particle: Particle): Picture[Unit] =
    (particle.location :: particle.history)
      .zipWithIndex
      .map{ case (pt, i) =>
        val color = particle.color.fadeOut((0.05 * i).normalized).desaturate((0.1 * i).normalized)
        circle[Algebra,Drawing](7 - i)
          .strokeColor(color)
          .fillColor(color)
          .strokeWidth(3.0)
          .at(pt)
      }
      .allOn

  val animation =
    Observable
      .repeat(1)
      .scan(emit(nParticles).run){ (state, _) =>
        iterate(state).run
      }
      .map(particles => (particles.map(draw _)).allOn)

  @JSExportTopLevel("Particles")
  def go() =
    animation.animateFrames(frame)
}
