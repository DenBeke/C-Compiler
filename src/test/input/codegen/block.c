// Blocks/Scopes

#include <stdio.h>

int a = 4;

void f(int a) {
    printf("%d", a);
}

void main() {
    
    printf("%d", a);
    
    int a = 5;
    {
        printf("%d", a);
        int a = 6;
        {
            printf("%d", a);
            int a = 7;
            {
                printf("%d", a);
                f(10);
                
            }
        }
    }
    
}