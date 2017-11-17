package entities

import core.engine.DisplayManager
import models.TexturedModel
import org.lwjgl.input.Keyboard
import org.lwjgl.util.vector.Vector3f
import terrain.Terrain

class Player(texturedModel: TexturedModel, position: Vector3f, rotX: Float, rotY: Float, rotZ: Float, scale: Float) : Entity(texturedModel, position, rotX, rotY, rotZ, scale) {
    companion object {
        val RUN_SPEED = 20F
        val TURN_SPEED = 160F // degrees per second [IM]
        val GRAVITY = -50F
        val JUMP_POWER = 30F
    }

    private var currentSpeed = 0F
    private var currentTurnSpeed = 0F
    private var upwardsSpeed = 0F
    private var isInAir = false

    fun move(terrain: Terrain) {
        checkInputs()
        // the turn speed should behave by seconds [IM]
        super.increaseRotation(0F, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0F)
        // how much we moved from the last frame [IM]
        val distance = currentSpeed * DisplayManager.getFrameTimeSeconds()
        val dx = distance * Math.sin(Math.toRadians(super.rotY.toDouble()))
        val dz = distance * Math.cos(Math.toRadians(super.rotY.toDouble()))
        super.increasePosition(dx.toFloat(), 0F, dz.toFloat())
        upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds()
        super.increasePosition(0F, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0F)

        val terrainHeight = terrain.getHeightOfTerrain(super.position.x, super.position.z)
        if(super.position.y < terrainHeight) {
            upwardsSpeed = 0F
            isInAir = false
            super.position.y = terrainHeight
        }
    }

    private fun jump() {
        if (!isInAir) {
            upwardsSpeed = JUMP_POWER
            isInAir = true
        }
    }

    private fun checkInputs() {
        currentSpeed = when {
            Keyboard.isKeyDown(Keyboard.KEY_W) -> RUN_SPEED
            Keyboard.isKeyDown(Keyboard.KEY_S) -> -RUN_SPEED
            else -> 0F
        }

        currentTurnSpeed = when {
            Keyboard.isKeyDown(Keyboard.KEY_D) -> -TURN_SPEED
            Keyboard.isKeyDown(Keyboard.KEY_A) -> TURN_SPEED
            else -> 0F
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            jump()
        }
    }
}
