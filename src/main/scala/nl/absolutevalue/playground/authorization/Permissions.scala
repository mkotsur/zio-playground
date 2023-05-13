package nl.absolutevalue.playground.authorization

sealed trait Permissions

object Permissions {

  sealed trait Db extends Permissions

  object Db {
    case object Write extends Db
    case object Read  extends Db
  }

  sealed trait FS
  object FS {
    case object Write extends FS
    case object Read  extends FS
  }

}
