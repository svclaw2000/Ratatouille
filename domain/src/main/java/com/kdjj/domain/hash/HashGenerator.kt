package com.kdjj.domain.hash

internal interface HashGenerator {

    fun hash(byteArray: ByteArray): String
}