<h1 align="center">Paper Command Dsl</h1>

---

This is a **Wrapper** for the [Paper Command API](https://docs.papermc.io/paper/dev/command-api/commands). It provides 
some syntactic sugar to help creating commands easier.

### Creating a command

```kt
val myCommand = command("hello-world") {
    runs<Player> {
        reply(!"Hello, ${sender.name}, welcome to my server!")
    }
}.register(plugin)

// Call this later to unregister
myCommand.unregister()

// And to destroy the command
CommandManager.dispose(myCommand)
```

> [!NOTE]
> You can unregister and register commands at runtime, but you may encounter bugs as
this is not directly supported by Paper and is a bit hacky.

### Arguments

```kt
val user by player()
val msg by greedyString()
val msgCommand = command("msg" / user / msg) {
    runs<Player> {
        user.first().sendMessage(!"${sender.name} -> You: ${msg()}")
        reply(!"You -> ${user.first().name}: ${msg()}")
    }
}.register(plugin)
```

Arguments for a command can be provided using a delegated method to **automatically
determine** the argument's name from the variable's name.

Specify the order of execution as a URL is structured.

### Custom arguments

If you simply want to map a `String` to a value `T`, use the delegate function
`mapped<T>(vararg pairs: Pair<String, T>)`.

```kt
val chat by mapped(
    "global" to ChatChannel.GLOBAL,
    "local" to ChatChannel.LOCAL,
    "party" to ChatChannel.PARTY
)
command("chat" / chat) {
    runs<Player> {
        ChatSettingsManager.setPlayerChannel(sender, chat())
    }
}.register(plugin)
```

If you wish to have **tooltips suggestions** or anything more custom you can create argument
types using `customArgument`.

Here is an example converted from the [Paper Documentation](https://docs.papermc.io/paper/dev/command-api/arguments#custom-types).

```kt
enum class IceCreamType {
    VANILLA,
    CHOCOLATE,
    STRAWBERRY,
    MINT,
    COOKIES
}

val iceCreamType = customArgument(StringArgumentType.word()) {
    convert { input ->
        kotlin.runCatching { IceCreamType.valueOf(input.uppercase()) }.getOrNull()
            ?: error("Invalid ice cream flavour $input!")
    }
    suggests { context, builder ->
        IceCreamType.entries.forEach { builder.suggest(it.name) }
        builder.buildFuture()
    }
}
```

### Flags and Options

You may want to have a large amount of optional variables in your command.
We can use our delegated arguments as options which the user can optionally
provide values for using a prefixed `--<optionName> [<value>]`.

```kt
val username by string()

val page by int(min = 0, max = 5)
val maxResults by int(min = 10, max = 1000)
val verbose by boolean()
val flags by options(page, maxResults, verbose)

commands += command("history" / username) {
    
    runs<Player> {
        reply(!"History (no flags provided)")
    }

    // This invokes a sub command (or optional argument)
    sub(flags) {
        runs<Player> {
            reply(!"History page = ${flags[page] ?: 1}, " +
                    "maxResults = ${flags[maxResults] ?: 10}, " +
                    "verbose = ${flags[verbose] ?: false}"
            )
        }
    }
}.register(this)
```

Flags will attempt to provide suggestions for available options and their underlying argument type's
suggestions.

This command could be invoked as:
- `/history MattMX --verbose` (Since it is a boolean no value is required)
- `/history MattMX --page 33`
- `/history MattMX --page 33 --verbose`
- `/history MattMX --page 33 --verbose --maxResults 30`