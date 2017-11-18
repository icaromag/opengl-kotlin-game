package core.engine

import models.RawModel
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import org.newdawn.slick.opengl.PNGDecoder
import org.newdawn.slick.opengl.TextureLoader
import textures.TextureData
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Loader {
    private val vaos: ArrayList<Int> = arrayListOf()
    private val vbos: ArrayList<Int> = arrayListOf()
    private val textures: ArrayList<Int> = arrayListOf()

    fun loadToVAO(positions: FloatArray, textureCoordinates: FloatArray, normals: FloatArray, indices: IntArray): RawModel {
        val vaoId = createVAO()
        bindIndicesBuffer(indices)
        storeDataInAttributeList(0, 3, positions)
        storeDataInAttributeList(1, 2, textureCoordinates)
        storeDataInAttributeList(2, 3, normals)
        unbindVAO()
        return RawModel(vaoId, indices.size)
    }

    fun loadTexture(fileName: String): Int {
        val texture = TextureLoader.getTexture("PNG", FileInputStream("res/$fileName.png"))
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4F) // too negative decreases performance [IM]
        val textureID = texture.textureID
        textures.add(textureID)
        return textureID
    }

    fun clean() {
        vaos.forEach { GL30.glDeleteVertexArrays(it) }
        vbos.forEach { GL15.glDeleteBuffers(it) }
        textures.forEach { GL11.glDeleteTextures(it) }
    }

    private fun createVAO(): Int {
        // create an empty VAO
        val vaoID = GL30.glGenVertexArrays()
        vaos.add(vaoID)
        // bind it
        GL30.glBindVertexArray(vaoID)
        return vaoID
    }

    private fun storeDataInAttributeList(attributeNumber: Int, coordinateSize: Int, data: FloatArray) {
        val vboID = GL15.glGenBuffers()
        vbos.add(vboID)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
        val floatBuffer: FloatBuffer = storeDataInFloatBuffer(data)
        // static draw means that we will never edit the data again
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatBuffer, GL15.GL_STATIC_DRAW)
        // size:3 means x, y, z
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    private fun unbindVAO() {
        GL30.glBindVertexArray(0)
    }

    private fun bindIndicesBuffer(indices: IntArray) {
        val vboID = GL15.glGenBuffers()
        vbos.add(vboID)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID)
        val intBuffer = storeDataInIntBuffer(indices)
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL15.GL_STATIC_DRAW)
    }

    private fun storeDataInIntBuffer(data: IntArray): IntBuffer {
        val intBuffer = BufferUtils.createIntBuffer(data.size)
        intBuffer.put(data)
        intBuffer.flip() // ready to go
        return intBuffer
    }

    fun loadCubeMap(textureFile: MutableList<String>): Int {
        val textureID = GL11.glGenTextures()
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID)
        textureFile.forEach {
            val data = decodeTextureFile("res/$it.png")
            // the first param is which face of the cube we want to load
            //   the texture into [IM]
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + 1, 0, GL11.GL_RGBA, data.width, data.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.buffer)
        }
        // set mag and min filter to make the textures appear smooth like
        //   in the mipmap implementation [IM]
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        textures.add(textureID)
        return textureID
    }

    private fun storeDataInFloatBuffer(data: FloatArray): FloatBuffer {
        val floatBuffer: FloatBuffer = BufferUtils.createFloatBuffer(data.size)
        floatBuffer.put(data)
        // tells that the data insertion is finished. Ready to go!
        floatBuffer.flip()
        return floatBuffer
    }

    private fun decodeTextureFile(fileName: String): TextureData {
        val inputStream = FileInputStream(fileName)
        val decoder = PNGDecoder(inputStream)
        val width = decoder.width
        val height = decoder.height
        val buffer = ByteBuffer.allocateDirect(4 * width * height)
        decoder.decode(buffer, width * 4, PNGDecoder.RGBA)
        buffer.flip()
        inputStream.close()
        return TextureData(width, height, buffer)
    }
}
