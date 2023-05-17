package nl.absolutevalue.playground.authorization.service

import nl.absolutevalue.playground.authorization.Token.Db
import zio.{&, RIO, ZIO}

object Repositories {

  def insert: RIO[ /*Db.Write.type & Db.Write.type*/ Any, Unit] =
    ZIO.debug("INSERT INTO ...")

  def select: RIO[ /*Db.Write.type & Db.Write.type*/ Any, String] =
    ZIO.debug("SELECT FROM ...") *> ZIO.succeed("Hello")
}
