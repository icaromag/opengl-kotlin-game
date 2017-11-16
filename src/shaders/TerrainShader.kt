package shaders

import entities.Camera
import entities.Light
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import utils.Maths

class TerrainShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private lateinit var transformationMatrixLocation: Any
    private lateinit var projectionMatrixLocation: Any
    private lateinit var viewMatrixLocation: Any
    private lateinit var lightPositionLocation: Any
    private lateinit var lightColorLocation: Any
    private lateinit var shineDamperLocation: Any
    private lateinit var reflectivityLocation: Any
    private lateinit var skyColorLocation: Any

    private lateinit var backgroundTextureLocation: Any
    private lateinit var rTextureLocation: Any
    private lateinit var gTextureLocation: Any
    private lateinit var bTextureLocation: Any
    private lateinit var blendMapLocation: Any


    companion object {
        private val VERTEX_FILE = "src/shaders/glsl/terrainVertexShader"
        private val FRAGMENT_FILE = "src/shaders/glsl/terrainFragmentShader"
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
        skyColorLocation = super.getUniformLocation("skyColor")

        backgroundTextureLocation = super.getUniformLocation("backgroundTexture")
        rTextureLocation = super.getUniformLocation("rTexture")
        gTextureLocation = super.getUniformLocation("gTexture")
        bTextureLocation = super.getUniformLocation("bTexture")
        blendMapLocation = super.getUniformLocation("blendMap")
    }

    fun connectTextureUnits() {
        super.loadInt(backgroundTextureLocation as Int, 0)
        super.loadInt(rTextureLocation as Int, 1)
        super.loadInt(gTextureLocation as Int, 2)
        super.loadInt(bTextureLocation as Int, 3)
        super.loadInt(blendMapLocation as Int, 4)
    }

    fun loadSkyColor(r: Float, g: Float, b: Float) {
        super.loadVector(skyColorLocation as Int, Vector3f(r, g, b))
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