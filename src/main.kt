import core.engine.DisplayManager
import core.engine.Loader
import core.engine.Renderer
import org.lwjgl.opengl.Display

fun main(args: Array<String>) {
    DisplayManager.create("ICG Loot Simulator")

    val loader = Loader()
    val renderer = Renderer()


    val rawModel = loader.loadToVAO(floatArrayOf(
            // left bottom triangle
            -.5F, .5F, 0F,
            -.5F, -.5F, 0F,
            .5F, -.5F, 0F,
            // right top triangle
            .5F, -.5F, 0F,
            .5F, .5F, 0F,
            -.5F, .5F, 0F
    ))

    do {
        renderer.prepare()
        renderer.render(rawModel)
        DisplayManager.update()
    } while (!Display.isCloseRequested())

    loader.clean()
    DisplayManager.close()
}
