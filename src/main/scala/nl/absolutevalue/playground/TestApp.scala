package nl.absolutevalue.playground

import zio.{ZIO, ZIOAppDefault}


object TestApp extends ZIOAppDefault {


  // ZIO[R, E, A] ~> (R => Either[E, A])

  // Unexceptional effect that doesn't
  // require any specific environment, and cannot fail.
  // type UIO[+A] = ZIO[Any, Nothing, A]


  // Effect that requires an R,
  // and cannot fail, but can succeed with an A.
  // type URIO[-R, +A] = ZIO[R, Nothing, A]

  // Effect that has no requirements,
  // and may fail with a Throwable.
  // type Task[+A] = ZIO[Any, Throwable, A]


  // Effect that requires an R, and may fail
  // with a Throwable value, or succeed with an A
  // type RIO[-R, +A]  = ZIO[R, Throwable, A]


  // effect that has no requirements,
  // and may fail with an E, or succeed with an A
  // type IO[+E, +A] = ZIO[Any, E, A]


  // Errors
  // - Failures are expected errors. We use ZIO.fail to model failures.
  // - Defects are unexpected errors. We use ZIO.die to model a defect.
  // - Fatals are catastrophic unexpected errors => kill app.


  //TODO: read about https://zio.dev/reference/core/runtime

  def run = {

    // From Option: Option[None] is an errro
    val zValue1: zio.IO[Option[Nothing], Int] = ZIO.fromOption(Option(2))
    zValue1.catchSome { case None => ZIO.succeed(42) }


    // Lift: Option[A] is a value
    val zValue2: zio.IO[Nothing, Option[Int]] = ZIO.succeed(Option(2))
    val zResolved2: zio.IO[RuntimeException, Int] = zValue2.flatMap {
      case Some(v) => ZIO.succeed(v)
      case None => ZIO.fail(new RuntimeException("NONE"))
    }


    zio.Console.printLine("Hello, World!")
  }
}
