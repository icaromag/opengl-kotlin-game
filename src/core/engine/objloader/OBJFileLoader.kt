package core.engine.objloader

import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*


object OBJFileLoader {
    private val RES_LOC = "res/"

    fun loadOBJ(objFileName: String): ModelData {
        var isr: FileReader? = null
        val objFile = File(RES_LOC + objFileName + ".obj")
        isr = FileReader(objFile)

        val reader = BufferedReader(isr!!)
        var line: String?
        val vertices = ArrayList<Vertex>()
        val textures = ArrayList<Vector2f>()
        val normals = ArrayList<Vector3f>()
        val indices = ArrayList<Int>()
        while (true) {
            line = reader.readLine()
            val currentLine = line.split(" ").toTypedArray()
            if (line!!.startsWith("v ")) {
                val vertex = Vector3f(currentLine[1].toFloat(), currentLine[2].toFloat(), currentLine[3].toFloat())
                val newVertex = Vertex(vertices.size, vertex)
                vertices.add(newVertex)
            } else if (line.startsWith("vt ")) {
                val texture = Vector2f(currentLine[1].toFloat(), currentLine[2].toFloat())
                textures.add(texture)
            } else if (line.startsWith("vn ")) {
                val normal = Vector3f(currentLine[1].toFloat(), currentLine[2].toFloat(), currentLine[3].toFloat())
                normals.add(normal)
            } else if (line.startsWith("f ")) {
                break
            }
        }
        reader.lines().forEach {
            if (it.startsWith("f ")) {
                val currentLine = it.split(" ").toTypedArray()

                val vertex1 = currentLine[1].split("/").toTypedArray()
                val vertex2 = currentLine[2].split("/").toTypedArray()
                val vertex3 = currentLine[3].split("/").toTypedArray()
                processVertex(vertex1, vertices, indices)
                processVertex(vertex2, vertices, indices)
                processVertex(vertex3, vertices, indices)
            }
        }
        removeUnusedVertices(vertices)
        val verticesArray = FloatArray(vertices.size * 3)
        val texturesArray = FloatArray(vertices.size * 2)
        val normalsArray = FloatArray(vertices.size * 3)
        val furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
                texturesArray, normalsArray)
        val indicesArray = convertIndicesListToArray(indices)
        return ModelData(verticesArray, texturesArray, normalsArray, indicesArray,
                furthest)
    }

    private fun processVertex(vertex: Array<String>, vertices: MutableList<Vertex>, indices: MutableList<Int>) {
        val index = Integer.parseInt(vertex[0]) - 1
        val currentVertex = vertices[index]
        val textureIndex = Integer.parseInt(vertex[1]) - 1
        val normalIndex = Integer.parseInt(vertex[2]) - 1
        if (!currentVertex.isSet) {
            currentVertex.textureIndex = textureIndex
            currentVertex.normalIndex = normalIndex
            indices.add(index)
        } else {
            dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
                    vertices)
        }
    }

    private fun convertIndicesListToArray(indices: List<Int>): IntArray {
        val indicesArray = IntArray(indices.size)
        for (i in indicesArray.indices) {
            indicesArray[i] = indices[i]
        }
        return indicesArray
    }

    private fun convertDataToArrays(vertices: List<Vertex>, textures: List<Vector2f>,
                                    normals: List<Vector3f>, verticesArray: FloatArray, texturesArray: FloatArray,
                                    normalsArray: FloatArray): Float {
        var furthestPoint = 0f
        for (i in vertices.indices) {
            val currentVertex = vertices[i]
            if (currentVertex.length > furthestPoint) {
                furthestPoint = currentVertex.length
            }
            val position = currentVertex.position
            val textureCoord = textures[currentVertex.textureIndex]
            val normalVector = normals[currentVertex.normalIndex]
            verticesArray[i * 3] = position.x
            verticesArray[i * 3 + 1] = position.y
            verticesArray[i * 3 + 2] = position.z
            texturesArray[i * 2] = textureCoord.x
            texturesArray[i * 2 + 1] = 1 - textureCoord.y
            normalsArray[i * 3] = normalVector.x
            normalsArray[i * 3 + 1] = normalVector.y
            normalsArray[i * 3 + 2] = normalVector.z
        }
        return furthestPoint
    }

    private fun dealWithAlreadyProcessedVertex(previousVertex: Vertex, newTextureIndex: Int,
                                               newNormalIndex: Int, indices: MutableList<Int>, vertices: MutableList<Vertex>) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.index)
        } else {
            val anotherVertex = previousVertex.duplicateVertex
            if (anotherVertex != null) {
                dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
                        indices, vertices)
            } else {
                val duplicateVertex = Vertex(vertices.size, previousVertex.position)
                duplicateVertex.textureIndex = newTextureIndex
                duplicateVertex.normalIndex = newNormalIndex
                previousVertex.duplicateVertex = duplicateVertex
                vertices.add(duplicateVertex)
                indices.add(duplicateVertex.index)
            }
        }
    }

    private fun removeUnusedVertices(vertices: List<Vertex>) {
        for (vertex in vertices) {
            if (!vertex.isSet) {
                vertex.textureIndex = 0
                vertex.normalIndex = 0
            }
        }
    }
}