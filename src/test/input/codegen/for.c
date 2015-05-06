// Simple for loop

#include <stdio.h>

void main() {
    
    for(int i = 0; i < 5; i = i + 1) {
        printf("%d", i);
        i--;
        i++;
        continue;
    }
    
    for(i = 0; i < 5; i++) {
        if(1) {
            continue;
        }
    }
    
    for(i = 0; i < 5; i++) {
        {
            {
                {
                    {
                        continue;
                    }
                }
            }
        }
    }
    
    for(i = 0; i < 5; i++) {
        {
            {
                {
                    {
                        break;
                    }
                }
            }
        }
    }

    
    
    for(;;) {
        // infinite for loop, with break
        break;
    }
    
}
