package com.mattmx.ktgui.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.mattmx.ktgui.button.GuiButton
import com.mattmx.ktgui.screen.GuiScreen

class KtGuiSerializer(
    block: (GsonBuilder.() -> Unit)? = null
) {
    val builder = GsonBuilder().setPrettyPrinting()
        get() {
            if (this::gson.isInitialized) {
                error("The gson serializer has already been created! You may only access the builder before initialization.")
            }

            return field
        }

    private lateinit var gson: Gson

    init {
        block?.invoke(builder)
    }

    inline fun <reified T : GuiScreen<*, *>> gui(json: String): T? {
        return runCatching { getGson().fromJson(json, T::class.java) }.getOrNull()
    }

    inline fun <reified T : GuiButton<*, *, *, *>> button(json: String): T? {
        return runCatching { getGson().fromJson(json, T::class.java) }.getOrNull()
    }

    fun createBuilder(): Gson {
        this.gson = builder.create()

        return gson
    }

    fun getGson(): Gson {
        if (!this::gson.isInitialized) {
            error("The Gson serializer is not initialized yet!")
        }

        return gson
    }

    companion object {
        fun <T : GuiScreen<*, *>> GsonBuilder.registerGuiScreenAdapter(
            type: Class<T>,
            adapter: TypeAdapter<T>
        ) {
            registerTypeAdapter(TypeToken.get(type).type, adapter)
        }

        fun <T : GuiButton<*, *, *, *>> GsonBuilder.registerGuiButtonAdapter(
            type: Class<T>,
            adapter: TypeAdapter<T>
        ) {
            registerTypeAdapter(TypeToken.get(type).type, adapter)
        }
    }
}