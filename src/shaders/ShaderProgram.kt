package shaders

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import java.io.BufferedReader
import java.io.FileReader


abstract class ShaderProgram(vertexFile: String, fragmentFile: String) {
    private val programID: Int
    private val vertexShaderID: Int
    private val fragmentShaderID: Int

    companion object {
        // 16 because the matrices have 4x4 dimension
        private val matrixBuffer = BufferUtils.createFloatBuffer(16)
    }

    init {
        vertexShaderID = load(vertexFile, GL20.GL_VERTEX_SHADER)
        fragmentShaderID = load(fragmentFile, GL20.GL_FRAGMENT_SHADER)
        programID = GL20.glCreateProgram()
        GL20.glAttachShader(programID, vertexShaderID)
        GL20.glAttachShader(programID, fragmentShaderID)
        bindAttributes()
        GL20.glLinkProgram(programID)
        GL20.glValidateProgram(programID)
        getAllUniformLocations()
    }

    protected abstract fun getAllUniformLocations()

    protected fun getUniformLocation(uniformName: String): Int {
        return GL20.glGetUniformLocation(programID, uniformName)
    }

    protected fun loadFloat(location: Int, value: Float) {
        GL20.glUniform1f(location, value)
    }

    protected fun loadInt(location: Int, value: Int) {
        GL20.glUniform1i(location, value)
    }

    protected fun loadVector(location: Int, vector: Vector3f) {
        GL20.glUniform3f(location, vector.x, vector.y, vector.z)
    }

    protected fun loadBoolean(location: Int, value: Boolean) {
        val toLoad = if (value) 1F else 0F
        GL20.glUniform1f(location, toLoad)
    }

    protected fun loadMatrix(location: Int, matrix: Matrix4f) {
        matrix.store(matrixBuffer)
        matrixBuffer.flip() // flag as ready
        GL20.glUniformMatrix4(location, false, matrixBuffer)
    }

    fun start() {
        GL20.glUseProgram(programID)
    }

    fun stop() {
        GL20.glUseProgram(0)
    }

    fun cleanUp() {
        stop()
        GL20.glDetachShader(programID, vertexShaderID)
        GL20.glDetachShader(programID, fragmentShaderID)
        GL20.glDeleteShader(vertexShaderID)
        GL20.glDeleteShader(fragmentShaderID)
        GL20.glDeleteProgram(programID)
    }

    protected abstract fun bindAttributes()

    protected fun bindAttribute(attribute: Int, variableName: String) {
        GL20.glBindAttribLocation(programID, attribute, variableName)
    }

    private fun load(file: String, type: Int): Int {
        val reader = BufferedReader(FileReader(file))
        val shaderSource = reader.use(BufferedReader::readText)
        reader.close()

        val shaderID = GL20.glCreateShader(type)
        GL20.glShaderSource(shaderID, shaderSource)
        GL20.glCompileShader(shaderID)
        when (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS)) {
            GL11.GL_FALSE -> {
                println(GL20.glGetShaderInfoLog(shaderID, 500))
                System.err.println("Could not compile shader!")
                System.exit(-1)
            }
        }
        return shaderID
    }
}
