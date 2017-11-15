package core.engine

import entities.Entity
import models.TexturedModel
import org.lwjgl.opengl.*
import shaders.StaticShader
import utils.Maths
import org.lwjgl.util.vector.Matrix4f


class Renderer(staticShader: StaticShader) {
    var shader: StaticShader = staticShader

    companion object {
        val FOV = 70F
        val NEAR_PLANE = 0.1F
        val FAR_PLANE = 1000F // how far you can see
    }

    init {
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        createProjectionMatrix()
        staticShader.start()
        staticShader.loadProjectionMatrix(createProjectionMatrix())
        staticShader.stop()
    }

    fun prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        // 1F rgba equals MAX(255F)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        GL11.glClearColor(0F, 0F, 0F, 1F)
    }

    fun render(entities: Map<TexturedModel, List<Entity>>) {
        entities.forEach {
            prepareTexturedModel(it.key)
            entities[it.key]?.forEach {
                prepareInstance(it)
                GL11.glDrawElements(GL11.GL_TRIANGLES, it.texturedModel.rawModel.vertexCount,
                        GL11.GL_UNSIGNED_INT, 0)
            }
            unbindTexturedModel()
        }
    }

    fun prepareTexturedModel(texturedModel: TexturedModel) {
        val rawModel = texturedModel.rawModel
        GL30.glBindVertexArray(rawModel.vaoID)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2) // obj normals
        val texture = texturedModel.texture
        shader.loadShineVariables(texture.shineDamper, texture.reflectivity)

        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.texture.textureID)
    }

    fun unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)  // obj normals
        GL30.glBindVertexArray(0)
    }

    fun prepareInstance(entity: Entity) {
        val transformationMatrix = Maths.createTransformationMatrix(
                entity.position, entity.rotX, entity.rotY, entity.rotZ, entity.scale)
        // load the transform matrix into the shader
        shader.loadTransformationMatrix(transformationMatrix)
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
