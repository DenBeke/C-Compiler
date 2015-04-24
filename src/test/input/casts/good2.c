int factorial(int n) {
	if(n == 0) {
		return 1;
	}

	return n * factorial(n-1);
}

char a = factorial(5);
char* b = &a;
int c = factorial(factorial(*b));
