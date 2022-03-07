package com.kdjj.domain.hash

import java.security.MessageDigest
import javax.inject.Inject
import kotlin.experimental.and

internal class SHA256HashGenerator @Inject constructor() : HashGenerator {

    private val digest = MessageDigest.getInstance("SHA-256")

    override fun hash(byteArray: ByteArray): String {
        return digest.digest(byteArray)
            .joinToString("") { byte ->
                (byte.and(0xff.toByte()) + 0x100)
                    .toString(16)
                    .takeLast(2)
            }
    }
}