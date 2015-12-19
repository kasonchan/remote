package remote

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.language.postfixOps

/**
  * Created by kasonchan on 12/19/15.
  */
object Remote {

  def main(args: Array[String]) {
    val system = ActorSystem("remotesystem")

    val actor = system.actorOf(Props[Worker], "remoteactor")

    actor ! "remote system starts"
  }

}

class Worker extends Actor with ActorLogging {

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
      sender() ! x.toString
      log.info(x.toString)
  }
}
