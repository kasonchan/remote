package remote

import akka.actor._

import scala.annotation.tailrec
import scala.io.StdIn
import scala.language.postfixOps

/**
  * Created by kasonchan on 12/19/15.
  */
object Remote {

  @tailrec
  def msg(actor: ActorRef): Any = {
    StdIn.readLine() match {
      case "quit" =>
        actor ! "quit"
        println("remote system quits")
      case x =>
        actor ! x
        msg(actor)
    }
  }

  def main(args: Array[String]) {
    val system = ActorSystem("remotesystem")

    val actor = system.actorOf(Props[Worker], "remoteactor")

    actor ! "remote system starts"

    msg(actor)
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
    case "quit" =>
      log.info(sender().path.name + ": " + "quit")
      context.system.terminate()
    case x =>
      log.info(sender().path.name + ": " + x.toString)
  }
}
