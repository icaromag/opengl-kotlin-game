package entities

import org.lwjgl.input.Mouse
import org.lwjgl.util.vector.Vector3f

class Camera(private val player: Player, val position: Vector3f) {
    private var distanceFromPlayer = 50F
    private var angleAroundPlayer = 0F

    var pitch = 20F
    var yaw = 0F
    val roll = 0F

    fun move() {
        calculateZoom()
        calculatePitch()
        calculateAngleAroundPlayer()
        val horizontalDistance = calculateHorizontalDistance()
        val verticalDistance = calculateVerticalDistance()
        calculateCameraPosition(horizontalDistance, verticalDistance)
        yaw = 180 - (player.rotY + angleAroundPlayer)
    }

    private fun calculateCameraPosition(horizontalDistance: Float, verticalDistance: Float) {
        val theta = player.rotY + angleAroundPlayer
        val offsetX = horizontalDistance * Math.sin(Math.toRadians(theta.toDouble()))
        val offsetZ = horizontalDistance * Math.cos(Math.toRadians(theta.toDouble()))
        position.x = (player.position.x - offsetX).toFloat()
        position.z = (player.position.z - offsetZ).toFloat()
        position.y = player.position.y + verticalDistance
    }

    private fun calculateHorizontalDistance(): Float = distanceFromPlayer * Math.cos(Math.toRadians(pitch.toDouble())).toFloat()

    private fun calculateVerticalDistance(): Float = distanceFromPlayer * Math.sin(Math.toRadians(pitch.toDouble())).toFloat()

    private fun calculateZoom() {
        val zoomLevel = Mouse.getDWheel() * 0.1F
        distanceFromPlayer -= zoomLevel
    }

    private fun calculatePitch() {
        if (Mouse.isButtonDown(1)) {
            val pitchChange = Mouse.getDY() * 0.1F
            pitch -= pitchChange
        }
    }

    private fun calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(0)) {
            val angleChange = Mouse.getDX() * 0.3F
            angleAroundPlayer -= angleChange
        }
    }
}
