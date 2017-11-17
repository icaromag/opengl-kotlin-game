package terrain

import core.engine.Loader
import models.RawModel
import textures.TerrainTexture
import textures.TerrainTexturePack
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class Terrain(
        gridX: Float, gridZ: Float, loader: Loader,
        val texturePack: TerrainTexturePack,
        val blendMap: TerrainTexture, val heightMapFileDirectory: String
) {

    val x: Float
    val z: Float
    val model: RawModel

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
                vertices[vertexPointer * 3 + 1] = getHeight(j, i, image)
                vertices[vertexPointer * 3 + 2] = i.toFloat() / (vertexCount.toFloat() - 1) * SIZE
                normals[vertexPointer * 3 + 0] = 0F
                normals[vertexPointer * 3 + 1] = 1F
                normals[vertexPointer * 3 + 2] = 0F
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

    fun getHeight(x: Int, z: Int, image: BufferedImage): Float {
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
}
