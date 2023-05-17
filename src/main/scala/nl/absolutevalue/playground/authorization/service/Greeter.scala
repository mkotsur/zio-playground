package nl.absolutevalue.playground.authorization.service

object Greeter {

  // We don't have to "protect" this method.
  def hello() = zio.Console.printLine("Hello, World!")
}
