package shaders

import entities.Camera
import org.lwjgl.util.vector.Matrix4f
import utils.Maths

class StaticShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private lateinit var transformationMatrixLocation: Any
    private lateinit var projectionMatrixLocation: Any
    private lateinit var viewMatrixLocation: Any

    companion object {
        private val VERTEX_FILE = "src/shaders/glsl/vertexShader"
        private val FRAGMENT_FILE = "src/shaders/glsl/fragmentShader"
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoords")
    }

    override fun getAllUniformLocations() {
        transformationMatrixLocation = super.getUniformLocation("transformationMatrix")
        projectionMatrixLocation = super.getUniformLocation("projectionMatrix")
        viewMatrixLocation = super.getUniformLocation("viewMatrix")
    }

    fun loadTransformationMatrix(matrix: Matrix4f) {
        super.loadMatrix(transformationMatrixLocation as Int, matrix)
    }

    fun loadProjectionMatrix(matrix: Matrix4f) {
        super.loadMatrix(projectionMatrixLocation as Int, matrix)
    }

    fun loadViewMatrix(camera: Camera) {
        val viewMatrix = Maths.createViewMatrix(camera)
        super.loadMatrix(viewMatrixLocation as Int, viewMatrix)
    }
}
