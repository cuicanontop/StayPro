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

import net.minecraft.util.math.BlockPos

class DeconstructiveBlockPos(x: Int, y: Int, z: Int) : BlockPos(x, y, z) {
    constructor(blockPos: BlockPos) : this(blockPos.x, blockPos.y, blockPos.z)

    operator fun component1() = x

    operator fun component2() = y

    operator fun component3() = z
}