import core.engine.DisplayManager
import core.engine.Loader
import core.engine.Renderer
import entities.Camera
import entities.Entity
import models.TexturedModel
import org.lwjgl.opengl.Display
import org.lwjgl.util.vector.Vector3f
import shaders.StaticShader
import textures.ModelTexture

fun main(args: Array<String>) {
    DisplayManager.create("ICG Loot Simulator")

    val vertices = floatArrayOf(-0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f,

            -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f,

            0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f,

            -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,

            -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f,

            -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f)

    val textureCoordinates = floatArrayOf(

            0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f)

    val indices = intArrayOf(0, 1, 3, 3, 1, 2, 4, 5, 7, 7, 5, 6, 8, 9, 11, 11, 9, 10, 12, 13, 15, 15, 13, 14, 16, 17, 19, 19, 17, 18, 20, 21, 23, 23, 21, 22)

    val loader = Loader()
    val staticShader = StaticShader()
    val renderer = Renderer(staticShader)
    val rawModel = loader.loadToVAO(vertices, textureCoordinates, indices)
    val texture = ModelTexture(loader.loadTexture("image"))
    val texturedModel = TexturedModel(texture, rawModel)

    val entity = Entity(
            texturedModel, Vector3f(0F, 0F, -2F), 0F, 0F, 0F, 1F)
    val camera = Camera()

    do {
        entity.increaseRotation(0.2F, 0.2f, 0.2f)
//        entity.increasePosition(0F, 0f, -0.01f)
        renderer.prepare()
        camera.move()
        staticShader.start()
        staticShader.loadViewMatrix(camera)
        renderer.render(entity, staticShader)
        staticShader.stop()
        DisplayManager.update()
    } while (!Display.isCloseRequested())

    staticShader.cleanUp()
    loader.clean()
    DisplayManager.close()
}
