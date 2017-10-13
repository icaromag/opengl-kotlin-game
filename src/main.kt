import core.engine.DisplayManager
import core.engine.Loader
import core.engine.Renderer
import entities.Entity
import models.TexturedModel
import org.lwjgl.opengl.Display
import org.lwjgl.util.vector.Vector3f
import shaders.StaticShader
import textures.ModelTexture

fun main(args: Array<String>) {
    DisplayManager.create("ICG Loot Simulator")

    val vertices = floatArrayOf(
            // rectangle vertices counterclockwise
            // v0, v1, v2, v3
            -.5F, .5F, 0F,
            -.5F, -.5F, 0F,
            .5F, -.5F, 0F,
            .5F, .5F, 0F
    )
    val indices = intArrayOf(0, 1, 3, 3, 1, 2)
    val textureCoordinates = floatArrayOf(
            0F, 0F, // V0
            0F, 1F,
            1F, 1F,
            1F, 0F  // V3
    )

    val loader = Loader()
    val staticShader = StaticShader()
    val renderer = Renderer(staticShader)
    val rawModel = loader.loadToVAO(vertices, textureCoordinates, indices)
    val texture = ModelTexture(loader.loadTexture("image"))
    val texturedModel = TexturedModel(texture, rawModel)

    val entity = Entity(
            texturedModel, Vector3f(0F, 0F, -1F), 0F, 0F, 0F, 1F)

    do {
        entity.increaseRotation(0.1F, 0.1f, 0.1f)
        entity.increasePosition(0F, 0f, -0.001f)
        renderer.prepare()
        staticShader.start()
        renderer.render(entity, staticShader)
        staticShader.stop()
        DisplayManager.update()
    } while (!Display.isCloseRequested())

    staticShader.cleanUp()
    loader.clean()
    DisplayManager.close()
}
