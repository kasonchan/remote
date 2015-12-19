package local

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.language.postfixOps

object Local {

  def main(args: Array[String]) {
    val system = ActorSystem("localsystem")

    val actor = system.actorOf(Props[Worker], "localactor")

    actor ! "local system starts"

    actor ! "Testing"

    actor ! "Testing"
  }

}

class Worker extends Actor with ActorLogging {

  lazy val timeout = Timeout(10 seconds)

  val remote = context.actorSelection("akka.tcp://remotesystem@127.0.0.1:2552/user/remoteactor")

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info("Pre-restart " + reason.toString)
  }

  override def postStop(): Unit = {
    log.info("Post-stop")
  }

  override def postRestart(reason: Throwable): Unit = {
    log.info("Post-restart " + reason.toString)
  }

  override def preStart(): Unit = {
    log.info("Pre-start")
  }

  def receive: PartialFunction[Any, Unit] = {
    case x =>
      sender() ! "I don't know"
      remote ! "Hey"
      log.info(x.toString)
  }
}
