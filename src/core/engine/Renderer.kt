package core.engine

import entities.Entity
import org.lwjgl.opengl.*
import shaders.StaticShader
import utils.Maths
import org.lwjgl.util.vector.Matrix4f


class Renderer(staticShader: StaticShader) {
    companion object {
        val FOV = 70F
        val NEAR_PLANE = 0.1F
        val FAR_PLANE = 1000F // how far you can see
    }

    init {
        createProjectionMatrix()
        staticShader.start()
        staticShader.loadProjectionMatrix(createProjectionMatrix())
        staticShader.stop()
    }

    fun prepare() {
        // 1F rgba equals MAX(255F)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClearColor(1F, 0F, 0F, 1F)
    }

    fun render(entity: Entity, shader: StaticShader) {
        val texturedModel = entity.texturedModel
        val rawModel = texturedModel.rawModel
        GL30.glBindVertexArray(rawModel.vaoID)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)

        val transformationMatrix = Maths.createTransformationMatrix(
                entity.position, entity.rotX, entity.rotY, entity.rotZ, entity.scale)
        // load the transform matrix into the shader
        shader.loadTransformationMatrix(transformationMatrix)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.texture.textureID)
        GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.vertexCount, GL11.GL_UNSIGNED_INT, 0)
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL30.glBindVertexArray(0)
    }

    private fun createProjectionMatrix(): Matrix4f {
        val aspectRatio = Display.getWidth() / Display.getHeight()
        val yScale = (1f / Math.tan(Math.toRadians((FOV / 2f).toDouble())) * aspectRatio)
        val xScale = yScale / aspectRatio
        val frustumLength = FAR_PLANE - NEAR_PLANE
        val projectionMatrix = Matrix4f()
        projectionMatrix.run {
            m00 = xScale.toFloat()
            m11 = yScale.toFloat()
            m22 = -((FAR_PLANE + NEAR_PLANE) / frustumLength)
            m23 = -1F
            m32 = -(2 * NEAR_PLANE * FAR_PLANE / frustumLength)
            m33 = 0F
        }
        return projectionMatrix
    }
}
