package com.mattmx.ktgui.cooldown

import com.mattmx.ktgui.scheduling.asyncRepeat
import com.mattmx.ktgui.utils.minutes
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.file.YamlConstructor
import java.io.File
import java.time.Duration

open class YamlPersistentCoolDown<T : Any>(
    path: String,
    file: File,
    duration: Duration,
    val yaml: YamlConfiguration =
        if (file.exists()) YamlConfiguration.loadConfiguration(file)
        else YamlConfiguration()
) : PersistentCoolDown<T>(duration, object : Impl {
    override fun set(user: Any, lastExecuted: Long?) {
        yaml.set("$path.$user", lastExecuted)
    }

    override fun get(user: Any): Long? {
        return yaml.getString("$path.$user")?.toLongOrNull()
    }
}) {
    init {
        asyncRepeat(5.minutes) {
            yaml.save(file)
        }
    }
}