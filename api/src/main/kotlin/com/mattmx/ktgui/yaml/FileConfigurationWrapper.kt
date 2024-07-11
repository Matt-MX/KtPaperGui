package com.mattmx.ktgui.yaml

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.time.Duration
import java.util.*

class FileConfigurationWrapper(
    val file: File,
    val autoSaveOptions: Options = Options()
) : YamlConfiguration() {
    var lastAutoSaved = System.currentTimeMillis()
    var isDirty: Boolean = false
        private set

    init {
        load(file)
    }

    fun markDirty() {
        isDirty = true
    }

    fun tickAutoSave() : Boolean {
        if (!shouldSave()) return false
        save()
        return true
    }

    fun shouldSave() = isDirty
        && autoSaveOptions.autoSaveTicks.isPresent
        && System.currentTimeMillis() - lastAutoSaved >= autoSaveOptions.autoSaveTicks.get().toMillis()

    fun save() = save(file)

    class Options {
        var autoSaveTicks = Optional.of(Duration.ofSeconds(30L))
    }
}