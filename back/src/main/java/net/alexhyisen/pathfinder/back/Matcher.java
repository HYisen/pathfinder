package net.alexhyisen.pathfinder.back;


import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Matcher implements Iterator<RawItem> {
    private int posA = 0;
    private int posD = 0;
    private int posR = 0;
    private List<NameDigest> listA;
    private List<NameDigest> listD;
    private List<NameDigest> listR;

    private RawItem next = null;
    private boolean exit = false;

    public Matcher(Path path) {
        TimeLogger.one.restart();

        //Using groupingByConcurrent is easy but likely not needed at the size of 10K.
        var data = Arrays
                .stream(Objects.requireNonNull(path.toFile().list()))
                .map(NameDigest::genNameDigestOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.groupingBy(NameDigest::getType));
        listA = data.get(RawType.ACCE);
        listD = data.get(RawType.DEPTH);
        listR = data.get(RawType.RGB);
        TimeLogger.one.log("loaded");

        //Would not cost much time, as the input from filesystem shall be already sorted.
        data.forEach((k, v) -> System.out.println(k + " = " + v.size()));
        data.values().forEach(v -> v.sort(Comparator.comparingLong(NameDigest::getTime)));
        TimeLogger.one.log("sorted");
    }

    @Override
    public boolean hasNext() {
        return !exit;
    }

    @Override
    public RawItem next() {
        if (next == null) {
            genNext();
        }
        RawItem rtn = next;
        next = null;
        return rtn;
    }

    private void genNext() {
        long time;
        time = listR.get(posR).getTime();
        if ((posD = findMatch(posD, listD, time)) == -1) {
            exit = true;
        }
        if ((posA = findMatch(posA, listA, time)) == -1) {
            exit = true;
        }

        NameDigest ndA = listA.get(posA);
        NameDigest ndD = listD.get(posD);
        NameDigest ndR = listR.get(posR);
        next = new RawItem(
                ndA.toFilename(),
                ndD.toFilename(),
                ndR.toFilename(),
                ndR.getTime(),
                ndA.getTime() - time,
                ndD.getTime() - time
        );
//        System.out.println(next);

        if (++posR >= listR.size()) {
            exit = true;
        }
    }


    //In this example, all the diff of pairs would output to the file log.
    public static void main(String[] args) {
//        var m = new Matcher(Path.of(".", "data", "raw"));
        var m = new Matcher(Path.of("C:\\code\\dataset\\basement_0001a"));
        m.forEachRemaining(v -> TimeLogger.print("\n" + Math.max(Math.abs(v.getDiffA()), Math.abs(v.getDiffD()))));
    }

    private static int findMatch(int pos, List<NameDigest> list, long time) {
        while (list.get(pos).getTime() < time) {
            if (++pos >= list.size()) {
                return -1;
            }
        }
        if (list.get(pos).getTime() - time > time - list.get(pos - 1 < 0 ? 0 : pos - 1).getTime()) {
            pos--;
        }
        return pos;
    }
}
