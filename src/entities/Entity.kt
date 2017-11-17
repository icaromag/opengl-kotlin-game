package entities

import models.TexturedModel
import org.lwjgl.util.vector.Vector3f

/**
 * Should represent an instance of a textured model.
 * Contains the textured model, its position, scale and some
 *  data that belongs to the model itself. [IM]
 */
open class Entity(
        val texturedModel: TexturedModel, val position: Vector3f,
        var rotX: Float, var rotY: Float, var rotZ: Float, val scale: Float) {

    private var textureIndex = 0

    constructor(
            texturedModel: TexturedModel, position: Vector3f,
            rotX: Float, rotY: Float, rotZ: Float, scale: Float, textureIndex: Int)
            : this(texturedModel, position, rotX, rotY, rotZ, scale) {
        this.textureIndex = textureIndex
    }

    fun getTextureXOffset() : Float {
        val column : Int = textureIndex % texturedModel.texture.numberOfRows
        return column.toFloat() / texturedModel.texture.numberOfRows.toFloat()
    }

    fun getTextureYOffset() : Float {
        val row : Int = textureIndex / texturedModel.texture.numberOfRows
        return row.toFloat() / texturedModel.texture.numberOfRows.toFloat()
    }

    fun increasePosition(dx: Float, dy: Float, dz: Float) =
            position.apply {
                x += dx
                y += dy
                z += dz
            }

    fun increaseRotation(dx: Float, dy: Float, dz: Float) {
        rotX += dx
        rotY += dy
        rotZ += dz
    }
}
