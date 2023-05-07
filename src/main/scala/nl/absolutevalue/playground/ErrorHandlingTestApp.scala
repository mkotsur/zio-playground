package nl.absolutevalue.playground

import zio.{IO, Task, ZIO, ZIOAppDefault}

class ErrorHandlingTestApp extends ZIOAppDefault {

  private object RepositoryOne {
    sealed trait Error extends Throwable
    case class RepositoryOneNotFound(id: String) extends Exception(s"R1 NF $id") with Error
  }

  private class RepositoryOne {
    def byId(id: String): IO[RepositoryOne.Error, String] = id match {
      case "ok" => ZIO.succeed("ok")
//      We can not do this 👇🏻: it won't compile
//      case "nok" => ZIO.fail(new RuntimeException("Repository 2 unexpected stuff"))
      case _ => ZIO.fail(RepositoryOne.RepositoryOneNotFound(id))
    }
  }

  private object RepositoryTwo {
    sealed trait Error extends Throwable
    case class NotFound(id: String) extends Exception(s"R2 NF $id") with Error
  }

  private class RepositoryTwo {
    def byId(id: String): Task[Int] = id match {
      case "ok" => ZIO.succeed(42)
      case "nok" => ZIO.fail(new RuntimeException("Repository 2 unexpected stuff"))
      case _ => ZIO.fail(RepositoryTwo.NotFound(id))
    }
  }

  private object RepositoryThree {
    sealed trait Error extends Throwable

    case class NotFound(id: String) extends Exception(s"R3 NF $id") with Error
  }

  private class RepositoryThree {
    def byId(id: String): IO[RepositoryThree.Error, String] = id match {
      case "ok" => ZIO.succeed("ok")
      case _ => ZIO.fail(RepositoryThree.NotFound(id))
    }
  }


  def run = {


    val r1 = new RepositoryOne
    val r2 = new RepositoryTwo
    val r3 = new RepositoryThree

    val eff: zio.Task[(String, Int, String)] = for {
      v1 <- r1.byId("ok")
      v2 <- r2.byId("ok")
      v3 <- r3.byId("ok")
    } yield (v1, v2, v3)

    zio.Console.printLine("Hello, World!")
  }
}
