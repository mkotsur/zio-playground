package nl.absolutevalue.playground.authorization

import nl.absolutevalue.playground.authorization.Token.Db
import nl.absolutevalue.playground.authorization.Sec.Bind
import zio.{&, IO, Tag, ZIO, ZLayer}
import zio.http.HttpError.Unauthorized

object Sec {

  private val AuthFailureLayer = ZLayer.fail(Unauthorized("Go away"))

  /**
    * This
    * @tparam R what is needed to make the auth. decision;
    * @tparam P permissions
    */
  type Bind[R, P] = PartialFunction[R, zio.ULayer[P]]

  def apply[R, P <: Token](bind: Bind[R, P]): Sec[R, P] =
    new Sec[R, P](bind) {}

  sealed abstract class Sec[R, P](bind: Bind[R, P]) {
    def layer(env: R): zio.Layer[Unauthorized, P] =
      if (bind.isDefinedAt(env)) bind(env) else AuthFailureLayer

//    def ++[P2](that: Sec[R, P2]): Sec[R, P & P2] =
//      Sec {
//        case env: R if this.bind.isDefinedAt(env) => this.bind.apply(env)
//        case env: R if that.bind.isDefinedAt(env) => that.bind.apply(env)
//      }

  }

}
