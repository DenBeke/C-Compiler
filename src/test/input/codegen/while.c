// While loops

#include <stdio.h>

void main() {
    
    int a = 100;
    while(a >= 99) {
        printf("%d\n", a);
        a = a - 1;
        continue;
    }
    
    
    while(1) {
        // infinite while loop with break
        break;
    }
    
}