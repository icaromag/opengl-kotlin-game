package shaders

import entities.Camera
import entities.Light
import org.lwjgl.util.vector.Matrix4f
import utils.Maths

class StaticShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private lateinit var transformationMatrixLocation: Any
    private lateinit var projectionMatrixLocation: Any
    private lateinit var viewMatrixLocation: Any
    private lateinit var lightPositionLocation: Any
    private lateinit var lightColorLocation: Any
    private lateinit var shineDamperLocation: Any
    private lateinit var reflectivityLocation: Any
    private lateinit var useFakeLightingLocation: Any

    companion object {
        private val VERTEX_FILE = "src/shaders/glsl/vertexShader"
        private val FRAGMENT_FILE = "src/shaders/glsl/fragmentShader"
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoordinates")
        super.bindAttribute(2, "normal")
    }

    override fun getAllUniformLocations() {
        transformationMatrixLocation = super.getUniformLocation("transformationMatrix")
        projectionMatrixLocation = super.getUniformLocation("projectionMatrix")
        viewMatrixLocation = super.getUniformLocation("viewMatrix")
        lightPositionLocation = super.getUniformLocation("lightPosition")
        lightColorLocation = super.getUniformLocation("lightColor")
        shineDamperLocation = super.getUniformLocation("shineDamper")
        reflectivityLocation = super.getUniformLocation("reflectivity")
        useFakeLightingLocation = super.getUniformLocation("useFakeLighting")
    }

    fun loadFakeLightingVariable(useFake: Boolean) {
        super.loadBoolean(useFakeLightingLocation as Int, useFake)
    }

    fun loadShineVariables(damper: Float, reflectivity: Float) {
        super.loadFloat(shineDamperLocation as Int, damper)
        super.loadFloat(reflectivityLocation as Int, reflectivity)
    }

    fun loadTransformationMatrix(matrix: Matrix4f) {
        super.loadMatrix(transformationMatrixLocation as Int, matrix)
    }

    fun loadLight(light: Light) {
        super.loadVector(lightPositionLocation as Int, light.position)
        super.loadVector(lightColorLocation as Int, light.color)
    }

    fun loadProjectionMatrix(matrix: Matrix4f) {
        super.loadMatrix(projectionMatrixLocation as Int, matrix)
    }

    fun loadViewMatrix(camera: Camera) {
        val viewMatrix = Maths.createViewMatrix(camera)
        super.loadMatrix(viewMatrixLocation as Int, viewMatrix)
    }
}
