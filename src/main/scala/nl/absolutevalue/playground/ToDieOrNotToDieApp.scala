package nl.absolutevalue.playground

import zio.{IO, Task, ZIO, ZIOAppDefault}

class ToDieOrNotToDieApp extends ZIOAppDefault {


  private object WithDie {
    private class Repository {
      def byId(id: String): Task[Int] = id match {
        case "ok" => ZIO.succeed(42)
        case "nok" => ZIO.fail(new RuntimeException("Repository 2 unexpected stuff"))
      }
    }

    object Business {

      private val repository = new Repository

      sealed trait MoneyError
      case object TooLittleMoney extends MoneyError

      def withdraw42(id: String): IO[MoneyError, Int] = repository.byId(id).orDie.flatMap {
        case m if m < 42 => ZIO.fail(TooLittleMoney)
        case m => ZIO.succeed(m - 42)
      }
    }
  }


  private object WithoutDie {
    private class Repository {
      def byId(id: String): Task[Int] = id match {
        case "ok" => ZIO.succeed(42)
        case "nok" => ZIO.fail(new RuntimeException("Repository 2 unexpected stuff"))
      }
    }

    object Business {

      private val repository = new Repository

      sealed trait MoneyError
      case object TooLittleMoney extends MoneyError
      case class OtherError(cause: Throwable) extends Exception(cause) with MoneyError

      def withdraw42(id: String): IO[MoneyError, Int] = repository.byId(id).mapError(OtherError).flatMap {
        case m if m < 42 => ZIO.fail(TooLittleMoney)
        case m => ZIO.succeed(m - 42)
      }
    }
  }

  def run = {

    zio.Console.printLine("Hello, World!") *>
    WithDie.Business.withdraw42("nok") *>
      zio.Console.printLine("Finish")
  }
}
