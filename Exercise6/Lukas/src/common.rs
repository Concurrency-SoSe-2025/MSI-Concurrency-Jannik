pub const REASONABLE_VALUE: usize = 20;

pub const PRIMES: [usize; 3] = [48597, 164903, 75623];

pub fn factorize(mut number: usize, factors: &mut [usize; REASONABLE_VALUE]) -> &mut [usize; REASONABLE_VALUE] {
    let mut count = 0;
    let mut f = 2;

    while f * f <= number && count < factors.len() {
        while number % f == 0 {
            factors[count] = f;
            count += 1;
            number /= f;
        }
        f += 1;
    }

    if number > 1 && count < factors.len() {
        factors[count] = number;
    }

    factors
}

pub fn print_result(number: usize, factors: [usize; REASONABLE_VALUE], cache_hit: bool) {
    let output = factors
        .iter()
        .filter(|&&n| n != 0)
        .map(|n| n.to_string())
        .collect::<Vec<_>>()
        .join(" * ");

    let state = if cache_hit { "Hit" } else { "Miss" };

    println!("Cache {state}: {number} = {output}");
}