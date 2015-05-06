// Functions

#include <stdio.h>

// Fibonacci
int fib(int n) {
    
    if(n == 0) {
        return 0;
    }
    else if(n == 1) {
        return 1;
    }
    
    return fib(n - 1) + fib(n - 2);
    
}

// Factorial
int fac(int n) {
    if(n == 1 || n == 0) {
        return 1;
    }
    return n * fac(n - 1);
}


void main() {
    
    // Nested function
    void hello_world() {
        print("Hello World!\n");
    }
    
    hello_world();
    
    printf("Fib = %d\n", fib(10));
    
    printf("Fac = %d\n", fac(4));
    
    printf(":)  = %d\n", fib(fac(3)));
    
    int f = fac(3);
    printf(":)  = %d\n", fib(f));
    
}