package core.engine

import models.RawModel
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import java.nio.FloatBuffer

class Loader() {
    private val vaos: ArrayList<Int> = arrayListOf()
    private val vbos: ArrayList<Int> = arrayListOf()

    fun loadToVAO(positions: FloatArray): RawModel {
        val vaoId = createVAO()
        storeDataInAttributeList(0, positions)
        unbindVAO()
        return RawModel(vaoId, positions.size / 3)
    }

    fun clean() {
        for (vao in vaos)
            GL30.glDeleteVertexArrays(vao)
        for (vbo in vbos)
            GL30.glDeleteVertexArrays(vbo)
    }

    private fun createVAO(): Int {
        // create an empty VAO
        val vaoID = GL30.glGenVertexArrays()
        vaos.add(vaoID)
        // bind it
        GL30.glBindVertexArray(vaoID)
        return vaoID
    }

    private fun storeDataInAttributeList(attributeNumber: Int, data: FloatArray) {
        val vboID = GL15.glGenBuffers()
        vbos.add(vboID)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
        val floatBuffer: FloatBuffer = storeDataInFloatBuffer(data)
        // static draw means that we will never edit the data again
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatBuffer, GL15.GL_STATIC_DRAW)
        // size:3 means x, y, z
        GL20.glVertexAttribPointer(attributeNumber, 3, GL11.GL_FLOAT, false, 0, 0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    private fun unbindVAO() {
        GL30.glBindVertexArray(0)
    }

    private fun storeDataInFloatBuffer(data: FloatArray): FloatBuffer {
        val floatBuffer: FloatBuffer = BufferUtils.createFloatBuffer(data.size)
        floatBuffer.put(data)
        // tells that the data insertion is finished. Ready to go!
        floatBuffer.flip()
        return floatBuffer
    }
}
