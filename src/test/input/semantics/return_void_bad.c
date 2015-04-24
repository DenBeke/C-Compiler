// Empty return in function that takes int as return value -> not ok!

int a() {
    if(0) {
        return 0;
    }
    else {
        return;
    }
}