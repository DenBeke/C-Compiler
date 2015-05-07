#include <stdio.h>

void test(int* a) {
	for(int i = 0; i < 5; i++) {
		*a = i * 10;
		a++;
	}
}

void main() {
	int a[5];

	test(a);

	for(int i = 0; i < 5; i++) {
		printf("%d", a[i]);
	}
}
