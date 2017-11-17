package terrain

import core.engine.Loader
import models.RawModel
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import textures.TerrainTexture
import textures.TerrainTexturePack
import utils.Maths
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class Terrain(
        gridX: Float, gridZ: Float, loader: Loader,
        val texturePack: TerrainTexturePack,
        val blendMap: TerrainTexture, heightMapFileDirectory: String
) {

    val x: Float
    val z: Float
    val model: RawModel

    private lateinit var heights: Array<FloatArray>

    companion object {
        private val SIZE = 800F
        private val MAX_HEIGHT = 40
        private val MAX_PIXEL_COLOR = 256 * 256 * 256
        // each side of the terrain contains 128 vertices [IM]
    }

    init {
        x = gridX * SIZE
        z = gridZ * SIZE
        model = generateTerrain(loader, heightMapFileDirectory)
    }

    // TODO change this mess to something more readable [IM]
    private fun generateTerrain(loader: Loader, heightMap: String): RawModel {
        /**
         * You do not need to understand this piece of code. Just consider that it is
         *   generating a model, in this case, the terrain. Currently we are working
         *   with a simple flat terrain that should be evolved in a near future. [IM]
         */
        val image = ImageIO.read(File(heightMap))

        val vertexCount = image.height

        heights = Array(vertexCount, { FloatArray(vertexCount) })

        val count = vertexCount * vertexCount
        val vertices = FloatArray(count * 3)
        val normals = FloatArray(count * 3)
        val textureCoordinates = FloatArray(count * 2)
        val indices = IntArray(6 * (vertexCount - 1) * (vertexCount - 1))
        var vertexPointer = 0
        for (i in 0 until vertexCount) {
            for (j in 0 until vertexCount) {
                vertices[vertexPointer * 3 + 0] = j.toFloat() / (vertexCount.toFloat() - 1) * SIZE
                // height based on the offset [IM]
                val height = getHeight(j, i, image)
                heights[j][i] = height
                vertices[vertexPointer * 3 + 1] = height
                vertices[vertexPointer * 3 + 2] = i.toFloat() / (vertexCount.toFloat() - 1) * SIZE
                // normals based on normal calc method [IM]
                val normal = calculateNormal(j, i, image)
                normals[vertexPointer * 3 + 0] = normal.x
                normals[vertexPointer * 3 + 1] = normal.y
                normals[vertexPointer * 3 + 2] = normal.z
                textureCoordinates[vertexPointer * 2] = j.toFloat() / (vertexCount.toFloat() - 1)
                textureCoordinates[vertexPointer * 2 + 1] = i.toFloat() / (vertexCount.toFloat() - 1)
                vertexPointer++
            }
        }
        var pointer = 0
        for (gz in 0 until vertexCount - 1) {
            for (gx in 0 until vertexCount - 1) {
                val topLeft = gz * vertexCount + gx
                val topRight = topLeft + 1
                val bottomLeft = (gz + 1) * vertexCount + gx
                val bottomRight = bottomLeft + 1
                indices[pointer++] = topLeft
                indices[pointer++] = bottomLeft
                indices[pointer++] = topRight
                indices[pointer++] = topRight
                indices[pointer++] = bottomLeft
                indices[pointer++] = bottomRight
            }
        }
        return loader.loadToVAO(vertices, textureCoordinates, normals, indices)
    }

    private fun getHeight(x: Int, z: Int, image: BufferedImage): Float {
        if (x < 0 || x >= image.height || z < 0 || z >= image.height) {
            return 0F
        }
        var height: Float = image.getRGB(x, z).toFloat()
        height += MAX_PIXEL_COLOR / 2F
        // range between -1 and 1 [IM]
        height /= MAX_PIXEL_COLOR / 2F
        height *= MAX_HEIGHT
        return height
    }

    fun getHeightOfTerrain(worldX: Float, worldZ: Float): Float {
        val terrainX = worldX - x
        val terrainZ = worldZ - z
        // if the terrain has n size vertices, it has n-1 grid squares [IM]
        val gridSquareSize = SIZE / (heights.size - 1)
        val gridX: Int = Math.floor((terrainX / gridSquareSize).toDouble()).toInt()
        val gridZ: Int = Math.floor((terrainZ / gridSquareSize).toDouble()).toInt()
        if (gridX >= heights.size - 1 || gridZ >= heights.size - 1 ||
                gridX < 0 || gridZ < 0) {
            return 0F
        }
        val xCoordinate = (terrainX % gridSquareSize) / gridSquareSize
        val zCoordinate = (terrainZ % gridSquareSize) / gridSquareSize

        return if (xCoordinate <= (1 - zCoordinate)) {
            Maths.barryCentric(
                    Vector3f(0F, heights[gridX][gridZ], 0F),
                    Vector3f(1F, heights[gridX + 1][gridZ], 0F),
                    Vector3f(0F, heights[gridX][gridZ + 1], 1F),
                    Vector2f(xCoordinate, zCoordinate))
        } else {
            Maths.barryCentric(
                    Vector3f(1F, heights[gridX + 1][gridZ], 0F),
                    Vector3f(1F, heights[gridX + 1][gridZ + 1], 1F),
                    Vector3f(0F, heights[gridX][gridZ + 1], 1F),
                    Vector2f(xCoordinate, zCoordinate))
        }
    }

    private fun calculateNormal(x: Int, z: Int, image: BufferedImage): Vector3f {
        // this approach is not optimum. Should be changed soon [IM]
        // check this out: https://stackoverflow.com/questions/13983189/opengl-how-to-calculate-normals-in-a-terrain-height-grid
        val heightL = getHeight(x - 1, z, image)
        val heightR = getHeight(x + 1, z, image)
        val heightD = getHeight(x, z - 1, image)
        val heightU = getHeight(x, z + 1, image)
        val normal = Vector3f(heightL - heightR, 2F, heightD - heightU)
        normal.normalise()
        return normal
    }
}
