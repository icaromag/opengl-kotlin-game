import core.engine.DisplayManager
import core.engine.Loader
import core.engine.OBJLoader
import core.engine.Renderer
import entities.Camera
import entities.Entity
import entities.Light
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
    val rawModel = OBJLoader.loadObjModel("dragon", loader)
    val texture = ModelTexture(loader.loadTexture("texture"))
    val texturedModel = TexturedModel(texture, rawModel)
    texturedModel.texture.shineDamper = 10F
    texturedModel.texture.reflectivity = 1F
    texture.reflectivity = 1F

    val entity = Entity(
            texturedModel, Vector3f(0F, 0F, -25F), 0F, 0F, 0F, 1F)
    val camera = Camera()
    val light = Light(position = Vector3f(0F, 0F, -10F), color = Vector3f(1F, 1F, 1F))

    do {
        entity.increaseRotation(0F, 0.5F, 0F)
        renderer.prepare()
        camera.move()
        staticShader.start()
        // loading 1 time per frame gives us the option
        //   to move the light and the camera during the
        //   game loop [IM]
        staticShader.loadLight(light)
        staticShader.loadViewMatrix(camera)
        renderer.render(entity, staticShader)
        staticShader.stop()
        DisplayManager.update()
    } while (!Display.isCloseRequested())

    staticShader.cleanUp()
    loader.clean()
    DisplayManager.close()
}
