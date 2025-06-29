package ru.pkstudio.localhomeworkandtaskmanager.core.data.encrypt

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object Crypto {
    //constant key alias for data store
    private const val KEY_ALIAS = "localHomeworkDataKeyAlias"

    //constant key alias for realm db
    private const val KEY_ALIAS_REALM = "localHomeworkRealmKeyAlias"

    private const val KEY_PREFERENCES = "KEY_PREFERENCES"
    private const val KEY_64BYTES_KEY = "KEY_64BYTES_KEY"
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"


    private val cipher = Cipher.getInstance(TRANSFORMATION)
    private val keyStore = KeyStore
        .getInstance("AndroidKeyStore")
        .apply {
            load(null)
        }

    // data store and files encryption
    private fun getKey(): SecretKey {
        val existingKey = keyStore
            .getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator
            .getInstance(ALGORITHM)
            .apply {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(PADDING)
                        .setRandomizedEncryptionRequired(true)
                        .setUserAuthenticationRequired(false)
                        .build()
                )
            }
            .generateKey()
    }

    fun encrypt(bytes: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(bytes)
        return iv + encrypted
    }

    fun decrypt(bytes: ByteArray): ByteArray {
        val iv = bytes.copyOfRange(0, cipher.blockSize)
        val data = bytes.copyOfRange(cipher.blockSize, bytes.size)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        return cipher.doFinal(data)
    }

    //realm encryption
    //get key from keyStore for encrypt or decrypt 64 bytes realm key
    private fun getRealmKey(): SecretKey {
        val existingKey = keyStore
            .getEntry(KEY_ALIAS_REALM, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createRealmKey()
    }

    private fun createRealmKey(): SecretKey {
        return KeyGenerator
            .getInstance(ALGORITHM)
            .apply {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS_REALM,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(PADDING)
                        .setRandomizedEncryptionRequired(true)
                        .setUserAuthenticationRequired(false)
                        .build()
                )
            }
            .generateKey()
    }

    //because 64bytes key stored in shared prefs we need to encrypt in adn store in Base64 string
    private fun encryptRealmKey(bytes: ByteArray): String {
        cipher.init(Cipher.ENCRYPT_MODE, getRealmKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(bytes)
        val encryptedBytes = iv + encrypted
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    private fun decryptRealmKey(key: String): ByteArray {
        val bytes = Base64.decode(key, Base64.DEFAULT)
        val iv = bytes.copyOfRange(0, cipher.blockSize)
        val data = bytes.copyOfRange(cipher.blockSize, bytes.size)
        cipher.init(Cipher.DECRYPT_MODE, getRealmKey(), IvParameterSpec(iv))
        return cipher.doFinal(data)
    }

    fun getKeyForRealm(context: Context): ByteArray {
        val sharedPreferences =
            context.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
        val key = sharedPreferences.getString(KEY_64BYTES_KEY, "")
        if (key.isNullOrBlank()) {
            val keyBytes = create64BytesKey()
            val encryptedKey = encryptRealmKey(keyBytes)
            sharedPreferences.edit {
                putString(KEY_64BYTES_KEY, encryptedKey)
            }
            return keyBytes
        }
        return decryptRealmKey(key)
    }

    //Realm db needs to be encrypted by 64bytes key only
    private fun create64BytesKey(): ByteArray {
        val key = ByteArray(64)
        SecureRandom().nextBytes(key)
        return key
    }
}