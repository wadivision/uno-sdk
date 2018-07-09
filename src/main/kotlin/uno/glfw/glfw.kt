package uno.glfw

import ab.appBuffer
import glm_.buffer.adr
import glm_.vec2.Vec2i
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.glfw.GLFWVulkan
import org.lwjgl.system.APIUtil
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.system.Platform
import org.lwjgl.vulkan.VkInstance
import vkk.VK_CHECK_RESULT
import vkk.VkSurfaceKHR
import vkk.adr
import java.io.PrintStream
import java.util.function.BiPredicate
import uno.glfw.windowHint.Profile

/**
 * Created by elect on 22/04/17.
 */

object glfw {

    /** Short version of:
     *  glfw.init()
     *  glfw.windowHint {
     *      context.version = "3.2"
     *      windowHint.profile = "core"
     *  }
     *  + default error callback
     */
    @Throws(RuntimeException::class)
    fun init(version: String, profile: Profile = Profile.core, installDefaultErrorCallback: Boolean = true) {
        init(installDefaultErrorCallback)
        windowHint {
            context.version = version
            windowHint.profile = profile
        }
    }

    @Throws(RuntimeException::class)
    fun init(errorCallback: GLFWErrorCallbackT) {
        this.errorCallback = errorCallback
        init()
    }

    @Throws(RuntimeException::class)
    fun init(installDefaultErrorCallback: Boolean) {
        if (installDefaultErrorCallback)
            errorCallback = defaultErrorCallback
        init()
    }

    @Throws(RuntimeException::class)
    fun init() {

        if (!glfwInit())
            throw RuntimeException("Unable to initialize GLFW")

        // This window hint is required to use OpenGL 3.1+ on macOS
        if (Platform.get() == Platform.MACOSX)
            windowHint.forwardComp = true
    }

    fun terminate() {
        glfwTerminate()
        nErrorCallback.free()
    }

    val version: String
        get() = glfwGetVersionString()

    private val ERROR_CODES: MutableMap<Int, String> = APIUtil.apiClassTokens(BiPredicate { _, value -> value in 65537..131071 }, null, org.lwjgl.glfw.GLFW::class.java)
    val nErrorCallback: GLFWErrorCallback = GLFWErrorCallback.create { error, description -> errorCallback?.invoke(ERROR_CODES[error]!!, memUTF8(description)) }
    val defaultErrorCallback: GLFWErrorCallbackT = { error, description -> System.err.println("glfw $error error: $description") }

    var errorCallback: GLFWErrorCallbackT? = null
        set(value) {
            if(value != null) {
                field = value
                nglfwSetErrorCallback(nErrorCallback.adr)
            } else
                nglfwSetErrorCallback(NULL)
        }

    val vulkanSupported: Boolean
        get() = GLFWVulkan.glfwVulkanSupported()

    val time: Double
        get() = glfwGetTime()

    val primaryMonitor: GlfwMonitor
        get() = glfwGetPrimaryMonitor()

    /** videoMode of primaryMonitor */
    val videoMode: GLFWVidMode
        get() = glfwGetVideoMode(primaryMonitor)!!

    fun videoMode(monitor: GlfwMonitor): GLFWVidMode? = glfwGetVideoMode(monitor)

    val resolution: Vec2i
        get() = Vec2i(videoMode.width, videoMode.height)

    var swapInterval = 0
        set(value) = glfwSwapInterval(value)

    fun pollEvents() = glfwPollEvents()

    val requiredInstanceExtensions: ArrayList<String>
        get() {
            val pCount = appBuffer.intBuffer
            val ppNames = GLFWVulkan.nglfwGetRequiredInstanceExtensions(pCount.adr)
            val count = pCount[0]
            val pNames = MemoryUtil.memPointerBufferSafe(ppNames, count) ?: return arrayListOf()
            val res = ArrayList<String>(count)
            for (i in 0 until count)
                res += MemoryUtil.memASCII(pNames[i])
            return res
        }

    fun createWindowSurface(windowHandle: Long, instance: VkInstance): VkSurfaceKHR {
        val pSurface = appBuffer.long
        VK_CHECK_RESULT(GLFWVulkan.nglfwCreateWindowSurface(instance.adr, windowHandle, NULL, pSurface))
        return memGetLong(pSurface)
    }

    fun <T> windowHint(block: windowHint.() -> T) = windowHint.block()
}