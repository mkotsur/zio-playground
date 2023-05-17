package nl.absolutevalue.playground.authorization.service

import nl.absolutevalue.playground.authorization.Token.{Db, InternalEmployee, UserSession}
import nl.absolutevalue.playground.authorization.Token
import Token.syntax._
import zio.{&, RIO, ZIO}

import scala.language.postfixOps

object InternalService {

  def resetPassword(): RIO[ /*TwoFactorSession.type & */ Repositories.type, Unit] = ???

  def updateData(): RIO[ /*ValidSession.type & */ Repositories.type, Unit] =
    ZIO.debug("** Doing some business: ... **") *>
      (for {
        repo <- ZIO.service[Repositories.type]
        _    <- repo.select
        _    <- repo.insert
      } yield ())
}
