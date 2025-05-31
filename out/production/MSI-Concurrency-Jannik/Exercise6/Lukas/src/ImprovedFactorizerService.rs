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
}

fn service(number: usize, factorizer_lock: &mut Arc<Mutex<FactorizerService>>) {

    let mut cache_hit = false;
    let mut factors = [0; REASONABLE_VALUE];

    {
        let factorizer = factorizer_lock.lock().unwrap();
        if number == factorizer.last_number {
            cache_hit = true;
            factors = factorizer.last_factors;
        }
    }

    if cache_hit {
        print_result(number, factors, cache_hit);
        return
    }
    
    factorize(number, &mut factors);

    {
        let mut factorizer = factorizer_lock.lock().unwrap();
        if number == factorizer.last_number {
            cache_hit = true;
            factors = factorizer.last_factors;
        } else { 
            factorizer.last_number = number;
            factorizer.last_factors = factors;
        }
    }
    
    print_result(number, factors, cache_hit)
}

fn main() {
    let shared_factorizer: Arc<Mutex<FactorizerService>> = Arc::new(Mutex::new(FactorizerService::new()));
    let mut shared_factorizer_1 = Arc::clone(&shared_factorizer);
    let mut shared_factorizer_2 = Arc::clone(&shared_factorizer);

    let handle1 = thread::spawn(move || {
        for number in PRIMES {
            service(number, &mut shared_factorizer_1)
        }
    });

    let handle2 = thread::spawn(move || {
        for number in PRIMES {
            service(number, &mut shared_factorizer_2)
        }
    });

    handle1.join().expect("Thread1 Panicked");
    handle2.join().expect("Thread2 Panicked");
}