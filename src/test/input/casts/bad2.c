char* func(char a) {
	return &a;
}

char a = *func('a');
char* b = func(""); // wrong argument type
