package com.mattmx.ktgui.scheduling

import com.mattmx.ktgui.GuiManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * Forces code to run in an async context.
 *
 * @param block to run
 */
fun <T> T.forceAsync(block: () -> Unit) {
    if (!isAsync()) async { block() }
    else block()
}

/**
 * Forces code to run on the main thread.
 *
 * @param block to run
 */
fun <T> T.forceMainThread(block: () -> Unit) {
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
fun <T> T.sync(task: BukkitTask.() -> Unit): BukkitTask {
    var delayedInit: BukkitTask? = null
    delayedInit = Bukkit.getScheduler().runTask(GuiManager.getPlugin(this!!::class.java)) { -> task(delayedInit!!) }
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
fun <T> T.syncRepeat(period: Long, delay: Long = 0, task: BukkitTask.() -> Unit): BukkitTask {
    var delayedInit: BukkitTask? = null
    delayedInit = Bukkit.getScheduler().runTaskTimer(GuiManager.getPlugin(this!!::class.java), {-> task(delayedInit!!) }, delay, period)
    return delayedInit
}

/**
 * Schedule a task to run in the future.
 *
 * @author MattMX
 * @param delay the delay of when this will execute, must be +ve (in ticks)
 * @param task to execute
 */
fun <T> T.syncDelayed(delay: Long, task: BukkitTask.() -> Unit): BukkitTask {
    var delayedInit: BukkitTask? = null
    delayedInit = Bukkit.getScheduler().runTaskLater(GuiManager.getPlugin(this!!::class.java), {-> task(delayedInit!!) }, delay)
    return delayedInit
}

/**
 * Schedule an async function to run next tick.
 *
 * @author MattMX
 * @param task to execute asynchronously
 */
fun <T> T.async(task: BukkitTask.() -> Unit): BukkitTask {
    var delayedInit: BukkitTask? = null
    delayedInit = Bukkit.getScheduler().runTaskAsynchronously(GuiManager.getPlugin(this!!::class.java)) { -> task(delayedInit!!) }
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
fun <T> T.asyncRepeat(period: Long, delay: Long = 0, task: BukkitTask.() -> Unit): BukkitTask {
    var delayedInit: BukkitTask? = null
    delayedInit = Bukkit.getScheduler().runTaskTimerAsynchronously(GuiManager.getPlugin(this!!::class.java), {-> task(delayedInit!!) }, delay, period)
    return delayedInit
}

/**
 * Schedule a task to run in the future to execute asynchronously.
 *
 * @author MattMX
 * @param delay the delay of when this will execute, must be +ve (in ticks)
 * @param task to execute
 */
fun <T> T.asyncDelayed(delay: Long, task: BukkitTask.() -> Unit): BukkitTask {
    var delayedInit: BukkitTask? = null
    delayedInit = Bukkit.getScheduler().runTaskLaterAsynchronously(GuiManager.getPlugin(this!!::class.java), {-> task(delayedInit!!) }, delay)
    return delayedInit
}

/**
 * Returns a [CompletableFuture] with the type you specify.
 * We can use this to get values asynchronously.
 *
 * @author MattMX
 * @param block that returns our value
 */
fun <T, V> V.future(block: () -> T) : CompletableFuture<T> {
    val future = CompletableFuture<T>()
    async {
        val result = block()
        future.complete(result)
    }
    return future
}

/**
 * Similar to [future], will return a [Future] with the type you want.
 * This method is Synchronous.
 *
 * @author MattMX
 * @param block that returns our value
 */
inline fun <T, reified V> V.call(noinline block: () -> T) : Future<T> {
    return Bukkit.getScheduler().callSyncMethod(GuiManager.getPlugin(V::class.java), block)
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
inline val <reified T> Future<T>.await : T
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