package com.mattmx.ktgui.scheduling

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.jvm.isAccessible

/**
 * Dummy object to store the plugin instance in, so we don't have to provide it for
 * every single method call.
 *
 * You must initialize this before calling any other methods
 *
 * @author MattMX
 */
object Scheduling {
    lateinit var plugin: JavaPlugin
}

val <T> KFunction<T>.getAsync: CompletableFuture<T>
    get() = future {
        async {
            isAccessible = true
            val result = runCatching(::call)
            if (result.isSuccess) complete(result.getOrThrow()) else completeExceptionally(result.exceptionOrNull())
        }
    }

infix fun <T, O> KFunction1<T, O>.getAsync(arg: T) = future {
    async {
        isAccessible = true
        val result = runCatching { call(arg) }
        if (result.isSuccess) complete(result.getOrThrow()) else completeExceptionally(result.exceptionOrNull())
    }
}

fun <T, U, O> KFunction2<T, U, O>.getAsync(arg: T, arg1: U) = future {
    async {
        isAccessible = true
        val result = runCatching { call(arg, arg1) }
        if (result.isSuccess) complete(result.getOrThrow()) else completeExceptionally(result.exceptionOrNull())
    }
}

val <T> KFunction<T>.syncTask: BukkitTask
    get() = sync {
        isAccessible = true
        call()
    }

/**
 * Forces code to run in an async context.
 *
 * @param block to run
 */
fun forceAsync(block: () -> Unit) {
    if (!isAsync()) async { block() }
    else block()
}

/**
 * Forces code to run on the main thread.
 *
 * @param block to run
 */
fun forceMainThread(block: () -> Unit) {
    if (isAsync()) sync { block() }
    else block()
}

/**
 * Returns true if the context of the code is async.
 *
 * @return if code is running async
 */
fun isAsync() = !Bukkit.isPrimaryThread()

/**
 * Schedules a method to run synchronously (blocking) on the main thread.
 * This will execute next tick.
 *
 * @author MattMX
 * @param task to execute
 */
fun sync(task: BukkitTask.() -> Unit): BukkitTask {
    var delayedInit: BukkitTask? = null
    delayedInit = Bukkit.getScheduler().runTask(Scheduling.plugin) { -> task(delayedInit!!) }
    return delayedInit
}

/**
 * Schedules a repeating task to execute the next tick. You can specify the initial delay, period and
 * max iterations. Keep in mind the iteration is not actually accepted in the code block.
 *
 * @author MattMX
 * @param period between each execution
 * @param delay initial delay before we start the loop
 * @param maxIterations how many times we want to do this before cancelling automatically
 * @param task to execute
 */
fun syncRepeat(period: Long, delay: Long = 0, task: BukkitTask.() -> Unit): BukkitTask {
    var delayedInit: BukkitTask? = null
    delayedInit = Bukkit.getScheduler().runTaskTimer(Scheduling.plugin, { -> task(delayedInit!!) }, delay, period)
    return delayedInit
}

/**
 * Schedule a task to run in the future.
 *
 * @author MattMX
 * @param delay the delay of when this will execute, must be +ve (in ticks)
 * @param task to execute
 */
fun syncDelayed(delay: Long, task: BukkitTask.() -> Unit): BukkitTask {
    var delayedInit: BukkitTask? = null
    delayedInit = Bukkit.getScheduler().runTaskLater(Scheduling.plugin, { -> task(delayedInit!!) }, delay)
    return delayedInit
}

/**
 * Schedule an async function to run next tick.
 *
 * @author MattMX
 * @param task to execute asynchronously
 */
fun async(task: BukkitTask.() -> Unit): BukkitTask {
    var delayedInit: BukkitTask? = null
    delayedInit = Bukkit.getScheduler().runTaskAsynchronously(Scheduling.plugin) { -> task(delayedInit!!) }
    return delayedInit
}

/**
 * Schedules a repeating task to execute asynchronously the next tick. You can specify the initial delay, period and
 * max iterations. Keep in mind the iteration is not actually accepted in the code block.
 *
 * @author MattMX
 * @param period between each execution
 * @param delay initial delay before we start the loop
 * @param maxIterations how many times we want to do this before cancelling automatically
 * @param task to execute
 */
fun asyncRepeat(period: Long, delay: Long = 0, task: BukkitTask.() -> Unit): BukkitTask {
    var delayedInit: BukkitTask? = null
    delayedInit =
        Bukkit.getScheduler().runTaskTimerAsynchronously(Scheduling.plugin, { -> task(delayedInit!!) }, delay, period)
    return delayedInit
}

/**
 * Schedule a task to run in the future to execute asynchronously.
 *
 * @author MattMX
 * @param delay the delay of when this will execute, must be +ve (in ticks)
 * @param task to execute
 */
fun asyncDelayed(delay: Long, task: BukkitTask.() -> Unit): BukkitTask {
    var delayedInit: BukkitTask? = null
    delayedInit = Bukkit.getScheduler().runTaskLaterAsynchronously(Scheduling.plugin, { -> task(delayedInit!!) }, delay)
    return delayedInit
}

/**
 * Returns a [CompletableFuture] with the type you specify.
 * We can use this to get values asynchronously.
 *
 * @author MattMX
 * @param block that returns our value
 */
<<<<<<< HEAD
fun <T> future(block: () -> T): CompletableFuture<T> {
    val future = CompletableFuture<T>()
    async {
        val result = block()
        future.complete(result)
    }
    return future
}
=======
fun <T> future(block: CompletableFuture<T>.() -> Unit): CompletableFuture<T> =
    CompletableFuture<T>().apply(block)
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb

/**
 * Similar to [future], will return a [Future] with the type you want.
 * This method is Synchronous.
 *
 * @author MattMX
 * @param block that returns our value
 */
fun <T> call(block: () -> T): Future<T> {
    return Bukkit.getScheduler().callSyncMethod(Scheduling.plugin, block)
}

/**
 * Utility function that runs blocking to get a value from a future, with a default
 * timeout of 30 seconds.
 *
 * This is useful if you have a function that returns a [Future], like [future], since we can
 * call the function and then [await] it.
 *
 * @author MattMX
 */
inline val <reified T> Future<T>.await: T
    get() {
        return this.get(30, TimeUnit.SECONDS)
    }

/**
 * Smaller notation of the [await] function. This could be confusing since it uses the `!` operator so
 * keep that in mind to keep your code readable. Although it does look nicer in my opinion.
 *
 *      !foo() as apposed to foo().await
 *
 * @author MattMX
 */
inline operator fun <reified T> Future<T>.not() = await