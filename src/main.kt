import core.engine.DisplayManager
import core.engine.Loader
import core.engine.OBJLoader
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

    val loader = Loader()
    val staticShader = StaticShader()
    val renderer = Renderer(staticShader)
    val rawModel = OBJLoader.loadObjModel("wolf", loader)
    val texture = ModelTexture(loader.loadTexture("wolfTexture"))
    val texturedModel = TexturedModel(texture, rawModel)

    val entity = Entity(
            texturedModel, Vector3f(0F, -0.5F, -1F), 0F, 0F, 0F, 1F)
    val camera = Camera()

    do {
        entity.increaseRotation(0F, 1F, 0F)
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
