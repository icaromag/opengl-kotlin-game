package core.engine

import org.lwjgl.Sys
import org.lwjgl.opengl.*

object DisplayManager {

    private val WIDTH = 1280
    private val HEIGHT = 720
    private val FPS_CAP = 120
    private var lastFrameTime: Long = 0
    private var delta: Float = 0F

    fun create(displayTitle: String) {
        val contextAttributes = ContextAttribs(3, 2)
                .withForwardCompatible(true).withProfileCore(true)
        Display.setDisplayMode(DisplayMode(WIDTH, HEIGHT))
        Display.create(PixelFormat(), contextAttributes)
        Display.setTitle(displayTitle)
        GL11.glViewport(0, 0, WIDTH, HEIGHT)
        lastFrameTime = getCurrentTime()
    }

    fun update() {
        Display.sync(FPS_CAP)
        Display.update()
        val currentFrameTime = getCurrentTime()
        delta = (currentFrameTime - lastFrameTime) / 1000F
        lastFrameTime = currentFrameTime
    }

    fun close() {
        Display.destroy()
    }

    fun getFrameTimeSeconds(): Float = delta

    private fun getCurrentTime(): Long = Sys.getTime() * 1000 / Sys.getTimerResolution()
}
