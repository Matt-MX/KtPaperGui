package com.mattmx.ktgui.command.arg

//import com.mojang.brigadier.arguments.ArgumentType
//import com.mojang.brigadier.context.CommandContext
//import com.mojang.brigadier.suggestion.Suggestions
//import com.mojang.brigadier.suggestion.SuggestionsBuilder
//import io.papermc.paper.command.brigadier.argument.CustomArgumentType
//import java.util.*
//import java.util.concurrent.CompletableFuture
//
//class CustomArgumentTypeBuilder<T : Any, N : Any>(
//    val base: ArgumentType<N>
//) : CustomArgumentType.Converted<T, N> {
//    lateinit var converter: (N) -> T
//    var suggests = Optional.empty<(CommandContext<*>, SuggestionsBuilder) -> CompletableFuture<Suggestions>>()
//    val examples = mutableListOf<String>()
//
//    override fun getNativeType(): ArgumentType<N> {
//        return base
//    }
//
//    override fun convert(nativeType: N): T {
//        return converter.invoke(nativeType)
//    }
//
//    fun convert(block: (N) -> T) = apply {
//        this.converter = block
//    }
//
//    override fun <S : Any> listSuggestions(
//        context: CommandContext<S>,
//        builder: SuggestionsBuilder
//    ): CompletableFuture<Suggestions> {
//        if (suggests.isPresent) {
//            return suggests.get().invoke(context, builder)
//        }
//
//        return super.listSuggestions(context, builder)
//    }
//
//    fun suggests(block: (context: CommandContext<*>, builder: SuggestionsBuilder) -> CompletableFuture<Suggestions>) = apply {
//        this.suggests = Optional.of(block)
//    }
//
//    override fun getExamples(): MutableCollection<String> {
//        return this.examples
//    }
//}
//
//fun <T : Any, N : Any> customArgument(base: ArgumentType<N>, block: CustomArgumentTypeBuilder<T, N>.() -> Unit) =
//    CustomArgumentTypeBuilder<T, N>(base).apply(block)
