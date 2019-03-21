package net.alexhyisen.pathfinder.world;

class Oscillator {
    private final float rank;
    private int count = 1;
    private boolean orientation = true;

    Oscillator(int rank) {
        this.rank = rank;
    }

    float next() {
        if (count == rank || count == 0) {
            orientation = !orientation;
        }
        if (orientation) {
            count++;
        } else {
            count--;
        }
        return count / rank;
    }
}
