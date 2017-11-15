import core.engine.DisplayManager
import core.engine.Loader
import core.engine.MasterRenderer
import core.engine.objloader.OBJFileLoader
import entities.Camera
import entities.Entity
import entities.Light
import models.TexturedModel
import org.lwjgl.opengl.Display
import org.lwjgl.util.vector.Vector3f
import terrain.Terrain
import textures.ModelTexture

fun createEntities(loader: Loader): MutableList<Entity> {
    val entities = mutableListOf<Entity>()

    // grass
    val grassOBJModelData = OBJFileLoader.loadOBJ("grassModel")
    val grassRawModel = loader.loadToVAO(
            grassOBJModelData.vertices, grassOBJModelData.textureCoords, grassOBJModelData.normals, grassOBJModelData.indices)
    val grassTexture = ModelTexture(loader.loadTexture("grassTexture"))
    grassTexture.hasTransparency = true
    // TODO fix fake lighting [IM]
    // grassTexture.useFakeLighting = true
    val grassTexturedModel = TexturedModel(grassTexture, grassRawModel)
    val entityGrass = Entity(grassTexturedModel, Vector3f(400F, 0F, 380F),
            0F, 0F, 0F, 2F)

    // dragon
    val dragonOBJModelData = OBJFileLoader.loadOBJ("dragon")
    val dragonRawModel = loader.loadToVAO(
            dragonOBJModelData.vertices, dragonOBJModelData.textureCoords, dragonOBJModelData.normals, dragonOBJModelData.indices)
    // load dragon [IM]
    val entityDragonTexture = ModelTexture(loader.loadTexture("texture"))
    val texturedModel = TexturedModel(entityDragonTexture, dragonRawModel)
    // configure specular lighting factors [IM]
    entityDragonTexture.shineDamper = 10F
    entityDragonTexture.reflectivity = 1F
    val entityDragon = Entity(texturedModel, Vector3f(400F, 0F, 380F),
            0F, 0F, 0F, 1F)


    entities.add(entityDragon)
    entities.add(entityGrass)
    return entities
}

fun main(args: Array<String>) {
    DisplayManager.create("ICG Loot Simulator")

    val loader = Loader()

    val renderer = MasterRenderer()


    val entities = createEntities(loader)

    // 400 is half 800 (terrain size) [IM]
    val camera = Camera(Vector3f(400F, 10F, 350F))
    val light = Light(position = Vector3f(400F, 20000F, 500F),
            color = Vector3f(1F, 1F, 1F))

    // loading terrain [IM]
    val terrainGrassTexture = ModelTexture(loader.loadTexture("grass"))
    terrainGrassTexture.shineDamper = 10F
    val terrain = Terrain(0F, 0F, loader, terrainGrassTexture)


    do {
        camera.move()

        // terrains [IM]
        renderer.processTerrains(terrain)

        // entities [IM]
        entities.forEach {
            renderer.processEntity(it)
        }
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
