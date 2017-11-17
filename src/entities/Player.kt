package entities

import core.engine.DisplayManager
import extensions.whenDown
import models.TexturedModel
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.util.vector.Vector3f

class Player(texturedModel: TexturedModel, position: Vector3f, rotX: Float, rotY: Float, rotZ: Float, scale: Float) : Entity(texturedModel, position, rotX, rotY, rotZ, scale) {
    companion object {
        val RUN_SPEED = 20F
        val TURN_SPEED = 160F // degrees per second [IM]
    }

    private var currentSpeed = 0F
    private var currentTurnSpeed = 0F

    fun move() {
        checkInputs()
        // the turn speed should behave by seconds [IM]
        super.increaseRotation(0F, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0F)
        // how much we moved from the last frame [IM]
        val distance = currentSpeed * DisplayManager.getFrameTimeSeconds()
        val dx = distance * Math.sin(Math.toRadians(super.rotY.toDouble()))
        val dz = distance * Math.cos(Math.toRadians(super.rotY.toDouble()))
        super.increasePosition(dx.toFloat(), 0F, dz.toFloat())
    }

    private fun checkInputs() {
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            currentSpeed = RUN_SPEED
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            currentSpeed = -RUN_SPEED
        } else {
            currentSpeed = 0F
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            currentTurnSpeed = -TURN_SPEED
        } else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            currentTurnSpeed = TURN_SPEED
        } else {
            currentTurnSpeed = 0F
        }
    }
}

