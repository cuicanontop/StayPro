/*
 * Copyright (c) 2020-2022
 * Defenders(Surmount) Utility Mode Developing Team. All rights reserved.
 * ---------------------------------------------------------------
 * Copyright (c) 2021-2022
 * CakeSlayer Reversing Team. All rights reserved.
 * ---------------------------------------------------------------
 * Copyright (c) 2021-2022
 * Nilquadium Coding Team. All rights reserved.
 * ---------------------------------------------------------------
 * Copyright (c) 2021-2022
 * HorizonLN Reversing Team. All rights reserved.
 * ---------------------------------------------------------------
 * NullHack based on Kotlin&Java, developing by Nilquadium Team
 * Authors: SagiriXiguajerry, PyWong
 */

package dev.cuican.staypro.utils.nnLinear.animations

import dev.cuican.staypro.utils.Wrapper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.opengl.GL11


class ScaleEasing @JvmOverloads constructor(easing: Easing = Easing.LINEAR, animationTime: Float = 700F) {
    var easing = easing
        set(value) {
            field = value
            animation = AnimationFlag(value, animationTime)
        }
    var animationTime = animationTime
        set(value) {
            field = value
            animation = AnimationFlag(easing, value)
        }
    private var animation: AnimationFlag

    init {
        this.animation = AnimationFlag(easing, animationTime)
        reset()
    }

    @JvmOverloads
    fun start(mcIn: Minecraft = Wrapper.mc){
        start(ScaledResolution(mcIn))
    }

    fun start(sr: ScaledResolution) {
        GL11.glPushMatrix()
        val percent = animation.getAndUpdate(1F)
        GL11.glTranslated(sr.scaledWidth / 2.0, sr.scaledHeight / 2.0, 0.0)
        GL11.glScalef(percent, percent, 0F)
        GL11.glTranslated(-sr.scaledWidth / 2.0, -sr.scaledHeight / 2.0, 0.0)
    }

    fun end() {
        GL11.glPopMatrix()
    }

    fun reset() {
        animation.forceUpdate(0F, 0F)
    }
}