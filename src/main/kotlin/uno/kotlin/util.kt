package uno.kotlin

import glm_.i
import kool.Adr
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GLCapabilities
import org.lwjgl.system.Pointer
import java.awt.event.KeyEvent
import java.io.File
import java.lang.Appendable
import java.nio.IntBuffer
import java.util.*
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

/**
 * Created by GBarbieri on 30.03.2017.
 */

infix fun <T> (() -> Any).shallThrow(exClass: Class<T>) {
    try {
        this()
    } catch (e: Throwable) {
        if (exClass.isInstance(e)) return
        else throw Error("Exception type different")
    }
    throw Error("No exception")
}

val String.uri get() = url.toURI()!!
val String.url get() = ClassLoader.getSystemResource(this)!!
val String.stream get() = ClassLoader.getSystemResourceAsStream(this)!!

val String.file get() = File(uri)

val Char.isPrintable: Boolean
    get() = with(Character.UnicodeBlock.of(this)) {
        (!Character.isISOControl(this@isPrintable)) &&
                this@isPrintable != KeyEvent.CHAR_UNDEFINED &&
                this != null &&
                this != Character.UnicodeBlock.SPECIALS
    }

fun Char.parseInt() = java.lang.Character.getNumericValue(this)

operator fun <K> HashSet<K>.plusAssign(element: K) {
    add(element)
}

operator fun <K> HashSet<K>.minusAssign(element: K) {
    remove(element)
}

infix operator fun Appendable.plusAssign(char: Char) {
    append(char)
}

infix operator fun Appendable.plusAssign(charSequence: CharSequence) {
    append(charSequence)
}

infix operator fun <T>StringBuilder.plusAssign(element: T) {
    append(element)
}

fun <T>treeSetOf() = java.util.TreeSet<T>()

fun <K, V>SortedMap<K, V>.getOrfirst(key: K): V? = get(key) ?: first
val <K, V>SortedMap<K, V>.first: V?  get() = get(firstKey())

operator fun <R> KMutableProperty0<R>.setValue(host: Any?, property: KProperty<*>, value: R) = set(value)
operator fun <R> KMutableProperty0<R>.getValue(host: Any?, property: KProperty<*>): R = get()

operator fun IntBuffer.plusAssign(bool: Boolean) = plusAssign(bool.i)
operator fun IntBuffer.plusAssign(i: Int) {
    put(i)
}

val version = "0.7.5"