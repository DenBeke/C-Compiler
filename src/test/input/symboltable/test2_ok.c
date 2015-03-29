// No redeclarations

int b;

int a() {
    int a = 5;
    int b;
    {
        int b;
        {
            int b;
        }
    }
    return 0;
}

int main() {
    return 0;
}