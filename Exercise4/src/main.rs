const MAX: usize = 20;

static mut LAST_NUMBER: u64 = 0;
static mut LAST_FACTORS: [u64; MAX] = [0; MAX];

fn factorizer(n: u64, output: &mut [u64; MAX]) {
    let mut num = n;
    let mut i = 2;
    let mut index = 0;

    while num > 1 && index < output.len() {
        if num % i == 0 {
            output[index] = i;
            num /= i;
            index += 1;
        } else {
            i += 1;
        }
    }

    // Fill remaining unused parts with 0
    while index < output.len() {
        output[index] = 0;
        index += 1;
    }
}

fn serviceNoCache(n: u64) -> [u64; 20] {
    let mut result: [u64; 20] = [0; 20];
    factorizer(n, &mut result);
    result
}

fn service(n: u64) -> [u64; MAX] {
    let mut result: [u64; MAX] = [0; MAX];
    unsafe {
        if n == LAST_NUMBER {
            println!("cache hit");
        } else {
            println!("cache miss");
            factorizer(n, &mut result);
            LAST_NUMBER = n;
            LAST_FACTORS = result;
        }
        LAST_FACTORS
    }
}

fn print_result(n: u64, factors: &[u64; MAX]) {
    print!("{} = ", n);
    let mut first = true;
    for &factor in factors.iter() {
        if factor == 0 {
            break;
        }
        if !first {
            print!(" * ");
        }
        print!("{}", factor);
        first = false;
    }
    println!();
}

fn main() {
    let numbers = [32567, 32568, 32568];
    for &n in numbers.iter() {
        // let result = serviceNoCache(n);
        let result = service(n);
        print_result(n, &result);
        println!();
    }
}
