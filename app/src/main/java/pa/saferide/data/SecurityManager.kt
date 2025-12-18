package pa.saferide.data

import at.favre.lib.crypto.bcrypt.BCrypt

object SecurityManager {

    // 1. Hash password
    fun hashPassword(plainPassword: String): String {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray())
    }

    // 2. Verify password
    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword).verified
    }

    // 3. Generate password untuk user baru
    fun generatePassword(username: String): String {
        val random = (1000..9999).random()
        return "${username.lowercase()}$random"
    }
}