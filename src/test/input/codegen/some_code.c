// Some code...

#include <stdio.h>

char func() {
    if(0) {
        return 0;
    } else {
        return 5;
    }
}


void echo(int i) {
    void echo2(int i) {
        printf("%d", i);
    }
    echo2(i);
}


void main() {

    int a = func();
    int* b = &a;


    for(int i = 0; i <= 6; i++) {


        while(1) {
            break;
        }

        if(i != 5) {
            continue;
        }

        echo(int i);
        break;

    }


}