package shaders

import org.lwjgl.util.vector.Matrix4f

class StaticShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {

    private var location_transformationMatrix = -1

    companion object {
        private val VERTEX_FILE = "src/shaders/vertexShader"
        private val FRAGMENT_FILE = "src/shaders/fragmentShader"
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoords ")
    }

    override fun getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix")
    }

    fun loadTransformationMatrix(matrix: Matrix4f) {
        super.loadMatrix(location_transformationMatrix, matrix)
    }
}
