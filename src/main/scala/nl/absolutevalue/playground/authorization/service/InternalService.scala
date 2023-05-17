package nl.absolutevalue.playground.authorization.service

import nl.absolutevalue.playground.authorization.Token.UserSession.{Admin, Valid}
import zio.{&, RIO, ZIO}

import scala.language.postfixOps

// These functions need [[Repositories]] as dependency,
// and we DO have to "protect" them.
object InternalService {

  /**
    * Only for users in the middle of 2FA
    */
  def resendCode(): RIO[ /*TwoFactor.type & */ Repositories.type, Unit] = ???

  /**
    * Only for logged-
    */
  def updateData(): RIO[Valid.type & Repositories.type, Unit] =
    ZIO.debug("** Doing some business: ... **") *>
      (for {
        repo <- ZIO.service[Repositories.type]
        _    <- repo.select
        _    <- repo.insert
      } yield ())
}
