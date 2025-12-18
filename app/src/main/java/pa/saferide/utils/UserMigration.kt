package pa.saferide.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object UserMigration {
    private const val TAG = "UserMigration"
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = Firebase.firestore

    /**
     * Migrate all users from Firestore to Firebase Auth
     * ONLY RUN ONCE!
     */
    fun migrateAllUsers(
        onProgress: (String) -> Unit,
        onError: (String) -> Unit,
        onComplete: () -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                onProgress("🔍 Mengambil data user dari Firestore...")

                // 1. Get all users from Firestore
                val snapshot = db.collection("users").get().await()

                onProgress("📋 Ditemukan ${snapshot.documents.size} user")

                var successCount = 0
                var errorCount = 0

                // 2. Process each user
                for (document in snapshot.documents) {
                    val data = document.data ?: continue

                    val email = data["email"] as? String ?: ""
                    val password = data["password"] as? String ?: ""
                    val username = data["username"] as? String ?: ""

                    if (email.isEmpty() || password.isEmpty()) {
                        onError("Email/password kosong untuk user: $username")
                        errorCount++
                        continue
                    }

                    try {
                        onProgress("🔄 Memproses: $email")

                        // 3. Try to create user in Firebase Auth
                        val userCredential = auth.createUserWithEmailAndPassword(email, password).await()

                        // 4. Update profile with username
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()

                        userCredential.user?.updateProfile(profileUpdates)?.await()

                        // 5. Update Firestore: add UID and remove password
                        document.reference.update(
                            mapOf(
                                "uid" to userCredential.user?.uid,
                                "migratedAt" to FieldValue.serverTimestamp(),
                                "password" to FieldValue.delete() // Remove plain text password
                            )
                        ).await()

                        successCount++
                        onProgress("✅ Berhasil: $email")

                        // Small delay to avoid rate limiting
                        kotlinx.coroutines.delay(500)

                    } catch (e: Exception) {
                        if (e.message?.contains("email-already-in-use") == true) {
                            // User already exists in Auth, get the UID
                            try {
                                val existingUser = auth.signInWithEmailAndPassword(email, password).await()

                                // Update Firestore with existing UID
                                document.reference.update(
                                    mapOf(
                                        "uid" to existingUser.user?.uid,
                                        "alreadyInAuth" to true,
                                        "password" to FieldValue.delete()
                                    )
                                ).await()

                                // Sign out after getting UID
                                auth.signOut()

                                successCount++
                                onProgress("⚠️ User sudah ada di Auth: $email")
                            } catch (signInError: Exception) {
                                errorCount++
                                onError("Gagal sign in $email: ${signInError.message}")
                            }
                        } else {
                            errorCount++
                            onError("Gagal buat user $email: ${e.message}")
                        }
                    }
                }

                // 6. Final cleanup
                onProgress("🧹 Membersihkan data...")
                for (document in snapshot.documents) {
                    try {
                        document.reference.update("password", FieldValue.delete()).await()
                    } catch (e: Exception) {
                        // Ignore if field doesn't exist
                    }
                }

                onProgress("🎉 Migration selesai! Berhasil: $successCount, Gagal: $errorCount")
                onComplete()

            } catch (e: Exception) {
                onError("Migration error: ${e.message}")
            }
        }
    }

    /**
     * Check if migration is needed
     */
    suspend fun isMigrationNeeded(): Boolean = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("users").limit(1).get().await()
            if (snapshot.documents.isEmpty()) return@withContext false

            val userData = snapshot.documents.first().data
            // Migration needed if password exists and is plain text
            val hasPassword = userData?.containsKey("password") == true
            val password = userData?.get("password") as? String ?: ""
            val isPlainText = password.isNotEmpty() && !password.startsWith("$2")

            return@withContext hasPassword && isPlainText
        } catch (e: Exception) {
            Log.e(TAG, "Error checking migration: ${e.message}")
            return@withContext false
        }
    }
}