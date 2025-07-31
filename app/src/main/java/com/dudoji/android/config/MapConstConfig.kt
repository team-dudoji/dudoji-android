package com.dudoji.android.config

import android.graphics.Color

// location diff threshold for update
const val LOCATION_UPDATE_THRESHOLD = 10 // meter
const val GRADIENT_RADIUS_RATE = 0.8f // rate of gradient radius to tile size

const val PIN_UPDATE_THRESHOLD = 10.0 // meter

const val LANDMARK_PIN_RADIUS = 3000 // meter

const val REVEAL_CIRCLE_RADIUS_BY_WALK = 100.0 // meter

// for swapping Tile overlay
const val TILE_OVERLAY_LOADING_TIME = 700L // ms

// for MapSection's Basic
const val BASIC_ZOOM_LEVEL = 15
const val DEFAULT_ZOOM_LEVEL = 17f

// Google Map Config
const val MIN_ZOOM = 10f
const val MAX_ZOOM = 20f

const val FOG_COLOR = Color.LTGRAY
const val FOG_PARTICLE_SIZE = 1000
const val FOG_PARTICLE_SPACING = 100
const val FOG_INVALIDATION_INTERVAL = 200 // ms

const val SPEED_THRESHOLD = 0.5f
