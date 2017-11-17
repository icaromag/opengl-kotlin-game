package core.engine

import entities.Entity
import models.TexturedModel
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.util.vector.Matrix4f
import shaders.StaticShader
import utils.Maths

class EntityRenderer(staticShader: StaticShader, projectionMatrix: Matrix4f) {
    private var shader: StaticShader = staticShader

    init {
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix)
        shader.stop()
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

    private fun prepareTexturedModel(texturedModel: TexturedModel) {
        val rawModel = texturedModel.rawModel
        GL30.glBindVertexArray(rawModel.vaoID)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2) // obj normals
        val texture = texturedModel.texture
        shader.loadNumberOfRows(texture.numberOfRows)
        if (texture.hasTransparency) {
            MasterRenderer.disableCulling()
        }
        shader.loadFakeLightingVariable(texture.useFakeLighting)
        shader.loadShineVariables(texture.shineDamper, texture.reflectivity)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.texture.textureID)
    }

    private fun unbindTexturedModel() {
        MasterRenderer.enableCulling()
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)  // obj normals
        GL30.glBindVertexArray(0)
    }

    private fun prepareInstance(entity: Entity) {
        val transformationMatrix = Maths.createTransformationMatrix(
                entity.position, entity.rotX, entity.rotY, entity.rotZ, entity.scale)
        // load the transform matrix into the shader
        shader.loadTransformationMatrix(transformationMatrix)
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset())
    }
}
