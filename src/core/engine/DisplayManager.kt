package core.engine

import org.lwjgl.opengl.*

object DisplayManager {
    private val WIDTH = 1280
    private val HEIGHT = 720
    private val FPS_CAP = 120

    fun create(displayTitle: String) {
        val contextAttributes = ContextAttribs(3, 2)
                .withForwardCompatible(true).withProfileCore(true)
        Display.setDisplayMode(DisplayMode(WIDTH, HEIGHT))
        Display.create(PixelFormat(), contextAttributes)
        Display.setTitle(displayTitle)
        GL11.glViewport(0, 0, WIDTH, HEIGHT)
    }

    fun update() {
        Display.sync(FPS_CAP)
        Display.update()
    }

    fun close() {
        Display.destroy()
    }
}
