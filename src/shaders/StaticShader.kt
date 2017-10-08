package shaders

class StaticShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    companion object {
        private val VERTEX_FILE = "src/shaders/vertexShader"
        private val FRAGMENT_FILE = "src/shaders/fragmentShader"
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoords ")
    }
}
