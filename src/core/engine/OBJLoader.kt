package core.engine

import models.RawModel
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import java.io.BufferedReader
import java.io.FileReader
import java.util.*

object OBJLoader {

    fun loadObjModel(fileName: String, loader: Loader): RawModel {
        val textureArray: FloatArray
        val normalsArray: FloatArray
        val reader = BufferedReader(FileReader("res/$fileName.obj"))
        val vertices = ArrayList<Vector3f>()
        val textures = ArrayList<Vector2f>()
        val normals = ArrayList<Vector3f>()
        val indices = ArrayList<Int>()

        while (true) {
            val line = reader.readLine()
            val currentLine = line.split(" ").toTypedArray()

            if (line.startsWith("v ")) {
                vertices.add(Vector3f(currentLine[1].toFloat(), currentLine[2].toFloat(), currentLine[3].toFloat()))
            } else if (line.startsWith("vt ")) {
                textures.add(Vector2f(currentLine[1].toFloat(), currentLine[2].toFloat()))
            } else if (line.startsWith("vn ")) {
                normals.add(Vector3f(currentLine[1].toFloat(), currentLine[2].toFloat(), currentLine[3].toFloat()))
            } else if (line.startsWith("f ")) {
                textureArray = FloatArray(vertices.size * 2)
                normalsArray = FloatArray(vertices.size * 3)
                break
            }
        }

        reader.lines().forEach {
            if (it.startsWith("f ")) {
                val currentLine = it.split(" ").toTypedArray()

                val vertex1 = currentLine[1].split("/").toTypedArray()
                val vertex2 = currentLine[2].split("/").toTypedArray()
                val vertex3 = currentLine[3].split("/").toTypedArray()

                processVertex(vertex1, indices, textures, normals, textureArray, normalsArray)
                processVertex(vertex2, indices, textures, normals, textureArray, normalsArray)
                processVertex(vertex3, indices, textures, normals, textureArray, normalsArray)
            }
        }


        val verticesArray = FloatArray(vertices.size * 3)
        val indicesArray = IntArray(indices.size)

        var vertexPointer = 0
        vertices.forEach {
            verticesArray[vertexPointer++] = it.x
            verticesArray[vertexPointer++] = it.y
            verticesArray[vertexPointer++] = it.z
        }

        for (i in indices.indices) indicesArray[i] = indices[i]

        return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray)
    }

    private fun processVertex(vertexData: Array<String>, indices: MutableList<Int>, textures: List<Vector2f>,
                              normals: List<Vector3f>, textureArray: FloatArray, normalsArray: FloatArray) {
        val currentVertexPointer = vertexData[0].toInt() - 1
        indices.add(currentVertexPointer)
        var tempCurrentVertexPointer: Int

        val currentTex = textures[vertexData[1].toInt() - 1]
        tempCurrentVertexPointer = currentVertexPointer * 2
        textureArray[tempCurrentVertexPointer + 0] = currentTex.x
        textureArray[tempCurrentVertexPointer + 1] = 1 - currentTex.y

        val currentNorm = normals[vertexData[2].toInt() - 1]
        tempCurrentVertexPointer = currentVertexPointer * 3
        normalsArray[tempCurrentVertexPointer + 0] = currentNorm.x
        normalsArray[tempCurrentVertexPointer + 1] = currentNorm.y
        normalsArray[tempCurrentVertexPointer + 2] = currentNorm.z
    }
}

