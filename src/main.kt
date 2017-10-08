import core.engine.DisplayManager
import core.engine.Loader
import core.engine.Renderer
import models.TexturedModel
import org.lwjgl.opengl.Display
import shaders.StaticShader
import textures.ModelTexture

fun main(args: Array<String>) {
    DisplayManager.create("ICG Loot Simulator")

    val loader = Loader()
    val renderer = Renderer()
    val shader = StaticShader()
    val rawModel = loader.loadToVAO(floatArrayOf(
            // rectangle vertices counterclockwise
            // v0, v1, v2, v3
            -.5F, .5F, 0F,
            -.5F, -.5F, 0F,
            .5F, -.5F, 0F,
            .5F, .5F, 0F
    ), intArrayOf(0, 1, 3, 3, 1, 2))
    val texture = ModelTexture(loader.loadTexture("image"))
    val texturedModel = TexturedModel(texture, rawModel)

    do {
        renderer.prepare()
        shader.start()
        renderer.render(texturedModel)
        shader.stop()
        DisplayManager.update()
    } while (!Display.isCloseRequested())

    shader.cleanUp()
    loader.clean()
    DisplayManager.close()
}
