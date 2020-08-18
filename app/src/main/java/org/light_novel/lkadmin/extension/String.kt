package org.light_novel.lkadmin.extension

import org.light_novel.lkadmin.LKAdminApplication
import java.math.BigInteger
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun String.sign(): String {
    val keyFactory = KeyFactory.getInstance("RSA")
    val keySpec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(LKAdminApplication.PRIVATE_KEY))
    val privateKey = keyFactory.generatePrivate(keySpec)
    val signature = Signature.getInstance("SHA256withRSA")
    signature.initSign(privateKey)
    signature.update(toByteArray())
    val signed = signature.sign()
    return String(Base64.getEncoder().encode(signed))
}

fun String.cutManage() = if (endsWith("管理")) {
    substring(IntRange(0, length - 3))
} else {
    this
}