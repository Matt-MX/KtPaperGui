package com.mattmx.ktgui.yaml

import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun JavaPlugin.yaml() : ReadOnlyProperty<Any?, FileConfigurationWrapper> {
    var instance: FileConfigurationWrapper? = null
    return ReadOnlyProperty { ref: Any?, prop: KProperty<*> ->
        if (instance != null) return@ReadOnlyProperty instance!!

        instance = yaml(prop.name + ".yml")
        return@ReadOnlyProperty instance!!
    }
}

fun <T : Any> JavaPlugin.yaml(get: FileConfigurationWrapper.() -> T) : ReadOnlyProperty<Any?, T> {
    var yamlFile: FileConfigurationWrapper? = null
    var instance: T? = null

    // todo replace with ReadWriteProperty, list/map wrapper object
    return ReadOnlyProperty { ref: Any?, prop: KProperty<*> ->
        if (yamlFile != null)
            return@ReadOnlyProperty instance!!

        yamlFile = yaml(prop.name + ".yml")
        instance = get(yamlFile!!)
        return@ReadOnlyProperty instance!!
    }
}

fun JavaPlugin.yaml(path: String): FileConfigurationWrapper {
    val file = File("$dataFolder/path")

    if (!file.exists()) {
        kotlin.runCatching {
            saveResource(path, false)
        }.getOrNull() ?: file.createNewFile()
    }

    return FileConfigurationWrapper(file)
}

inline operator fun <reified T : Any> FileConfigurationWrapper.getValue(thisRef: Any?, property: KProperty<*>): T? {
    return getObject(pathFromPropertyName(property.name), T::class.java)
}

inline operator fun <reified T : Any> FileConfigurationWrapper.invoke(default: T) =
    ReadOnlyProperty { thisRef: Any?, prop ->
        val path = pathFromPropertyName(prop.name)
        val inConfig = getObject(path, T::class.java)
        return@ReadOnlyProperty if (inConfig == null) {
            set(path, default)
            default
        } else inConfig
    }

fun pathFromPropertyName(name: String) = name.replace("_", ".")