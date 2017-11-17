import core.engine.DisplayManager
import core.engine.Loader
import core.engine.MasterRenderer
import core.engine.TerrainRenderer
import core.engine.objloader.OBJFileLoader
import entities.Camera
import entities.Entity
import entities.Light
import entities.Player
import models.TexturedModel
import org.lwjgl.opengl.Display
import org.lwjgl.util.vector.Vector3f
import terrain.Terrain
import textures.ModelTexture
import textures.TerrainTexture
import textures.TerrainTexturePack
import java.util.*

val random = Random()

fun loadPlayer(loader: Loader): Player {
    // dragon
    val dragonOBJModelData = OBJFileLoader.loadOBJ("dragon")
    val dragonRawModel = loader.loadToVAO(
            dragonOBJModelData.vertices, dragonOBJModelData.textureCoords, dragonOBJModelData.normals, dragonOBJModelData.indices)
    // load dragon [IM]
    val entityDragonTexture = ModelTexture(loader.loadTexture("texture"))
    // configure specular lighting factors [IM]
    entityDragonTexture.shineDamper = 5F
    entityDragonTexture.reflectivity = 3F
    val texturedModel = TexturedModel(entityDragonTexture, dragonRawModel)

    return Player(texturedModel, Vector3f(100F, 5F, -150F),
            0F, 180F, 0F, 0.6F)
}

fun createEntities(loader: Loader, terrain: Terrain): MutableList<Entity> {
    val entities = mutableListOf<Entity>()
    // low poly tree
    val lowPolyTreeOBJModelData = OBJFileLoader.loadOBJ("tree")
    val lowPolyTreeRawModel = loader.loadToVAO(
            lowPolyTreeOBJModelData.vertices, lowPolyTreeOBJModelData.textureCoords, lowPolyTreeOBJModelData.normals, lowPolyTreeOBJModelData.indices)
    val lowPolyTreeTexture = ModelTexture(loader.loadTexture("tree"))
    val lowPolyTreeModel = TexturedModel(lowPolyTreeTexture, lowPolyTreeRawModel)

    // grass
    val grassOBJModelData = OBJFileLoader.loadOBJ("grassModel")
    val grassRawModel = loader.loadToVAO(
            grassOBJModelData.vertices, grassOBJModelData.textureCoords, grassOBJModelData.normals, grassOBJModelData.indices)
    val grassTexture = ModelTexture(loader.loadTexture("grassTexture"))
    grassTexture.hasTransparency = true
    grassTexture.useFakeLighting = true
    val grassTexturedModel = TexturedModel(grassTexture, grassRawModel)

    // ferns
    val fernOBJModelData = OBJFileLoader.loadOBJ("fern")
    val fernRawModel = loader.loadToVAO(
            fernOBJModelData.vertices, fernOBJModelData.textureCoords, fernOBJModelData.normals, fernOBJModelData.indices)
    val fernTexture = ModelTexture(loader.loadTexture("fern"))
    fernTexture.numberOfRows = 2
    fernTexture.hasTransparency = true
    fernTexture.shineDamper = 10F
    fernTexture.reflectivity = 0.4F

    val fernTexturedModel = TexturedModel(fernTexture, fernRawModel)
    for (i in 1 until 300) {
        val entityFern = Entity(fernTexturedModel, terrain.getRandomLocation(),
                0F, 0F, 0F, 0.6F, random.nextInt(4))

        if(i%50 == 0) {
            val entityGrass = Entity(grassTexturedModel, terrain.getRandomLocation(),
                    0F, 0F, 0F, 1F)
            entities.add(entityGrass)
            val entityLowPolyTree = Entity(lowPolyTreeModel, terrain.getRandomLocation(),
                    0F, 0F, 0F, 10F)
            entities.add(entityLowPolyTree)
        }
        entities.add(entityFern)

    }

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
    val terrainPack = createTerrainPack(loader)

    // loading terrain [IM]
    val blendMap = TerrainTexture(loader.loadTexture("/terrain/textures/blendMap"))
    val heightMapFileDirectory = "res/terrain/textures/heightmap.png"
    val terrain = Terrain(0F, -1F, loader, terrainPack, blendMap, heightMapFileDirectory)
    val entities = createEntities(loader, terrain)

    val player = loadPlayer(loader)
    val camera = Camera(player, Vector3f(0F, 50F, 0F))
    val light = Light(position = Vector3f(20000F, 40000F, 20000F),
            color = Vector3f(1F, 1F, 1F))

    do {
        player.move(terrain)
        camera.move()
        renderer.processEntity(player)
        // terrains [IM]
        renderer.processTerrains(terrain)
        // entities [IM]
        entities.forEach { renderer.processEntity(it) }
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
