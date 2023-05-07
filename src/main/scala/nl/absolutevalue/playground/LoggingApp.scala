package nl.absolutevalue.playground

import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object LoggingApp extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      _ <- ZIO.succeed {
        Thread.sleep(1000)
        println("Foo")
      }
      //      _ = println("Completed Foo")
      _ <- zio.Console.printLine("Hello, World!")
    } yield ()

}
