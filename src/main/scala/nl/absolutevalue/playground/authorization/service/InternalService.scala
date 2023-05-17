package nl.absolutevalue.playground.authorization.service

import zio.{RIO, ZIO}

import scala.language.postfixOps

// These functions need [[Repositories]] as dependency,
// and we DO have to "protect" them.
object InternalService {

  /**
    * Only for users in the middle of 2FA
    */
  def resendCode(): RIO[ /*TwoFactorSession.type & */ Repositories.type, Unit] = ???

  /**
   * Only for logged-
   */
  def updateData(): RIO[ /*ValidSession.type & */ Repositories.type, Unit] =
    ZIO.debug("** Doing some business: ... **") *>
      (for {
        repo <- ZIO.service[Repositories.type]
        _    <- repo.select
        _    <- repo.insert
      } yield ())
}
