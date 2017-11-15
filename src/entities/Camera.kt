package entities

import extensions.whenDown
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.util.vector.Vector3f

class Camera {
    private val keyboardDisplacementRate = .08F
    private val mouseWheelkeyboardDisplacementRate = .08F

    val position = Vector3f(0F, 0F, 0F)
    val pitch: Float = 0.0F
    val yaw: Float = 0.0F
    val roll: Float = 0.0F

    fun move() {
        val mouseWheelDisplacement = Mouse.getDWheel()
        Keyboard.KEY_W.whenDown { position.z += keyboardDisplacementRate }
        Keyboard.KEY_S.whenDown { position.z -= keyboardDisplacementRate }
        Keyboard.KEY_A.whenDown { position.x += keyboardDisplacementRate }
        Keyboard.KEY_D.whenDown { position.x -= keyboardDisplacementRate }
        Keyboard.KEY_UP.whenDown { position.y -= keyboardDisplacementRate }
        Keyboard.KEY_DOWN.whenDown { position.y += keyboardDisplacementRate }
        if (mouseWheelDisplacement > 0) {
            position.z += mouseWheelkeyboardDisplacementRate
        } else if (mouseWheelDisplacement < 0) {
            position.z -= mouseWheelkeyboardDisplacementRate
        }
    }
}
