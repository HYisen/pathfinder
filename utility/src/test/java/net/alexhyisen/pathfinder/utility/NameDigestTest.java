package net.alexhyisen.pathfinder.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class NameDigestTest {
    @Test
    void toFilename() {
        NameDigest item;

        item = new NameDigest(1316653580629974L, 1324147033L, RawType.ACCE);
        assert "a-1316653580.629974-1324147033.dump".equals(item.toFilename());

        item = new NameDigest(1316653580544897L, 1318140568L, RawType.DEPTH);
        assert "d-1316653580.544897-1318140568.pgm".equals(item.toFilename());

        item = new NameDigest(1316653630521770L, 23964863, RawType.RGB);
        assert "r-1316653630.521770-23964863.ppm".equals(item.toFilename());

        item = new NameDigest(1000001L, 17, RawType.RGB);
        Assertions.assertEquals("r-1.000001-17.ppm",item.toFilename());
        //TODO : replace all the primitive assert with Assertions::method like the previous snippet.
    }

    @Test
    void equals() {
        NameDigest lhs, rhs;
        lhs = new NameDigest(1000, 1000, RawType.RGB);

        rhs = new NameDigest(1000, 1000, RawType.RGB);
        assert lhs.equals(rhs);

        rhs = new NameDigest(4000, 1000, RawType.RGB);
        assert !lhs.equals(rhs);

        rhs = new NameDigest(1000, 4000, RawType.RGB);
        assert !lhs.equals(rhs);

        rhs = new NameDigest(1000, 1000, RawType.DEPTH);
        assert !lhs.equals(rhs);

        rhs = new NameDigest(4000, 4000, RawType.RGB);
        assert !lhs.equals(rhs);

        rhs = new NameDigest(4000, 1000, RawType.DEPTH);
        assert !lhs.equals(rhs);

        rhs = new NameDigest(1000, 4000, RawType.DEPTH);
        assert !lhs.equals(rhs);

        rhs = new NameDigest(4000, 4000, RawType.DEPTH);
        assert !lhs.equals(rhs);
    }

    @Test
    void genNameDigestOptional() {
        Optional<NameDigest> item;
        NameDigest orig;

        item = NameDigest.genNameDigestOptional("a-1316653580.629974-1324147033.dump");
        orig = new NameDigest(1316653580629974L, 1324147033L, RawType.ACCE);
        assert item.isPresent() && item.get().equals(orig);

        item = NameDigest.genNameDigestOptional("d-1316653580.544897-1318140568.pgm");
        orig = new NameDigest(1316653580544897L, 1318140568L, RawType.DEPTH);
        assert item.isPresent() && item.get().equals(orig);

        item = NameDigest.genNameDigestOptional("r-1316653630.521770-23964863.ppm");
        orig = new NameDigest(1316653630521770L, 23964863, RawType.RGB);
        assert item.isPresent() && item.get().equals(orig);

        item = NameDigest.genNameDigestOptional(".");
        assert item.isEmpty();

        item = NameDigest.genNameDigestOptional("in god we trust");
        assert item.isEmpty();

        item = NameDigest.genNameDigestOptional("r-1316653630.521770-23964863.dump");
        assert item.isEmpty();
    }
}