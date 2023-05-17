package nl.absolutevalue.playground.authorization

import nl.absolutevalue.playground.authorization.Token.{InternalEmployee, UserSession}
import nl.absolutevalue.playground.authorization.service.{Greeter, InternalService}
import zio.http.HttpError.{BadRequest, Unauthorized}
import zio.{ZIO, ZIOAppDefault, ZLayer}
object SecureApp extends ZIOAppDefault {

  /**
    * “Inside every large program is a small program struggling to get out.”
    * — Tony Hoare
    */

  /** Typical problems:
     - Difficulty of making changes
        - Authentication mixed with business logic
        - Repeated code
        - Hard to change permission permission types.
        - Hard to refactor code

    */

  /**
    * Agenda:
    * - Layers and Dependencies in ZIO
    * - Adding dependencies to a program
    * - "Security Layer", or SLayer
    * - SLayer As A Function
    * - SLayer As A Partial Function
    * - Requiring multiple SLayers
    * - Applying this to Routes
    */

  /**
    *
    * An approach that allows to separate authentication from business logic in
    * developer-friendly and type safe manner.
    *
    * IT DOES:
    *  ✅ separate authentication and authorisation
    *  ✅ separate permission validation from business logic
    *  ✅ allow modeling permissions as ADTs
    *  ✅ allow granular permissions configuration
    *  ✅ make impossible to add a route without proper authoriseDbReadWrite check
    *  - work with ZIO-http
    *  - work with Tapir
    *
    * IT DOES NOT:
    *  - require boilerplate code
    *  - trigger scalafix or compiler warnings
    *  - require deep knowledge of ZIO or FP
    *  - require a lot of rewrite to work with Scala 3
    */

  // Token authentication
  case class ValidJwtToken(age: Int, country: String, sessionType: String)

  def authenticateToken( /*[r: Request]*/ ): ZIO[Any, BadRequest, ValidJwtToken] =
    ZIO.succeed(ValidJwtToken(22, "NL", "Mobile"))

  override def run: ZIO[Any, Any, Any] = {

    val businessLogic1 = for {
      _ <- ZIO.service[Greeter.type].flatMap(_.hello())
//      repo     <- ZIO.service[Repositories.type]
//      _        <- repo.insert
      business <- ZIO.service[InternalService.type]
//      _        <- business.doSomething()
    } yield ()

    // 1*********2*********3*********4*********5*********6*********7********* //

    /**
      * Let's write a rule that says how to validate the session.
      * This rule is reusable in other services too!
      */
    def validSessionCheck(
        token: ValidJwtToken
    ): zio.ZLayer[
      Any,          // It can also have access to other services via other layers!
      Unauthorized, // Layers can "fail" too in ZIO!
      UserSession.Valid.type
    ] =
      token match {
        case ValidJwtToken(_, _, st) if Seq("mobile", "web").contains(st) =>
          ZLayer.succeed(UserSession.Valid)
        case _ => ZLayer.fail(Unauthorized("Go away"))
      }
    // 1*********2*********3*********4*********5*********6*********7********* //

    // The "less boilerplate way",
    // describe the "happy flow" as a partial function.
    val FancyValidSessionCheck = Sec[ValidJwtToken, UserSession.Valid.type]({
      case ValidJwtToken(_, _, st) if Seq("mobile", "web").contains(st) =>
        zio.ZLayer.succeed(UserSession.Valid)
    })

    // 1*********2*********3*********4*********5*********6*********7********* //

    // Or call from a service

    // Resolve dependencies and remove "R" (it changes to Any) in ZIO[R,E,A]
    // and allows to run the program!
    authenticateToken( /*request*/ ).flatMap { token =>
      businessLogic1
        .provideSome(
          ZLayer.succeed(Greeter)
//            ++ ZLayer.succeed(InternalService)
//            ++ validSessionCheck(token)
//            ++ FancyValidSessionCheck.layer(token)
//            ++ ZLayer.succeed(Repositories)
        )
    // In the "routes situation" we would concat more routes here...
    }

  }

}
