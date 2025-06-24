use std::sync::mpsc::{self, Receiver, Sender};
use std::thread;

// AccountMessage with command, amount, and a backchannel to send the new balance
#[derive(Debug)]
struct AccountMessage {
    command: i32, // 0 = Deposit, 1 = Withdraw
    amount: f64,
    response: Sender<f64>, // Backchannel to send balance result
}

// Account actor
struct Account {
    balance: f64,
    receiver: Receiver<AccountMessage>,
}

impl Account {
    // Factory method
    fn new() -> (Self, Sender<AccountMessage>) {
        let (sender, receiver) = mpsc::channel();
        let account = Account {
            balance: 0.0,
            receiver,
        };
        (account, sender)
    }

    // Actor loop
    fn run(&mut self) {
        while let Ok(message) = self.receiver.recv() {
            match message.command {
                0 => {
                    self.deposit(message.amount);
                    println!(
                        "[Account] Deposited: {}, New Balance: {}",
                        message.amount, self.balance
                    );
                }
                1 => {
                    self.withdraw(message.amount);
                    println!(
                        "[Account] Withdrew: {}, New Balance: {}",
                        message.amount, self.balance
                    );
                }
                _ => {
                    eprintln!("[Account] Unknown command: {}", message.command);
                }
            }

            // Send the new balance back to the sender
            let _ = message.response.send(self.balance);
        }

        println!(
            "[Account] All senders dropped. Final Balance: {}",
            self.balance
        );
    }

    fn deposit(&mut self, amount: f64) {
        self.balance += amount;
    }

    fn withdraw(&mut self, amount: f64) {
        self.balance -= amount;
    }
}

fn main() {
    let (mut account, sender) = Account::new();

    // Start the account actor thread
    let account_handle = thread::spawn(move || {
        account.run();
    });

    let sender1 = sender.clone();
    let sender2 = sender.clone();

    // Thread for deposits
    let deposit_thread = thread::spawn(move || {
        for i in 0..10000 {
            let (response_tx, response_rx) = mpsc::channel();
            let msg = AccountMessage {
                command: 0, // Deposit
                amount: 1.0,
                response: response_tx,
            };
            sender1.send(msg).unwrap();
            let balance = response_rx.recv().unwrap();
            println!(
                "[Deposit Thread] Iteration {}: Balance after deposit = {}",
                i, balance
            );
        }
    });

    // Thread for withdrawals
    let withdraw_thread = thread::spawn(move || {
        for i in 0..10000 {
            let (response_tx, response_rx) = mpsc::channel();
            let msg = AccountMessage {
                command: 1, // Withdraw
                amount: 1.0,
                response: response_tx,
            };
            sender2.send(msg).unwrap();
            let balance = response_rx.recv().unwrap();
            println!(
                "[Withdraw Thread] Iteration {}: Balance after withdrawal = {}",
                i, balance
            );
        }
    });

    // Wait for both threads to complete
    deposit_thread.join().unwrap();
    withdraw_thread.join().unwrap();

    // Drop the last sender to let the account thread finish
    drop(sender);
    account_handle.join().unwrap();
}
