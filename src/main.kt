import core.engine.DisplayManager
import core.engine.Loader
import core.engine.MasterRenderer
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
    val dragonOBJModelData = OBJFileLoader.loadOBJ("player")
    val dragonRawModel = loader.loadToVAO(
            dragonOBJModelData.vertices, dragonOBJModelData.textureCoords, dragonOBJModelData.normals, dragonOBJModelData.indices)
    // load dragon [IM]
    val entityDragonTexture = ModelTexture(loader.loadTexture("player"))
    // configure specular lighting factors [IM]
    entityDragonTexture.shineDamper = 5F
    entityDragonTexture.reflectivity = 3F
    val texturedModel = TexturedModel(entityDragonTexture, dragonRawModel)

    return Player(texturedModel, Vector3f(153F, 5F, -274F),
            0F, 100F, 0F, 1F)
}

fun createEntities(entities: MutableList<Entity>, loader: Loader, terrain: Terrain) {
    // pine
    // low poly tree
    val pineOBJModelData = OBJFileLoader.loadOBJ("pine")
    val pineTexture = ModelTexture(loader.loadTexture("pine"))
    val pineRawModel = loader.loadToVAO(
            pineOBJModelData.vertices,
            pineOBJModelData.textureCoords,
            pineOBJModelData.normals,
            pineOBJModelData.indices
    )
    pineTexture.hasTransparency = true
    pineTexture.shineDamper = 10F
    pineTexture.reflectivity = 0.4F
    val pineModel = TexturedModel(pineTexture, pineRawModel)

    // grass
    val grassOBJModelData = OBJFileLoader.loadOBJ("grassModel")
    val grassTexture = ModelTexture(loader.loadTexture("grassTexture"))
    val grassRawModel = loader.loadToVAO(
            grassOBJModelData.vertices,
            grassOBJModelData.textureCoords,
            grassOBJModelData.normals,
            grassOBJModelData.indices
    )
    grassTexture.hasTransparency = true
    grassTexture.useFakeLighting = true
    val grassTexturedModel = TexturedModel(grassTexture, grassRawModel)

    // fern
    val fernOBJModelData = OBJFileLoader.loadOBJ("fern")
    val fernTexture = ModelTexture(loader.loadTexture("fern"))
    val fernRawModel = loader.loadToVAO(
            fernOBJModelData.vertices,
            fernOBJModelData.textureCoords,
            fernOBJModelData.normals,
            fernOBJModelData.indices
    )
    fernTexture.numberOfRows = 2
    fernTexture.hasTransparency = true
    fernTexture.shineDamper = 10F
    fernTexture.reflectivity = 0.4F
    val fernTexturedModel = TexturedModel(fernTexture, fernRawModel)

    for (i in 1 until 20) {
        val entityFern = Entity(fernTexturedModel, terrain.getRandomLocation(),
                0F, 0F, 0F, 0.6F, random.nextInt(4))
        entities.add(entityFern)
        val entityGrass = Entity(grassTexturedModel, terrain.getRandomLocation(),
                0F, 0F, 0F, 1F)
        entities.add(entityGrass)

        val pine = Entity(pineModel, terrain.getRandomLocation(),
                0F, 0F, 0F, 3F)
        entities.add(pine)
    }
}

fun createLights(entities: MutableList<Entity>, lights: MutableList<Light>, loader: Loader, terrain: Terrain) {
    var entity: Entity
    var light: Light
    var pos: Vector3f

    val lightPost = OBJFileLoader.loadOBJ("lamp")
    val lightPostTexture = ModelTexture(loader.loadTexture("lamp"))
    val lightPostRawModel = loader.loadToVAO(
            lightPost.vertices,
            lightPost.textureCoords,
            lightPost.normals,
            lightPost.indices
    )

    val lightPostModel = TexturedModel(lightPostTexture, lightPostRawModel)
    val positions = mutableListOf<Vector3f>(Vector3f(153F, 0F, -250F), Vector3f(230F, 0F, -150F))

    positions.forEach {
        pos = it
        entity = Entity(
                lightPostModel, Vector3f(
                pos.x, terrain.getHeightOfTerrain(pos.x, pos.z), pos.z),
                0F, 0F, 0F, 1F
        )
        light = Light(
                position = Vector3f(pos.x, entity.position.y + 7, pos.z),
                color = Vector3f(1F, 1F, 1F),
                attenuation = Vector3f(0.01F, 0.01F, 0.001F))
        entities.add(entity)
        lights.add(light)
    }

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
    val renderer = MasterRenderer(loader)
    val terrainPack = createTerrainPack(loader)

    // loading terrain [IM]
    val blendMap = TerrainTexture(loader.loadTexture("/terrain/textures/blendMap"))
    val heightMapFileDirectory = "res/terrain/textures/heightmap.png"
    val terrain = Terrain(0F, -1F, loader, terrainPack, blendMap, heightMapFileDirectory)

    val entities = mutableListOf<Entity>()
    val lights = mutableListOf<Light>()
    createEntities(entities, loader, terrain)
    createLights(entities, lights, loader, terrain)

    val player = loadPlayer(loader)
    val camera = Camera(player, Vector3f(0F, 50F, 0F))

    do {
        player.move(terrain)
        camera.move()
        renderer.processEntity(player)
        // terrains [IM]
        renderer.processTerrains(terrain)
        // entities [IM]
        entities.forEach { renderer.processEntity(it) }
        renderer.render(lights, camera)
        // loading 1 time per frame gives us the option
        //   to move the light and the camera during the
        //   game loop [IM]
        DisplayManager.update()
    } while (!Display.isCloseRequested())

    renderer.cleanUp()
    loader.clean()
    DisplayManager.close()
}

