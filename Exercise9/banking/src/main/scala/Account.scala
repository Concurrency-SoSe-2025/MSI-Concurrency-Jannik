package banking

import akka.actor.{Actor, ActorRef}

// Messages
sealed trait AccountMessage
case class Deposit(amount: Double) extends AccountMessage
case class Withdraw(amount: Double) extends AccountMessage
case object GetBalance extends AccountMessage
case class Balance(amount: Double)

// Account actor
class Account extends Actor {
  private var balance: Double = 0.0

  def receive: Receive = {
    case Deposit(amount) =>
      balance += amount
      println(s"[${self.path.name}] Deposited $amount, new balance: $balance")

    case Withdraw(amount) =>
      balance -= amount
      println(s"[${self.path.name}] Withdrew $amount, new balance: $balance")

    case GetBalance =>
      sender() ! Balance(balance)
  }
}
