package terrain

import core.engine.Loader
import models.RawModel
import textures.ModelTexture


class Terrain(gridX: Float, gridZ: Float, loader: Loader, private val texture: ModelTexture) {

    private val x: Float
    private val z: Float
    private val model: RawModel

    companion object {
        private val SIZE = 800F
        // each side of the terrain contains 128 vertices [IM]
        private val VERTEX_COUNT = 128
    }

    init {
        x = gridX * SIZE
        z = gridZ * SIZE
        this.model = generateTerrain(loader)
    }

    // TODO change this mess to something more readable [IM]
    private fun generateTerrain(loader: Loader): RawModel {
        /**
         * You do not need to understand this piece of code. Just consider that it is
         *   generating a model, in this case, the terrain. Currently we are working
         *   with a simple flat terrain that should be evolved in a near future. [IM]
         */
        val count = VERTEX_COUNT * VERTEX_COUNT
        val vertices = FloatArray(count * 3)
        val normals = FloatArray(count * 3)
        val textureCoordinates = FloatArray(count * 2)
        val indices = IntArray(6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1))
        var vertexPointer = 0
        for (i in 0 until VERTEX_COUNT) {
            for (j in 0 until VERTEX_COUNT) {
                vertices[vertexPointer * 3] = j.toFloat() / (VERTEX_COUNT.toFloat() - 1) * SIZE
                vertices[vertexPointer * 3 + 1] = 0F
                vertices[vertexPointer * 3 + 2] = i.toFloat() / (VERTEX_COUNT.toFloat() - 1) * SIZE
                normals[vertexPointer * 3] = 0F
                normals[vertexPointer * 3 + 1] = 1F
                normals[vertexPointer * 3 + 2] = 0F
                textureCoordinates[vertexPointer * 2] = j.toFloat() / (VERTEX_COUNT.toFloat() - 1)
                textureCoordinates[vertexPointer * 2 + 1] = i.toFloat() / (VERTEX_COUNT.toFloat() - 1)
                vertexPointer++
            }
        }
        var pointer = 0
        for (gz in 0 until VERTEX_COUNT - 1) {
            for (gx in 0 until VERTEX_COUNT - 1) {
                val topLeft = gz * VERTEX_COUNT + gx
                val topRight = topLeft + 1
                val bottomLeft = (gz + 1) * VERTEX_COUNT + gx
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
}
