package nl.absolutevalue.playground.authorization

import zio.{&, Tag, ULayer}

sealed trait Token

object Token {

//  object syntax {
//    implicit class CombinableToken[P1](a: ULayer[P1]) {
//      def ~![P2: Tag](b: ULayer[P2]): ULayer[P1 & P2] = a.zipWithPar(b)(_.union[P2](_))
//    }
//  }

  case object InternalEmployee extends Token
  case object VpnAccess        extends Token

  sealed trait UserSession extends Token

  object UserSession {
    case object TwoFactor extends UserSession
    case object Valid     extends UserSession
    case object Admin     extends UserSession
  }

}
