package textures

class ModelTexture(val textureID: Int) {
    var hasTransparency = false
    var useFakeLighting = false
    var shineDamper = 1F
    var reflectivity = 0F
    var numberOfRows = 1
}
