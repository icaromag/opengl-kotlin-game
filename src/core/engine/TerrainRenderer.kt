package core.engine

import entities.Entity
import models.TexturedModel
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import shaders.TerrainShader
import terrain.Terrain
import utils.Maths

class TerrainRenderer(private val shader: TerrainShader, private val projectionMatrix: Matrix4f) {
    init {
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix)
        shader.stop()
    }

    fun render(terrains: List<Terrain>) {
        terrains.forEach {
            prepareTerrain(it)
            loadModelMatrix(it)
            GL11.glDrawElements(GL11.GL_TRIANGLES, it.model.vertexCount,
                    GL11.GL_UNSIGNED_INT, 0)
            unbindTexturedModel()
        }
    }

    fun prepareTerrain(terrain: Terrain) {
        val rawModel = terrain.model
        GL30.glBindVertexArray(rawModel.vaoID)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2) // obj normals
        val texture = terrain.texture
        shader.loadShineVariables(texture.shineDamper, texture.reflectivity)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureID)
    }

    fun unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)  // obj normals
        GL30.glBindVertexArray(0)
    }

    fun loadModelMatrix(terrain: Terrain) {
        // 0 on y means that our terrain is flat and grounded
        //   without any height [IM]
        val transformationMatrix = Maths.createTransformationMatrix(
                Vector3f(terrain.x, 0F, terrain.z), 0F, 0F, 0F, 1F)
        // load the transform matrix into the shader
        shader.loadTransformationMatrix(transformationMatrix)
    }
}
