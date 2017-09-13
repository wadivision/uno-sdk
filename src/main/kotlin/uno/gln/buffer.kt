package uno.gln

import glm_.L
import glm_.mat4x4.Mat4
import glm_.set
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL31
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.MemoryUtil.memAddress
import uno.gl.iBuf
import uno.gl.m4Buf
import java.nio.*
import kotlin.properties.Delegates

/**
 * Created by elect on 18/04/17.
 */

var bufferName: IntBuffer by Delegates.notNull()


inline fun glBufferData(target: Int, size: Int, usage: Int) = GL15.nglBufferData(target, size.L, NULL, usage)


// ----- Mat4 ----- TODO others

inline fun glBufferData(target: Int, mat: Mat4, usage: Int) = GL15.nglBufferData(target, Mat4.size.L, memAddress(mat to m4Buf), usage)
inline fun glBufferSubData(target: Int, offset: Int, mat: Mat4) = GL15.nglBufferSubData(target, offset.L, Mat4.size.L, memAddress(mat to m4Buf))
inline fun glBufferSubData(target: Int, mat: Mat4) = GL15.nglBufferSubData(target, 0L, Mat4.size.L, memAddress(mat to m4Buf))


inline fun glBindBuffer(target: Int) = GL15.glBindBuffer(target, 0)
inline fun glBindBuffer(target: Int, buffer: IntBuffer) = GL15.glBindBuffer(target, buffer[0])
inline fun glBindBuffer(target: Int, buffer: Enum<*>) = GL15.glBindBuffer(target, bufferName[buffer])


inline fun glBindUniformBufferRange(index: Int, buffer: IntBuffer, offset: Int, size: Int) = glBindBufferRange(GL31.GL_UNIFORM_BUFFER, index, buffer, offset, size)

inline fun glBindBufferRange(target: Int, index: Int, buffer: IntBuffer, offset: Int, size: Int) = GL30.glBindBufferRange(target, index, buffer[0], offset.L, size.L)
inline fun glBindBufferRange(target: Int, index: Int, buffer: Enum<*>, offset: Int, size: Int) = GL30.glBindBufferRange(target, index, bufferName[buffer], offset.L, size.L)

inline fun glBindBufferBase(target: Int, index: Int, buffer: Enum<*>) = GL30.glBindBufferBase(target, index, bufferName[buffer])
inline fun glBindBufferBase(target: Int, index: Int, buffer: IntBuffer) = GL30.glBindBufferBase(target, index, buffer[0])
inline fun glBindBufferBase(target: Int, index: Int) = GL30.glBindBufferBase(target, index, 0)


inline fun initArrayBuffer(buffer: IntBuffer, block: Buffer.() -> Unit) = buffer.set(0, initBuffer(GL15.GL_ARRAY_BUFFER, block))
inline fun initArrayBuffer(buffer: Enum<*>, block: Buffer.() -> Unit) = bufferName.set(buffer.ordinal, initBuffer(GL15.GL_ARRAY_BUFFER, block))
inline fun initElementBuffer(buffer: IntBuffer, block: Buffer.() -> Unit) = buffer.set(0, initBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, block))
inline fun initElementBuffer(buffer: Enum<*>, block: Buffer.() -> Unit) = bufferName.set(buffer.ordinal, initBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, block))
inline fun initUniformBuffer(buffer: IntBuffer, block: Buffer.() -> Unit) = buffer.set(0, initBuffer(GL31.GL_UNIFORM_BUFFER, block))
inline fun initUniformBuffer(buffer: Enum<*>, block: Buffer.() -> Unit) = bufferName.set(buffer.ordinal, initBuffer(GL31.GL_UNIFORM_BUFFER, block))
inline fun initBuffer(target: Int, block: Buffer.() -> Unit): Int {
    Buffer.target = target
    GL15.glGenBuffers(iBuf)
    val name = iBuf[0]
    Buffer.name = name // bind
    Buffer.block()
    GL15.glBindBuffer(target, 0)
    return name
}

inline fun initBuffers(buffers: IntBuffer, block: Buffers.() -> Unit) {
    GL15.glGenBuffers(buffers)
    Buffers.buffers = buffers
    Buffers.block()
}

inline fun initUniformBuffers(buffers: IntBuffer, block: Buffers.() -> Unit) {
    Buffers.target = GL31.GL_UNIFORM_BUFFER
    GL15.glGenBuffers(buffers)
    Buffers.buffers = buffers
    Buffers.block()
    GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)
}

inline fun withBuffer(target: Int, buffer: IntBuffer, block: Buffer.() -> Unit) = withBuffer(target, buffer[0], block)
inline fun withBuffer(target: Int, buffer: Enum<*>, block: Buffer.() -> Unit) = withBuffer(target, bufferName[buffer], block)
inline fun withBuffer(target: Int, buffer: Int, block: Buffer.() -> Unit) {
    Buffer.target = target
    Buffer.name = buffer   // bind
    Buffer.block()
    GL15.glBindBuffer(target, 0)
}

inline fun withArrayBuffer(buffer: Enum<*>, block: Buffer.() -> Unit) = withBuffer(GL15.GL_ARRAY_BUFFER, bufferName[buffer], block)
inline fun withArrayBuffer(buffer: IntBuffer, block: Buffer.() -> Unit) = withBuffer(GL15.GL_ARRAY_BUFFER, buffer[0], block)
inline fun withArrayBuffer(buffer: Int, block: Buffer.() -> Unit) = withBuffer(GL15.GL_ARRAY_BUFFER, buffer, block)
inline fun withElementBuffer(buffer: Enum<*>, block: Buffer.() -> Unit) = withBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, bufferName[buffer], block)
inline fun withElementBuffer(buffer: IntBuffer, block: Buffer.() -> Unit) = withBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer[0], block)
inline fun withElementBuffer(buffer: Int, block: Buffer.() -> Unit) = withBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, block)
inline fun withUniformBuffer(buffer: Enum<*>, block: Buffer.() -> Unit) = withBuffer(GL31.GL_UNIFORM_BUFFER, bufferName[buffer], block)
inline fun withUniformBuffer(buffer: IntBuffer, block: Buffer.() -> Unit) = withBuffer(GL31.GL_UNIFORM_BUFFER, buffer[0], block)
inline fun withUniformBuffer(buffer: Int, block: Buffer.() -> Unit) = withBuffer(GL31.GL_UNIFORM_BUFFER, buffer, block)

object Buffer {

    var target = 0
    var name = 0
        set(value) {
            GL15.glBindBuffer(target, value)
            field = value
        }

    inline fun data(data: ByteBuffer, usage: Int = 0) = GL15.nglBufferData(target, data.remaining().L, memAddress(data, data.position()), usage)
    inline fun data(data: ShortBuffer, usage: Int = 0) = GL15.nglBufferData(target, (data.remaining() shl 1).L, memAddress(data, data.position()), usage)
    inline fun data(data: IntBuffer, usage: Int = 0) = GL15.nglBufferData(target, (data.remaining() shl 2).L, memAddress(data, data.position()), usage)
    inline fun data(data: LongBuffer, usage: Int = 0) = GL15.nglBufferData(target, (data.remaining() shl 3).L, memAddress(data, data.position()), usage)
    inline fun data(data: FloatBuffer, usage: Int = 0) = GL15.nglBufferData(target, (data.remaining() shl 2).L, memAddress(data, data.position()), usage)
    inline fun data(data: DoubleBuffer, usage: Int = 0) = GL15.nglBufferData(target, (data.remaining() shl 3).L, memAddress(data, data.position()), usage)

    inline fun data(size: Int, usage: Int = 0) = GL15.nglBufferData(target, size.L, NULL, usage)

    inline fun subData(offset: Int, data: ByteBuffer) = GL15.nglBufferSubData(target, offset.L, data.remaining().L, memAddress(data, data.position()))
    inline fun subData(offset: Int, data: ShortBuffer) = GL15.nglBufferSubData(target, offset.L, (data.remaining() shl 1).L, memAddress(data, data.position()))
    inline fun subData(offset: Int, data: IntBuffer) = GL15.nglBufferSubData(target, offset.L, (data.remaining() shl 2).L, memAddress(data, data.position()))
    inline fun subData(offset: Int, data: LongBuffer) = GL15.nglBufferSubData(target, offset.L, (data.remaining() shl 3).L, memAddress(data, data.position()))
    inline fun subData(offset: Int, data: FloatBuffer) = GL15.nglBufferSubData(target, offset.L, (data.remaining() shl 2).L, memAddress(data, data.position()))
    inline fun subData(offset: Int, data: DoubleBuffer) = GL15.nglBufferSubData(target, offset.L, (data.remaining() shl 3).L, memAddress(data, data.position()))

    inline fun subData(data: ByteBuffer) = GL15.nglBufferSubData(target, 0L, data.remaining().L, memAddress(data, data.position()))
    inline fun subData(data: ShortBuffer) = GL15.nglBufferSubData(target, 0L, (data.remaining() shl 1).L, memAddress(data, data.position()))
    inline fun subData(data: IntBuffer) = GL15.nglBufferSubData(target, 0L, (data.remaining() shl 2).L, memAddress(data, data.position()))
    inline fun subData(data: LongBuffer) = GL15.nglBufferSubData(target, 0L, (data.remaining() shl 3).L, memAddress(data, data.position()))
    inline fun subData(data: FloatBuffer) = GL15.nglBufferSubData(target, 0L, (data.remaining() shl 2).L, memAddress(data, data.position()))
    inline fun subData(data: DoubleBuffer) = GL15.nglBufferSubData(target, 0L, (data.remaining() shl 3).L, memAddress(data, data.position()))


    // ----- Mat4 -----
    inline fun data(mat: Mat4, usage: Int = 0) = GL15.nglBufferData(target, Mat4.size.L, memAddress(mat to m4Buf, 0), usage)

    inline fun subData(offset: Int, mat: Mat4) = GL15.nglBufferSubData(target, offset.L, Mat4.size.L, memAddress(mat to m4Buf, 0))
    inline fun subData(mat: Mat4) = GL15.nglBufferSubData(target, 0L, Mat4.size.L, memAddress(mat to m4Buf, 0))


    inline fun bindRange(index: Int, offset: Int, size: Int) = GL30.glBindBufferRange(target, index, name, offset.L, size.L)

    inline fun bindBase(index: Int) = GL30.glBindBufferBase(target, index, 0)

    inline fun mapRange(length: Int, access: Int) = mapRange(0, length, access)
    inline fun mapRange(offset: Int, length: Int, access: Int): ByteBuffer = GL30.glMapBufferRange(target, offset.L, length.L, access)
}

object Buffers {

    lateinit var buffers: IntBuffer
    var target = 0
    var name = 0

    inline fun data(data: ByteBuffer, usage: Int) = GL15.nglBufferData(target, data.remaining().L, memAddress(data, data.position()), usage)
    inline fun data(data: ShortBuffer, usage: Int) = GL15.nglBufferData(target, (data.remaining() shl 1).L, memAddress(data, data.position()), usage)
    inline fun data(data: IntBuffer, usage: Int) = GL15.nglBufferData(target, (data.remaining() shl 2).L, memAddress(data, data.position()), usage)
    inline fun data(data: LongBuffer, usage: Int) = GL15.nglBufferData(target, (data.remaining() shl 3).L, memAddress(data, data.position()), usage)
    inline fun data(data: FloatBuffer, usage: Int) = GL15.nglBufferData(target, (data.remaining() shl 2).L, memAddress(data, data.position()), usage)
    inline fun data(data: DoubleBuffer, usage: Int) = GL15.nglBufferData(target, (data.remaining() shl 3).L, memAddress(data, data.position()), usage)

    inline fun data(size: Int, usage: Int) = GL15.nglBufferData(target, size.L, NULL, usage)

    inline fun subData(offset: Int, data: ByteBuffer) = GL15.nglBufferSubData(target, offset.L, data.remaining().L, memAddress(data, data.position()))
    inline fun subData(offset: Int, data: ShortBuffer) = GL15.nglBufferSubData(target, offset.L, (data.remaining() shl 1).L, memAddress(data, data.position()))
    inline fun subData(offset: Int, data: IntBuffer) = GL15.nglBufferSubData(target, offset.L, (data.remaining() shl 2).L, memAddress(data, data.position()))
    inline fun subData(offset: Int, data: LongBuffer) = GL15.nglBufferSubData(target, offset.L, (data.remaining() shl 3).L, memAddress(data, data.position()))
    inline fun subData(offset: Int, data: FloatBuffer) = GL15.nglBufferSubData(target, offset.L, (data.remaining() shl 2).L, memAddress(data, data.position()))
    inline fun subData(offset: Int, data: DoubleBuffer) = GL15.nglBufferSubData(target, offset.L, (data.remaining() shl 3).L, memAddress(data, data.position()))

    inline fun subData(data: ByteBuffer) = GL15.nglBufferSubData(target, 0L, data.remaining().L, memAddress(data, data.position()))
    inline fun subData(data: ShortBuffer) = GL15.nglBufferSubData(target, 0L, (data.remaining() shl 1).L, memAddress(data, data.position()))
    inline fun subData(data: IntBuffer) = GL15.nglBufferSubData(target, 0L, (data.remaining() shl 2).L, memAddress(data, data.position()))
    inline fun subData(data: LongBuffer) = GL15.nglBufferSubData(target, 0L, (data.remaining() shl 3).L, memAddress(data, data.position()))
    inline fun subData(data: FloatBuffer) = GL15.nglBufferSubData(target, 0L, (data.remaining() shl 2).L, memAddress(data, data.position()))
    inline fun subData(data: DoubleBuffer) = GL15.nglBufferSubData(target, 0L, (data.remaining() shl 3).L, memAddress(data, data.position()))


    // ----- Mat4 -----
    fun data(mat: Mat4, usage: Int) = GL15.nglBufferData(target, Mat4.size.L, memAddress(mat to m4Buf, 0), usage)

    inline fun subData(offset: Int, mat: Mat4) = GL15.nglBufferSubData(target, offset.L, Mat4.size.L, memAddress(mat to m4Buf, 0))
    inline fun subData(mat: Mat4) = GL15.nglBufferSubData(target, 0L, Mat4.size.L, memAddress(mat to m4Buf, 0))


    inline fun bindRange(index: Int, offset: Int, size: Int) = GL30.glBindBufferRange(target, index, name, offset.L, size.L)

    inline fun bindBase(index: Int) = GL30.glBindBufferBase(target, index, 0)

    inline fun mapRange(length: Int, access: Int) = mapRange(0, length, access)
    inline fun mapRange(offset: Int, length: Int, access: Int): ByteBuffer = GL30.glMapBufferRange(target, offset.L, length.L, access)


    inline fun at(bufferIndex: Enum<*>, block: Buffer.() -> Unit) = at(bufferIndex.ordinal, block)
    inline fun at(bufferIndex: Int, block: Buffer.() -> Unit) {
        Buffer.target = target
        Buffer.name = buffers[bufferIndex] // bind
        Buffer.block()
    }

    inline fun withArrayAt(bufferIndex: Enum<*>, block: Buffer.() -> Unit) = withArrayAt(bufferIndex.ordinal, block)
    inline fun withArrayAt(bufferIndex: Int, block: Buffer.() -> Unit) {
        Buffer.target = GL15.GL_ARRAY_BUFFER
        Buffer.name = buffers[bufferIndex] // bind
        Buffer.block()
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    //    inline fun withElement(block: Buffer.() -> Unit) = withElementAt(0, block)
    inline fun withElementAt(bufferIndex: Enum<*>, block: Buffer.() -> Unit) = withElementAt(bufferIndex.ordinal, block)

    inline fun withElementAt(bufferIndex: Int, block: Buffer.() -> Unit) {
        Buffer.target = GL15.GL_ELEMENT_ARRAY_BUFFER
        Buffer.name = buffers[bufferIndex] // bind
        Buffer.block()
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    //    inline fun withUniform(block: Buffer.() -> Unit) = withUniformAt(0, block)
    inline fun withUniformAt(bufferIndex: Enum<*>, block: Buffer.() -> Unit) = withUniformAt(bufferIndex.ordinal, block)

    inline fun withUniformAt(bufferIndex: Int, block: Buffer.() -> Unit) {
        Buffer.target = GL31.GL_UNIFORM_BUFFER
        Buffer.name = buffers[bufferIndex] // bind
        Buffer.block()
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)
    }
}


inline fun mappingUniformBufferRange(buffer: Enum<*>, length: Int, access: Int, block: MappedBuffer.() -> Unit) = mappingUniformBufferRange(bufferName[buffer], length, access, block)
inline fun mappingUniformBufferRange(buffer: IntBuffer, length: Int, access: Int, block: MappedBuffer.() -> Unit) = mappingUniformBufferRange(buffer[0], length, access, block)
inline fun mappingUniformBufferRange(buffer: Int, length: Int, access: Int, block: MappedBuffer.() -> Unit) {
    MappedBuffer.target = GL31.GL_UNIFORM_BUFFER
    MappedBuffer.name = buffer    // bind
    MappedBuffer.mapRange(length, access)
    MappedBuffer.block()
    GL15.glUnmapBuffer(GL31.GL_UNIFORM_BUFFER)
    GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)
}

object MappedBuffer {

    var target = 0
    var name = 0
        set(value) {
            GL15.glBindBuffer(target, value)
            field = value
        }

    fun mapRange(length: Int, access: Int) {
        buffer = GL30.glMapBufferRange(target, 0L, length.L, access)
    }

    lateinit var buffer: ByteBuffer

    var pointer: Any = NULL
        set(value) {
            if (value is Mat4) value to buffer
        }
}