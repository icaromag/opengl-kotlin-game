package shaders

import entities.Camera
import org.lwjgl.util.vector.Matrix4f
import utils.Maths

class SkyboxShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private lateinit var projectionMatrixLocation: Any
    private lateinit var viewMatrixLocation: Any

    companion object {
        private val VERTEX_FILE = "src/skybox/skyboxVertexShader"
        private val FRAGMENT_FILE = "src/skybox/skyboxFragmentShader"
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
    }

    override fun getAllUniformLocations() {
        projectionMatrixLocation = super.getUniformLocation("projectionMatrix")
        viewMatrixLocation = super.getUniformLocation("viewMatrix")
    }

    fun loadProjectionMatrix(matrix: Matrix4f) {
        super.loadMatrix(projectionMatrixLocation as Int, matrix)
    }

    fun loadViewMatrix(camera: Camera) {
        val viewMatrix = Maths.createViewMatrix(camera)
        // modify the view matrix to follow the camera before load it [IM]
        viewMatrix.m30 = 0F
        viewMatrix.m31 = 0F
        viewMatrix.m32 = 0F
        super.loadMatrix(viewMatrixLocation as Int, viewMatrix)
    }
}
