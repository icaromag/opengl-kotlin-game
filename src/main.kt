import org.lwjgl.opengl.Display

fun main(args: Array<String>) {
    DisplayManager.create("ICG Loot Simulator")
    do {
        DisplayManager.update()
    } while (!Display.isCloseRequested())
    DisplayManager.close()
}
