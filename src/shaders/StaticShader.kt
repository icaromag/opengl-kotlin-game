package shaders

import entities.Camera
import entities.Light
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import utils.Maths

class StaticShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private lateinit var transformationMatrixLocation: Any
    private lateinit var projectionMatrixLocation: Any
    private lateinit var viewMatrixLocation: Any
    private lateinit var lightPositionLocation: MutableList<Any>
    private lateinit var lightColorLocation: MutableList<Any>
    private lateinit var attenuationLocation: MutableList<Any>
    private lateinit var shineDamperLocation: Any
    private lateinit var reflectivityLocation: Any
    private lateinit var useFakeLightingLocation: Any
    private lateinit var skyColorLocation: Any
    // texture atlases [IM]
    private lateinit var numberOfRowsLocation: Any
    private lateinit var offsetLocation: Any


    companion object {
        // this val is hardcoded to match the vertex and fragment shaders [IM]
        private val MAX_LIGHTS = 4
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
        shineDamperLocation = super.getUniformLocation("shineDamper")
        reflectivityLocation = super.getUniformLocation("reflectivity")
        useFakeLightingLocation = super.getUniformLocation("useFakeLighting")
        skyColorLocation = super.getUniformLocation("skyColor")
        numberOfRowsLocation = super.getUniformLocation("numberOfRows")
        offsetLocation = super.getUniformLocation("offset")

        // multiple lights [IM]
        lightPositionLocation = MutableList(MAX_LIGHTS, {})
        lightColorLocation = MutableList(MAX_LIGHTS, {})
        attenuationLocation = MutableList(MAX_LIGHTS, {})
        for (i in 0 until MAX_LIGHTS) {
            lightPositionLocation[i] = super.getUniformLocation("lightPosition[$i]")
            lightColorLocation[i] = super.getUniformLocation("lightColor[$i]")
            attenuationLocation[i] = super.getUniformLocation("attenuation[$i]")
        }
    }

    fun loadNumberOfRows(numberOfRows: Int) {
        super.loadFloat(numberOfRowsLocation as Int, numberOfRows.toFloat())
    }

    fun loadOffset(x: Float, y: Float) {
        super.load2DVector(offsetLocation as Int, Vector2f(x, y))
    }

    fun loadSkyColor(r: Float, g: Float, b: Float) {
        super.loadVector(skyColorLocation as Int, Vector3f(r, g, b))
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

    fun loadLights(lights: MutableList<Light>) {
        for (i in 0 until MAX_LIGHTS) {
            if (i < lights.size) {
                super.loadVector(lightPositionLocation[i] as Int, lights[i].position)
                super.loadVector(lightColorLocation[i] as Int, lights[i].color)
                super.loadVector(attenuationLocation[i] as Int, lights[i].attenuation)
            } else {
                super.loadVector(lightPositionLocation[i] as Int, Vector3f(0F, 0F, 0F))
                super.loadVector(lightColorLocation[i] as Int, Vector3f(0F, 0F, 0F))
                super.loadVector(attenuationLocation[i] as Int, Vector3f(1F, 0F, 0F))
            }
        }
    }

    fun loadProjectionMatrix(matrix: Matrix4f) {
        super.loadMatrix(projectionMatrixLocation as Int, matrix)
    }

    fun loadViewMatrix(camera: Camera) {
        val viewMatrix = Maths.createViewMatrix(camera)
        super.loadMatrix(viewMatrixLocation as Int, viewMatrix)
    }
}
