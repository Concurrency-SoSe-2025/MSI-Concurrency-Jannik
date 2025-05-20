use std::sync::{Arc, Mutex};
use std::thread;

pub struct Account {
    balance: isize
}

impl Account {
    pub fn new() -> Self {
        Self {
            balance: 0,
        }
    }

    fn deposit(&mut self, amount: isize) {
        self.balance += amount
    }

    fn withdraw(&mut self, amount: isize) {
        self.balance -= amount
    }

    pub fn get_balance(&self) -> isize {
        self.balance
    }
}

pub fn execute(command: &str, account: &Arc<Mutex<Account>>, amount: isize) {
    let mut account_m = account.lock().unwrap();

    if command == "deposit" {
        account_m.deposit(amount)
    } else if command == "withdraw" {
        account_m.withdraw(amount)
    } else {
        println!("Unknown command {command}")
    }
}

fn main() {
    let shared_account: Arc<Mutex<Account>> = Arc::new(Mutex::new(Account::new()));
    let shared_account_1 = Arc::clone(&shared_account);
    let shared_account_2 = Arc::clone(&shared_account);

    let handle1 = thread::spawn(move || {
        for number in 1..1000 {
            println!("Withdraw {number}€");
            execute("withdraw", &shared_account_1, number)
        }
    });

    let handle2 = thread::spawn(move || {
        for number in 1..1000 {
            println!("Deposit {number}€");
            execute("deposit", &shared_account_2, number)
        }
    });

    handle1.join().expect("Thread1 Panicked");
    handle2.join().expect("Thread2 Panicked");

    let account = shared_account.lock().unwrap();
    let balance = account.get_balance();
    println!("{balance}€ in Account.")
}
