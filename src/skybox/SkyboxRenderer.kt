package skybox

import core.engine.Loader
import entities.Camera
import models.RawModel
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.util.vector.Matrix4f
import shaders.SkyboxShader

class SkyboxRenderer(loader: Loader, val projectionMatrix: Matrix4f) {
    // change to make the skybox bigger or smaller [IM]
    private val SIZE = 500f

    private val VERTICES = floatArrayOf(
            -SIZE, SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            -SIZE, SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, SIZE
    )

    companion object {
        val TEXTURE_FILES = listOf(
                "skybox/right",
                "skybox/left",
                "skybox/top",
                "skybox/bottom",
                "skybox/back",
                "skybox/front"
        )
    }

    private val cube: RawModel
    private val texture: Int
    private val shader: SkyboxShader

    init {
        cube = loader.loadToVAO(VERTICES, 3)
        texture = loader.loadCubeMap(TEXTURE_FILES)
        shader = SkyboxShader()
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix)
        shader.stop()
    }

    fun render(camera: Camera) {
        shader.start()
        shader.loadViewMatrix(camera)
        GL30.glBindVertexArray(cube.vaoID)
        GL20.glEnableVertexAttribArray(0)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture)
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.vertexCount)
        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
        shader.stop()
    }
}
