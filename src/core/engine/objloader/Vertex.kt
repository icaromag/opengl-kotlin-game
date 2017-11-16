package core.engine.objloader

import org.lwjgl.util.vector.Vector3f

class Vertex(val index: Int, val position: Vector3f) {
    companion object {
        private val NO_INDEX = -1
    }

    var textureIndex = NO_INDEX
    var normalIndex = NO_INDEX
    var duplicateVertex: Vertex? = null
    val length: Float = position.length()

    val isSet: Boolean
        get() = textureIndex != NO_INDEX && normalIndex != NO_INDEX

    fun hasSameTextureAndNormal(textureIndexOther: Int, normalIndexOther: Int): Boolean {
        return textureIndexOther == textureIndex && normalIndexOther == normalIndex
    }
}
