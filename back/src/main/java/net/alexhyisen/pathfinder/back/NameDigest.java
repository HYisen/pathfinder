package net.alexhyisen.pathfinder.back;

import java.util.Arrays;
import java.util.Optional;

public class NameDigest {
    private final long time;
    private final long sequ;
    private final RawType type;

    public NameDigest(long time, long sequ, RawType type) {
        this.time = time;
        this.sequ = sequ;
        this.type = type;
    }

    public String toFilename() {
        var med = String.format("-%d.%06d-%d.", time / 1000000, time % 1000000, sequ);
        switch (type) {
            case ACCE:
                return "a" + med + "dump";
            case DEPTH:
                return "d" + med + "pgm";
            case RGB:
                return "r" + med + "ppm";
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString() {
        return String.format("NameDigest time=%d sequ=%d type=%s", time, sequ, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NameDigest) {
            NameDigest orig = (NameDigest)obj;
            return orig.time == time && orig.sequ == sequ && orig.type == type;
        }
        return false;
    }

    public long getTime() {
        return time;
    }

    public long getSequ() {
        return sequ;
    }

    public RawType getType() {
        return type;
    }

    public static Optional<NameDigest> genNameDigestOptional(String filename) {
        RawType type;
        long time, sequ;
        try {
            //a-1316653580.629974-1324147033.dump
            //d-1316653580.544897-1318140568.pgm
            //r-1316653630.521770-23964863.ppm
            var limbs = filename.split("-");

            //load type
            switch (limbs[0]) {
                case "a":
                    type = RawType.ACCE;
                    break;
                case "d":
                    type = RawType.DEPTH;
                    break;
                case "r":
                    type = RawType.RGB;
                    break;
                default:
                    //
                    System.out.println(filename);
                    return Optional.empty();
            }
            //assure filename correctness
            boolean status = false;
            var subs = limbs[2].split("\\.");
            switch (type) {
                case ACCE:
                    status = "dump".equals(subs[1]);
                    break;
                case DEPTH:
                    status = "pgm".equals(subs[1]);
                    break;
                case RGB:
                    status = "ppm".equals(subs[1]);
                    break;
            }
            if (!status) {
                return Optional.empty();
            }

            //load time&sequ
            var parts = limbs[1].split("\\.");
            //A fixed point structure may improve performance.
            time = Long.valueOf(parts[0]) * 1000000 + Long.valueOf(parts[1]);
            sequ = Long.valueOf(subs[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println(filename);
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(new NameDigest(time, sequ, type));
    }

    public static void main(String[] args) {
        Arrays.stream("a-1316653580.629974-1324147033.dump".split("\\.")).forEach(System.out::println);
    }
}
