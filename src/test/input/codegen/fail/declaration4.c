// Declared in other scope

#include <stdio.h>

int main() {

    int a = 5;
    int b = 3;
    
    if(1) {
        int c = a + b;
    }
    
    printf("%d", c);

}