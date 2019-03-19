package net.alexhyisen.pathfinder.utility;

@FunctionalInterface
public interface QuadFunction<T0, T1, T2, T3, R> {
    R apply(T0 t0, T1 t1, T2 t2, T3 t3);
}
