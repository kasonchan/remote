package local

import akka.actor._
import akka.util.Timeout

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.io.StdIn
import scala.language.postfixOps

object Local {

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
    val system = ActorSystem("localsystem")

    val actor = system.actorOf(Props[Worker], "localactor")

    actor ! "local system starts"

    msg(actor)
  }

}

class Worker extends Actor with ActorLogging {

  lazy val timeout = Timeout(10 seconds)

  val remote = context.actorSelection("akka.tcp://remotesystem@127.0.0.1:5150/user/remoteactor")

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
    case "local system starts" =>
    case "quit" =>
      remote ! "quit"
      context.system.terminate()
    case x =>
      remote ! x
      log.info(sender().path.name + ": " + x.toString)
  }
}
