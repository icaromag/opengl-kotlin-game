package core.engine

import entities.Entity
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import shaders.StaticShader
import utils.Maths

class Renderer {
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
}
