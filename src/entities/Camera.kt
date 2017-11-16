package entities

import extensions.whenDown
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.util.vector.Vector3f

class Camera(val position: Vector3f) {
    private val keyboardDisplacementRate = 1.3F
    private val mouseWheelDisplacementRate = .08F

    val pitch: Float = 45F
    var yaw: Float = 0F
    val roll: Float = 0F

    fun move() {
        val mouseWheelDisplacement = Mouse.getDWheel()
        Keyboard.KEY_W.whenDown { position.z += keyboardDisplacementRate }
        Keyboard.KEY_S.whenDown { position.z -= keyboardDisplacementRate }
        Keyboard.KEY_A.whenDown { position.x += keyboardDisplacementRate }
        Keyboard.KEY_D.whenDown { position.x -= keyboardDisplacementRate }
        Keyboard.KEY_UP.whenDown { position.y -= keyboardDisplacementRate }
        Keyboard.KEY_DOWN.whenDown { position.y += keyboardDisplacementRate }
        Keyboard.KEY_LEFT.whenDown { yaw -= keyboardDisplacementRate }
        Keyboard.KEY_RIGHT.whenDown { yaw += keyboardDisplacementRate }
        if (mouseWheelDisplacement > 0) {
            position.z += mouseWheelDisplacementRate
        } else if (mouseWheelDisplacement < 0) {
            position.z -= mouseWheelDisplacementRate
        }
    }
}
