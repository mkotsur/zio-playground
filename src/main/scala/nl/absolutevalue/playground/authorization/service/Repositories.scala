package nl.absolutevalue.playground.authorization.service

import nl.absolutevalue.playground.authorization.Permissions.Db
import zio.{RIO, ZIO}

object Repositories {

  def insert: RIO[Db.Write.type, Unit] =
    ZIO.debug("INSERT INTO ...")

  def select: RIO[Db.Read.type, String] =
    ZIO.debug("SELECT FROM ...") *> ZIO.succeed("Hello")
}
