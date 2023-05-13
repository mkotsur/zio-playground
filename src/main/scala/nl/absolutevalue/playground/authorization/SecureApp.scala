package nl.absolutevalue.playground.authorization

import nl.absolutevalue.playground.authorization.Permissions.Db
import nl.absolutevalue.playground.authorization.service.{BusinessServices, Repositories}
import zio.http.HttpError.{BadRequest, Unauthorized}
import zio.{&, ZIO, ZIOAppDefault, ZLayer}

object SecureApp extends ZIOAppDefault {

  /**
    * Authorization as a code:
    *
    * An approach that allows to secure routes and business logic in
    * developer-friendly and type safe manner.
    *
    * IT DOES:
    *  - separate authentication and authoriseDbReadWrite
    *  - separate permission validation from business logic
    *  - allow modeling permissions as ADTs
    *  - allow granular permissions configuration
    *  - make impossible to add a route without proper authoriseDbReadWrite check
    *  - work with ZIO-http
    *  - work with Tapir
    *
    * IT DOES NOT:
    *  - require boilerplate code
    *  - trigger scalafix or compiler warnings
    *  - require deep knowledge of ZIO or FP
    *  - require a lot of rewrite to work with Scala 3
    */

  override def run: ZIO[Any, Any, Any] = {

    val businessLogic1 = for {
      _        <- zio.Console.printLine("Hello, World!")
      repo     <- ZIO.service[Repositories.type]
      _        <- repo.insert
      business <- ZIO.service[BusinessServices.type]
      _        <- business.doSomething()
    } yield ()

    authenticateToken().flatMap { token =>
      businessLogic1
        .provideSome(
          authoriseDbReadWrite(token) ++
            ZLayer.succeed(Repositories) ++
            ZLayer.succeed(BusinessServices)
        )
    // In the "routes situation" we would concat more routes here...
    }

  }

  case class ValidJwtToken(age: Int)

  def authenticateToken(): ZIO[Any, BadRequest, ValidJwtToken] =
    ZIO.succeed(ValidJwtToken(22))

  /**
    * Authorizer.
    * Only here we can have an may need to deal with Unauthorized exception.
    */
  private def authoriseDbReadWrite(
      token: ValidJwtToken
  ): zio.Layer[Unauthorized, Db.Read.type & Db.Write.type] =
    token match {
      case ValidJwtToken(age) if age > 18 =>
        (ZLayer.succeed(Db.Write) ++ ZLayer.succeed(Db.Read)).tapError(ZIO.logError)
      case _ => ZLayer.fail(Unauthorized("Go away"))
    }

}
