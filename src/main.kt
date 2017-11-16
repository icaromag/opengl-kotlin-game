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
import textures.TerrainTexture
import textures.TerrainTexturePack

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

fun createTerrainPack(loader: Loader): TerrainTexturePack {
    val backgroundTexture = TerrainTexture(loader.loadTexture("/terrain/textures/grassy2"))
    val rTexture = TerrainTexture(loader.loadTexture("/terrain/textures/mud"))
    val gTexture = TerrainTexture(loader.loadTexture("/terrain/textures/grassFlowers"))
    val bTexture = TerrainTexture(loader.loadTexture("/terrain/textures/path"))
    // return the full pack [IM]
    return TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture)
}

fun main(args: Array<String>) {
    DisplayManager.create("ICG Loot Simulator")
    val loader = Loader()
    val renderer = MasterRenderer()
    val entities = createEntities(loader)
    val terrainPack = createTerrainPack(loader)

    // 400 is half 800 (terrain size) [IM]
    val camera = Camera(Vector3f(0F, 50F, 0F))
    val light = Light(position = Vector3f(20000F, 40000F, 20000F),
            color = Vector3f(1F, 1F, 1F))

    // loading terrain [IM]
    val blendMap = TerrainTexture(loader.loadTexture("/terrain/textures/blendMap"))
    val terrain1 = Terrain(0F, -1F, loader, terrainPack, blendMap)
    val terrain2 = Terrain(-1F, -1F, loader, terrainPack, blendMap)

    do {
        camera.move()
        // terrains [IM]
        renderer.processTerrains(terrain1)
        renderer.processTerrains(terrain2)
        // entities [IM]
//        entities.forEach { renderer.processEntity(it) }
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
