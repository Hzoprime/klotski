package me.hb.huarongdao01;

class Coordinate {

    int row;
    int column;

    Coordinate() {
        row = 0;
        column = 0;
    }

    Coordinate(int r, int c) {
        row = r;
        column = c;
    }
}


class ButtonShape {

    final static ButtonShape vertical;
    final static ButtonShape horizontal;
    final static ButtonShape block;
    final static ButtonShape point;

    static {
        vertical = new ButtonShape(2, 1);
        horizontal = new ButtonShape(1, 2);
        point = new ButtonShape(1, 1);
        block = new ButtonShape(2, 2);
    }

    int width;
    int height;

    ButtonShape() {
        width = 0;
        height = 0;
    }

    ButtonShape(int h, int w) {
        width = w;
        height = h;
    }
}