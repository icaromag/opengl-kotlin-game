package entities

import org.lwjgl.util.vector.Vector3f

class Light(val position: Vector3f, val color: Vector3f, val attenuation: Vector3f = Vector3f(1F, 0F, 0F))
