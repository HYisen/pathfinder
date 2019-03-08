module pathfinder.world {
    requires pathfinder.utility;
    requires org.joml;
    requires org.lwjgl;
    requires org.lwjgl.natives;
    requires org.lwjgl.opengl;
    requires org.lwjgl.opengl.natives;
    requires org.lwjgl.glfw;
    requires org.lwjgl.glfw.natives;

    opens shader;
    exports net.alexhyisen.pathfinder.world;
}