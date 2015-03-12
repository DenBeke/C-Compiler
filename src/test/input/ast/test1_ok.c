int c = 123;

void main() {
	int a = 5;
	{
		int b = (a + 2);

		{}

		int c = 2;
	}

	for(int i = 0; i < 10; i++) {
		a--;
	}
}

int a = 2;
