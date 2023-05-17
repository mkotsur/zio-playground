package nl.absolutevalue.playground.authorization

import nl.absolutevalue.playground.authorization.Token.{InternalEmployee, UserSession}
import nl.absolutevalue.playground.authorization.service.{Greeter, InternalService, Repositories}
import zio.http.HttpError.{BadRequest, Unauthorized}
import zio.{&, ZIO, ZIOAppDefault, ZLayer}
object SecureApp extends ZIOAppDefault {

  // 1********************************************************************* //
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

  // **********2*********************************************************** //

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

  // ********************3************************************************* //

  // Token authentication – making sure the token is valid and the claims are to be trusted!
  case class ValidJwtToken(age: Int, country: String, sessionType: String)

  // We can do it based on data from request,
  // using existing JWT libraries, wrapped into a thin layer of ZIO.
  def authenticateToken( /*[r: Request]*/ ): ZIO[Any, BadRequest, ValidJwtToken] =
    ZIO.succeed(ValidJwtToken(22, "NL", "mobile"))

  override def run: ZIO[Any, Any, Any] = {

    // ******************************4*************************************** //

    val unitOfBusinessLogic = for {
      _        <- ZIO.service[Greeter.type].flatMap(_.hello())
      business <- ZIO.service[InternalService.type]
      _        <- business.updateData()
    } yield ()

    // **************************************************6******************* //

    /**
      * Let's write a rule that says how to validate the session.
      * This rule is reusable in other services too!
      */
    def simpleValidSessionCheck(
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
    // ************************************************************7********* //

    // The "less boilerplate way",
    // describe the "happy flow" as a partial function.
    val FancyValidSessionCheck = Sec[ValidJwtToken, UserSession.Valid.type]({
      case ValidJwtToken(_, _, st) if Seq("mobile", "web").contains(st) =>
        zio.ZLayer.succeed(UserSession.Valid)
    })

    // ****************************************5***************************** //

    // Resolve dependencies and remove "R" (it changes to Any) in ZIO[R,E,A]
    // and allows to run the program!
    authenticateToken( /*request*/ ).flatMap { token =>
      unitOfBusinessLogic
        .provideSome(
          ZLayer.succeed(Greeter)
            ++ ZLayer.succeed(InternalService)
            ++ simpleValidSessionCheck(token)
            ++ FancyValidSessionCheck.layer(token)
            ++ ZLayer.succeed(Repositories)
        )
    // In the "routes situation" we would concat more routes here...
    }

  }

}
