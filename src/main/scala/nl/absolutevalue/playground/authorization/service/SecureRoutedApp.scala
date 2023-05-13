package nl.absolutevalue.playground.authorization.service
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.http._

object SecureRoutedApp extends ZIOAppDefault {

  private val app = Http.collect[Request] {
    case Method.GET -> !! / "fruits" / "a" => Response.text("Apple")
    case Method.GET -> !! / "fruits" / "b" => Response.text("Banana")
  }

  override def run =
    Server.serve(app).provide(Server.defaultWithPort(8090))
}
