package net.alexhyisen.pathfinder.world;

public enum Mode {
    UNI("uni_model"),
    TRI("triangles"),
    ;

    private String desc;

    Mode(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
