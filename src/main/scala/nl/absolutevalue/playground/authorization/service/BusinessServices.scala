package nl.absolutevalue.playground.authorization.service

import nl.absolutevalue.playground.authorization.Permissions.Db
import zio.{&, RIO, ZIO}

import scala.language.postfixOps

object BusinessServices {

  type NeededPermissions = Db.Write.type & Db.Read.type

  def doSomething(): RIO[NeededPermissions & Repositories.type, Unit] =
    ZIO.debug("Doing some business ...") *>
      (for {
        repo <- ZIO.service[Repositories.type]
        _    <- repo.select
        _    <- repo.insert
      } yield ())
}
