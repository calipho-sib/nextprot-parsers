package org.nextprot.parser.core.actor

import akka.actor.{ ActorRef, ActorSystem, Props, Actor, Inbox }
import java.io.File
import org.nextprot.parser.core.actor.message._
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.core.exception.NXExceptionType
import org.nextprot.parser.core.exception.UNEXPECTED_EXCEPTION

/**
 * Actor responsible to parse one single file
 * @author Daniel Teixeira
 * 
 */
class NXWorker extends Actor {

  def receive = {
    case m: ProcessMSG => {
      try {
        //TODO here we should change to use reflection, the parser will depend
        val parser = Class.forName(m.parserImpl).newInstance().asInstanceOf[org.nextprot.parser.core.NXParser];
        try {
    		val wrappedBean = parser.parse(m.file.getAbsolutePath())
    		sender ! SuccessFileParsedMSG(wrappedBean)
        } catch {
          case e: NXException => {
            sender ! NXExceptionFoundMSG(new NXException(m.file, e))
          }
          case e: Exception => {
            e.printStackTrace();
            //looks like the master receives : 
            //[info] [INFO] [09/12/2013 18:57:46.146] [ParsingSystem-akka.actor.default-dispatcher-14] [akka://ParsingSystem/user/master/workerRouter/$h] Message [org.nextprot.parser.core.actor.message.ParseFileMSG] from Actor[akka://ParsingSystem/user/master#758976694] to Actor[akka://ParsingSystem/user/master/workerRouter/$h#477434584] was not delivered. [10] dead letters encountered, no more dead letters will be logged. This logging can be turned off or adjusted with configuration settings 'akka.log-dead-letters' and 'akka.log-dead-letters-during-shutdown'.
            sender ! NXExceptionFoundMSG(new NXException(m.file, UNEXPECTED_EXCEPTION, m.file.getName() + " - " + e.getMessage()))
          }
        }
      }
    }
  }
}
