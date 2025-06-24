package banking

import akka.actor.{ActorSystem, Props}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import akka.util.Timeout

object BankingApp extends App {
  val system = ActorSystem("BankingSystem")
  val account = system.actorOf(Props[Account], "SharedAccount")

  val numOperations = 1000
  val amount = 1.0

  def doDepositsAndWithdrawals(): Future[Unit] = Future {
    for (_ <- 1 to numOperations) {
      account ! Deposit(amount)
      account ! Withdraw(amount)
    }
  }

  val future1 = doDepositsAndWithdrawals()
  val future2 = doDepositsAndWithdrawals()

  val combined = for {
    _ <- future1
    _ <- future2
  } yield ()

  Await.result(combined, 30.seconds)

  Thread.sleep(1000) // wait for messages to process

  implicit val timeout: Timeout = Timeout(5.seconds)
  val futureBalance = (account ? GetBalance).mapTo[Balance]
  val finalBalance = Await.result(futureBalance, timeout.duration)

  println(s"\nFinal balance should be 0.0")
  println(s"Final balance: ${finalBalance.amount}")

  system.terminate()
}
