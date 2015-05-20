// Pascal's triangle

#include <stdio.h>

int const D = 16;

int pascals(int *x, int *y, int d)
{
    int i;
    for (i = 1; i < d; i++) {
        y = y + i;
        int* x0 = x + i - 1;
        int* x1 = x + i;
        *y = *x0 + *x1;
        
        if(i < d-1) {
            printf("%d%c", *y, ' ');
        }
        else {
            printf("%d%c", *y, '\n');
        }
        y = y-i;
    }
        

    if(D > d) {
        return pascals(y, x, d + 1);
    }
    else {
        return 0;
    }

}

void main()
{
    int x[16] = {0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    int y[16] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    pascals(x, y, 0);
}