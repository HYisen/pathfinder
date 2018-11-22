package net.alexhyisen.pathfinder.utility;

public class RawItem {
    private String a;
    private String d;
    private String r;
    private long time;
    private long diffA;
    private long diffD;

    @Override
    public String toString() {
        return String.format("RawItem\n%16d|%s\n%+16d|%s\n%+16d|%s\n\n", time, r, diffD, d, diffA, a);
    }

    public RawItem(String a, String d, String r, long time, long diffA, long diffD) {
        this.a = a;
        this.d = d;
        this.r = r;
        this.time = time;
        this.diffA = diffA;
        this.diffD = diffD;
    }

    public String getA() {
        return a;
    }

    public String getD() {
        return d;
    }

    public String getR() {
        return r;
    }

    public long getTime() {
        return time;
    }

    public long getDiffA() {
        return diffA;
    }

    public long getDiffD() {
        return diffD;
    }
}
