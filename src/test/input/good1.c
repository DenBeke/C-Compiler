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
	if(a == 5) {
		return 5;
	}

	if(b == 0) {
		return "test";
	} else {
		int c = 1;
		return c;
	}

	while(1)
		if(1)
			a = 5;
		else
			b = 1;

	return s;
}

/*
	some more comments
	on multiple lines
*/
void* funcA(char* abc) {
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
		return 'a';
	}

	for(int i = 0; i < 10; i++) {
		continue;
	}

	for(a = 5;;) {
		if(1==2)
			funcB(a);
		else
			funcA(a);
	}
}

int a = 6;
int b;
char* c = 1+2;
