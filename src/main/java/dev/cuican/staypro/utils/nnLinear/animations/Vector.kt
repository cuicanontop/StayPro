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

import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import dev.cuican.staypro.utils.nnLinear.animations.DeconstructiveVec3d

val Vec3d.deconstructive: DeconstructiveVec3d
    get() = DeconstructiveVec3d(this)

val Vec3i.deconstructive: DeconstructiveVec3d
    get() = DeconstructiveVec3d(this)