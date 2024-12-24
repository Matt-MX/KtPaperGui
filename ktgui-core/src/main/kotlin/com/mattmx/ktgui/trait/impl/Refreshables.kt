package com.mattmx.ktgui.trait.impl

import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.trait.MultiTrait

class Refreshables(owner: GuiScreen<*, *>) : MultiTrait<GuiScreen<*, *>, RefreshableTrait>(owner)