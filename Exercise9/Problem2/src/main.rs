use std::sync::mpsc::{self, Receiver, Sender};
use std::thread;

// AccountMessage struct with numeric command code
#[derive(Debug)]
struct AccountMessage {
    command: i32, // 0 = Deposit, 1 = Withdraw
    amount: f64,
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
                        "[Account] Deposited: {}, Balance: {}",
                        message.amount, self.balance
                    );
                }
                1 => {
                    self.withdraw(message.amount);
                    println!(
                        "[Account] Withdrew: {}, Balance: {}",
                        message.amount, self.balance
                    );
                }
                _ => {
                    eprintln!("[Account] Unknown command: {}", message.command);
                }
            }
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

    // Start the account thread
    let account_handle = thread::spawn(move || {
        account.run();
    });

    let sender1 = sender.clone();
    let sender2 = sender.clone();

    // Deposit thread (sends 10000 deposit messages)
    let deposit_thread = thread::spawn(move || {
        for _ in 0..10000 {
            let msg = AccountMessage {
                command: 0, // Deposit
                amount: 1.0,
            };
            sender1.send(msg).unwrap();
        }
    });

    // Withdraw thread (sends 10000 withdraw messages)
    let withdraw_thread = thread::spawn(move || {
        for _ in 0..10000 {
            let msg = AccountMessage {
                command: 1, // Withdraw
                amount: 1.0,
            };
            sender2.send(msg).unwrap();
        }
    });

    // Wait for both threads to finish
    deposit_thread.join().unwrap();
    withdraw_thread.join().unwrap();

    // Drop the original sender to close the channel
    drop(sender);

    // Wait for the account to finish processing
    account_handle.join().unwrap();
}
