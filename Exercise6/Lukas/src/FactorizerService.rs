use std::sync::{Arc, Mutex};
use std::thread;
use common::{factorize, print_result, REASONABLE_VALUE, PRIMES};

struct FactorizerService {
    last_number: usize,
    last_factors: [usize; REASONABLE_VALUE],
}
impl FactorizerService {
    fn new() -> Self {
        Self {
            last_number: 0,
            last_factors: [0; REASONABLE_VALUE],
        }
    }

    fn service(&mut self, number: usize) {
        
        let mut cache_hit = false;

        if number == self.last_number {
            cache_hit = true
        } else {
            let mut factors = [0; REASONABLE_VALUE];
            
            factorize(number, &mut factors);

            self.last_number = number;
            self.last_factors = factors;
        }

        print_result(self.last_number, self.last_factors, cache_hit);
    }
}

fn main() {
    let shared_factorizer: Arc<Mutex<FactorizerService>> = Arc::new(Mutex::new(FactorizerService::new()));
    let shared_factorizer_1 = Arc::clone(&shared_factorizer);
    let shared_factorizer_2 = Arc::clone(&shared_factorizer);

    let handle1 = thread::spawn(move || {
        for number in PRIMES {
            let mut factorizer = shared_factorizer_1.lock().unwrap();
            factorizer.service(number)
        }
    });

    let handle2 = thread::spawn(move || {
        for number in PRIMES {
            let mut factorizer = shared_factorizer_2.lock().unwrap();
            factorizer.service(number)
        }
    });

    handle1.join().expect("Thread1 Panicked");
    handle2.join().expect("Thread2 Panicked");
}