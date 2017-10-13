package entities

import org.lwjgl.input.Keyboard
import org.lwjgl.util.vector.Vector3f

class Camera {
    private val displacementRate = 0.02F

    val position = Vector3f(0F, 0F, 0F)
    val pitch: Float = 0.0F
    val yaw: Float = 0.0F
    val roll: Float = 0.0F

    fun move() {
        Keyboard.KEY_W.whenDown { position.z += displacementRate }
        Keyboard.KEY_S.whenDown { position.z -= displacementRate }
        Keyboard.KEY_A.whenDown { position.x += displacementRate }
        Keyboard.KEY_D.whenDown { position.x -= displacementRate }
    }
}

fun Int.whenDown(block: () -> Unit) {
    if (Keyboard.isKeyDown(this)) block.invoke()
}
