package shaders

import entities.Camera
import org.lwjgl.util.vector.Matrix4f
import utils.Maths

class StaticShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private lateinit var location_transformationMatrix: Any
    private lateinit var location_projectionMatrix: Any
    private lateinit var location_viewMatrix: Any

    companion object {
        private val VERTEX_FILE = "src/shaders/vertexShader"
        private val FRAGMENT_FILE = "src/shaders/fragmentShader"
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoords")
    }

    override fun getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix")
        location_projectionMatrix = super.getUniformLocation("projectionMatrix")
        location_viewMatrix = super.getUniformLocation("viewMatrix")
    }

    fun loadTransformationMatrix(matrix: Matrix4f) {
        super.loadMatrix(location_transformationMatrix as Int, matrix)

    }

    fun loadProjectionMatrix(matrix: Matrix4f) {
        super.loadMatrix(location_projectionMatrix as Int, matrix)
    }

    fun loadViewMatrix(camera: Camera) {
        val viewMatrix = Maths.createViewMatrix(camera)
        super.loadMatrix(location_viewMatrix as Int, viewMatrix)
    }
}
