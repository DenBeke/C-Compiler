#include <stdio.h>

const int WIDTH = 15;
const int HEIGHT = 15;
// Be sure its WIDTH * HEIGHT
char map[225];

const int MAXLENGTH = 30;
// Be sure its MAXLENGTH
int player[30];
int length;

// Whether a price is on the field or not
int pricespawned = 0;

int turn = 0;
int score = 0;

int modulo(int a, int b) {
	while(a >= b) {
		a = a - b;
	}

	return a;
}

int topos(int x, int y) {
	return y * HEIGHT + x;
}

int xfrompos(int pos) {
	return modulo(pos, HEIGHT);
}

int yfrompos(int pos) {
	return pos / HEIGHT;
}

void initmap() {
	for(int y = 0; y < HEIGHT; y++) {
		for(int x = 0; x < WIDTH; x++) {
			int pos = topos(x, y);

			if(x == 0 || x == WIDTH-1 || y == 0 || y == HEIGHT-1) {
				map[pos] = '#';
			} else {
				map[pos] = ' ';
			}
		}
	}

	for(int i = 0; i < length; i++) {
		map[player[i]] = '@';
	}


}

void initplayer() {
	player[0] = topos(WIDTH/2, HEIGHT/2);
	length = 1;
}

void printmap() {
	for(int y = 0; y < HEIGHT; y++) {
		for(int x = 0; x < WIDTH; x++) {
			int pos = topos(x, y);
			printf("%c", map[pos]);
		}

		printf("\n");
	}
}


int move(int x, int y) {
	int newpos = topos(x,y);
	// Ignore if we try to move into our first child
	if(length > 1) {
		if(newpos == player[1]) {
			return 0;
		}
	}

	turn++;

	if(map[newpos] == '#' || map[newpos] == 'x') {
		return 1;
	}

	int scored = 0;

	if(map[newpos] == '$') {
		pricespawned = 0;
		score = score + length;
		scored = 1;
	}

	if(scored && length < MAXLENGTH) {
		map[newpos] = '@';
		map[player[0]] = 'x';

		for(int i = length - 1; i > -1 ; i--) {
			player[i+1] = player[i];
		}
		player[0] = newpos;
		
		length++;
		
	} else {
		map[newpos] = '@';
		map[player[length - 1]] = ' ';
		if(length > 1) {
			map[player[0]] = 'x';
		}

		int headpos = newpos;
		for(int i = 0; i < length; i++) {
			int prevpos = player[i];
			player[i] = headpos;
			headpos = prevpos;
		}
	}

	return 0;
}

int spawnmod = 1;
void handlespawnprice() {
	if(modulo(turn, spawnmod) != 0) {
		return;
	}

	if(pricespawned) {
		return;
	}

	int freespots = 0;
	// Get number of freespots
	for(int y = 1; y < HEIGHT - 1; y++) {
		for(int x = 1; x < WIDTH - 1; x++) {
			int tpos = topos(x, y);

			if(map[tpos] == ' ') {
				freespots++;
			}
		}
	}

	if(freespots == 0) {
		return;
	}

	int pos = -1;
	int random = modulo((score + freespots) / 2 + freespots / 5 * (turn + 3), freespots);
	for(int y = 1; y < HEIGHT - 1; y++) {
		for(int x = 1; x < WIDTH - 1; x++) {
			int tpos = topos(x, y);

			if(map[tpos] == ' ') {
				random--;
			}

			if(random == 0) {
				pos = tpos;
			}
		}
	}

	if(pos == -1) {
		return;
	}

	map[pos] = '$';

	pricespawned = 1;
	spawnmod = modulo((score + freespots) / 2 + freespots / 5 * (turn + 3), 8);
	if(spawnmod == 0) {
		spawnmod++;
	}
}

void main() {
	initplayer();
	initmap();

	printf("*****************************************\n");
	printf("*         WELCOME TO P-SNAKE            *\n");
	printf("*           version 0.0.1a              *\n");
	printf("*           by Timo Truyts              *\n");
	printf("* This software is licensed under WTFPL *\n");
	printf("*****************************************\n\n");
	printf("Objective: Stay alive as long as possible\n");
	printf("           by not running into a wall or\n");
	printf("           into yourself. Meanwhile try to\n");
	printf("           eat as much prices as you can.\n\n");
	printf("Input:     Input is given by entering a character\n");
	printf("           followed by an enter.\n");
	printf("'w'|'z' = Up\n");
	printf("'s' = Down\n");
	printf("'a'|'q' = Left\n");
	printf("'d' = Right\n");
	printf("'x' = Quit the game\n\n");
	printf("Legend:\n");
	printf("'#' = Wall\n'@' = Snake's head\n'x' = Snake's body\n'$' = Price\n\n");

	char tmp = ' ';
	while(tmp != 's' && tmp != 'x') {
		printf("Press 's' to start the game or 'x' to quit: ");
		scanf("%c", &tmp);
	}

	int quit = 0;

	if(tmp == 'x') {
		quit = 1;
	}

	while(!quit) {
		handlespawnprice();
		printmap();
		printf("Score: %d\nTurn: %d\n_________________________\n", score, turn);

		char c;
		printf("Input: ");
		scanf("%c", &c);
		printf("\n");

		if(c == 'w' || c == 'z') {
			quit = move(xfrompos(player[0]), yfrompos(player[0]) - 1);
		}
		if(c == 's') {
			quit = move(xfrompos(player[0]), yfrompos(player[0]) + 1);
		}
		if(c == 'a' || c == 'q') {
			quit = move(xfrompos(player[0]) - 1, yfrompos(player[0]));
		}
		if(c == 'd') {
			quit = move(xfrompos(player[0]) + 1, yfrompos(player[0]));
		}
		if(c == 'x') {
			quit = 1;
		}
	}

	printf("Game over!\nScore: %d\nTurns: %d\n_________________________\n", score, turn);
}

