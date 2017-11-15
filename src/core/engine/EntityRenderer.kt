package core.engine

import entities.Entity
import models.TexturedModel
import org.lwjgl.opengl.*
import org.lwjgl.util.vector.Matrix4f
import shaders.StaticShader
import utils.Maths


class EntityRenderer(staticShader: StaticShader, projectionMatrix: Matrix4f) {
    var shader: StaticShader = staticShader


    init {
        staticShader.start()
        staticShader.loadProjectionMatrix(projectionMatrix)
        staticShader.stop()
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
        if(texture.hasTransparency) {
            MasterRenderer.enableCulling()
        }
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
}
