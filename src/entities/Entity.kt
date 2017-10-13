package entities

import models.TexturedModel
import org.lwjgl.util.vector.Vector3f

/**
 * Should represent an instance of a textured model.
 * Contains the textured model, its position, scale and some
 *  data that belongs to the model itself.
 */
class Entity(val texturedModel: TexturedModel, val position: Vector3f,
             var rotX: Float, var rotY: Float, var rotZ: Float, val scale: Float) {

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
