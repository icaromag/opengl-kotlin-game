package extensions

import org.lwjgl.input.Keyboard

fun Int.whenDown(block: () -> Unit) {
    if (Keyboard.isKeyDown(this)) block.invoke()
}
