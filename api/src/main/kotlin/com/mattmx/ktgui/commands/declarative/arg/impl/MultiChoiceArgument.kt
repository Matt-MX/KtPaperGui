package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.consumer.ArgumentConsumer

class MultiChoiceArgument<T : Any>(
    name: String,
    typeName: String,
    consumer: ArgumentConsumer
) : Argument<T>(name, typeName, consumer) {



}