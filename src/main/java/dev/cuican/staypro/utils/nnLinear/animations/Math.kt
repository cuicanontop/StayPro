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
@file:Suppress("nothing_to_inline", "unused")

package dev.cuican.staypro.utils.nnLinear.animations

import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.floor

const val PI_FLOAT: Float = 3.1415926535897932384626f

const val FLOOR_DOUBLE_D: Double = 1_073_741_824.0
const val FLOOR_DOUBLE_I: Int = 1_073_741_824

const val FLOOR_FLOAT_F: Float = 4_194_304.0f
const val FLOOR_FLOAT_I: Int = 4_194_304

inline fun Double.floorToInt(): Int = floor(this).toInt()
inline fun Float.floorToInt(): Int = floor(this).toInt()

inline fun Double.ceilToInt(): Int = ceil(this).toInt()
inline fun Float.ceilToInt(): Int = ceil(this).toInt()

inline fun Double.fastFloor(): Int = (this + FLOOR_DOUBLE_D).toInt() - FLOOR_DOUBLE_I
inline fun Float.fastFloor(): Int = (this + FLOOR_FLOAT_F).toInt() - FLOOR_FLOAT_I

inline fun Double.fastCeil(): Int = FLOOR_DOUBLE_I - (FLOOR_DOUBLE_D - this).toInt()
inline fun Float.fastCeil(): Int = FLOOR_FLOAT_I - (FLOOR_FLOAT_F - this).toInt()

inline fun Float.toRadian(): Float = this / 180.0f * PI_FLOAT
inline fun Double.toRadian(): Double = this / 180.0 * PI

inline fun Float.toDegree(): Float = this * 180.0f / PI_FLOAT
inline fun Double.toDegree(): Double = this * 180.0 / PI

inline val Double.sq: Double get() = this * this
inline val Float.sq: Float get() = this * this
inline val Int.sq: Int get() = this * this

inline val Double.cubic: Double get() = this * this * this
inline val Float.cubic: Float get() = this * this * this
inline val Int.cubic: Int get() = this * this * this

inline val Double.quart: Double get() = this * this * this * this
inline val Float.quart: Float get() = this * this * this * this
inline val Int.quart: Int get() = this * this * this * this

inline val Double.quint: Double get() = this * this * this * this * this
inline val Float.quint: Float get() = this * this * this * this * this
inline val Int.quint: Int get() = this * this * this * this * this