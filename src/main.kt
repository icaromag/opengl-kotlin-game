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
    val textureCoords = floatArrayOf(
            0F, 0F, // V0
            0F, 1F,
            1F, 1F,
            1F, 0F  // V3
    )

    val loader = Loader()
    val renderer = Renderer()
    val shader = StaticShader()
    val rawModel = loader.loadToVAO(vertices, textureCoords, indices)
    val texture = ModelTexture(loader.loadTexture("image"))
    val texturedModel = TexturedModel(texture, rawModel)

    val entity = Entity(
            texturedModel, Vector3f(0F, 0F, 0F), 0F, 0F, 0F, 1F)

    do {
        entity.increaseRotation(1F, 1f, 1f)
        renderer.prepare()
        shader.start()
        renderer.render(entity, shader)
        shader.stop()
        DisplayManager.update()
    } while (!Display.isCloseRequested())

    shader.cleanUp()
    loader.clean()
    DisplayManager.close()
}
