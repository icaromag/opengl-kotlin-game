import core.engine.DisplayManager
import core.engine.Loader
import core.engine.Renderer
import org.lwjgl.opengl.Display

fun main(args: Array<String>) {
    DisplayManager.create("ICG Loot Simulator")

    val loader = Loader()
    val renderer = Renderer()


    val rawModel = loader.loadToVAO(floatArrayOf(
            // rectangle vertices counterclockwise
            // v0, v1, v2, v3
            -.5F, .5F, 0F,
            -.5F, -.5F, 0F,
            .5F, -.5F, 0F,
            .5F, .5F, 0F
    ), intArrayOf(0, 1, 3, 3, 1, 2))

    do {
        renderer.prepare()
        renderer.render(rawModel)
        DisplayManager.update()
    } while (!Display.isCloseRequested())

    loader.clean()
    DisplayManager.close()
}
