import core.engine.*
import entities.Camera
import entities.Entity
import entities.Light
import models.TexturedModel
import org.lwjgl.opengl.Display
import org.lwjgl.util.vector.Vector
import org.lwjgl.util.vector.Vector3f
import terrain.Terrain
import textures.ModelTexture

fun main(args: Array<String>) {
    DisplayManager.create("ICG Loot Simulator")

    val loader = Loader()
    val renderer = MasterRenderer()
    val rawModel = OBJLoader.loadObjModel("dragon", loader)
    val modelTexture = ModelTexture(loader.loadTexture("texture"))
    val texturedModel = TexturedModel(modelTexture, rawModel)

    // configure specular lighting factors [IM]
    modelTexture.shineDamper = 10F
    modelTexture.reflectivity = 1F


    val camera = Camera(Vector3f(50F, 10F, 0F))
    val light = Light(
            position = Vector3f(50F, 15F, 0F),
            color = Vector3f(1F, 1F, 1F))

    // entities and the terrain model [IM]
    val entity = Entity(texturedModel, Vector3f(50F, 5F, 15F),
            0F, 0F, 0F, 1F)

    val terrain1 = Terrain(0F, 0F, loader, ModelTexture(loader.loadTexture("grass")))
    val terrain2 = Terrain(1F, 0F, loader, ModelTexture(loader.loadTexture("grass")))

    // TODO change to kotlin implementation [IM]

    do {
        entity.increaseRotation(0F, 0.5F, 0F)
        camera.move()

        // terrains [IM]
        renderer.processTerrains(terrain1)
        renderer.processTerrains(terrain2)

        // entities [IM]
        renderer.processEntity(entity)
        renderer.render(light, camera)
        // loading 1 time per frame gives us the option
        //   to move the light and the camera during the
        //   game loop [IM]
        DisplayManager.update()
    } while (!Display.isCloseRequested())

    renderer.cleanUp()
    loader.clean()
    DisplayManager.close()
}
