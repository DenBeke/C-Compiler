/*
Tests:
	comments
	function declarations
	if else statements
	while statements
	for statements
	return statements
	variable declarations
	operators
	continue, break statements
	function calls
*/

// Some comment
int *funcA(int a, int b, char* s) {
	int* d;
	int** e = &d;
	if(a == 5) {
		return d;
	}

	if(b == 0) {
		return &a;
	} else {
		int c = 1;
		return *e;
	}

	while(1)
		if(1)
			a = 5;
		else
			b = 1;

	return d;
}

/*
	some more comments
	on multiple lines
*/
char* funcB(char* abc) {
	int i = 0;
	while(i != 5) { // While comment
		i = i + 1; /* i = i + 1 */
		continue;
	}

	if(i == 5) {
		return "test"; // return something
	} else {
		/*
			return something else
		*/
		break;
		return abc;
	}

	for(i = 0; i < 10; i++) {
		continue;
	}
	int a = 0;
	for(a = 5;;) {
		if(1==2)
			funcB("abc");
		else
			funcA(a, i, abc);
	}
}

int a = 6;
int b;
char* c;
