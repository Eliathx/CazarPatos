package com.velasco.eliath.cazarpatos


data class Player(var username: String, var huntedDucks: Int) {
    constructor() : this("", 0)
}
