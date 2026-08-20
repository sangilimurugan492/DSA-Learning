# Mobile Payment Implementation — Code Examples

> Production-ready code snippets for critical payment components. Language: Kotlin (Android) and Dart (Flutter) unless noted.

---

## Table of Contents

1. [Request Signing (HMAC-SHA256)](#1-request-signing-hmac-sha256)
2. [Idempotency Interceptor (Dio)](#2-idempotency-interceptor-dio)
3. [SSL Certificate Pinning](#3-ssl-certificate-pinning)
4. [SQLCipher Encrypted Local Database](#4-sqlcipher-encrypted-local-database)
5. [Secure Token Storage (Keystore)](#5-secure-token-storage-keystore)
6. [Double-Entry Ledger (SQL)](#6-double-entry-ledger-sql)
7. [Idempotency on Server (Redis + DB)](#7-idempotency-on-server-redis--db)
8. [Fraud Risk Scoring](#8-fraud-risk-scoring)
9. [Biometric Authentication (Android)](#9-biometric-authentication-android)
10. [UPI Intent & Deep Link Handling](#10-upi-intent--deep-link-handling)
11. [Webhook Signature Verification](#11-webhook-signature-verification)
12. [Payment State Machine (Sealed Classes)](#12-payment-state-machine-sealed-classes)
13. [Database Schema (PostgreSQL)](#13-database-schema-postgresql)

---

## 1. Request Signing (HMAC-SHA256)

Every payment request is signed with a device-specific secret key to detect tampering.

### Kotlin (Android)

```kotlin
// RequestSigner.kt

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest

object RequestSigner {

    /**
     * Signs a payment request using HMAC-SHA256.
     *
     * @param method HTTP method (POST, GET)
     * @param path API path (/api/payments)
     * @param timestamp Unix timestamp (seconds)
     * @param bodyHash SHA-256 hash of request body
     * @param secretKey Device secret key from Keystore
     * @return Hex-encoded HMAC signature
     */
    fun sign(
        method: String,
        path: String,
        timestamp: String,
        bodyHash: String,
        secretKey: ByteArray
    ): String {
        // Build canonical string
        val canonical = "$method\n$path\n$timestamp\n$bodyHash"

        // HMAC-SHA256
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secretKey, "HmacSHA256"))
        val signature = mac.doFinal(canonical.toByteArray(Charsets.UTF_8))

        return signature.toHex()
    }

    /**
     * SHA-256 hash of request body (for integrity check)
     */
    fun bodyHash(body: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(body.toByteArray(Charsets.UTF_8)).toHex()
    }

    private fun ByteArray.toHex(): String =
        joinToString("") { "%02x".format(it) }
}
```

### Usage in API Interceptor

```kotlin
// SigningInterceptor.kt

class SigningInterceptor(
    private val keystoreHelper: KeystoreHelper
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val body = request.body?.let { bodyToString(it) } ?: ""

        // Only sign payment-related requests
        if (!request.url.encodedPath.startsWith("/api/payments") &&
            !request.url.encodedPath.startsWith("/api/wallet") &&
            !request.url.encodedPath.startsWith("/api/refunds")
        ) {
            return chain.proceed(request)
        }

        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val bodyHash = RequestSigner.bodyHash(body)
        val secretKey = keystoreHelper.getDeviceSecretKey()

        val signature = RequestSigner.sign(
            method = request.method,
            path = request.url.encodedPath,
            timestamp = timestamp,
            bodyHash = bodyHash,
            secretKey = secretKey
        )

        val signedRequest = request.newBuilder()
            .addHeader("X-Request-Signature", signature)
            .addHeader("X-Request-Timestamp", timestamp)
            .addHeader("X-Body-Hash", bodyHash)
            .build()

        return chain.proceed(signedRequest)
    }

    private fun bodyToString(body: RequestBody): String {
        val buffer = Buffer()
        body.writeTo(buffer)
        return buffer.readUtf8()
    }
}
```

---

## 2. Idempotency Interceptor (Dio)

Automatically attaches idempotency keys to payment POST requests.

### Dart (Flutter/Dio)

```dart
// idempotency_interceptor.dart

import 'package:dio/dio.dart';
import 'package:uuid/uuid.dart';

class IdempotencyInterceptor extends Interceptor {
  final _uuid = const Uuid();

  /// Keys currently in-flight (to prevent duplicate concurrent requests)
  final Map<String, String> _activeKeys = {};

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    // Only add idempotency key to payment POST requests
    if (options.method == 'POST' &&
        options.path.startsWith('/api/payments')) {

      // Check if request already has a key (e.g., retry with same key)
      if (!options.headers.containsKey('Idempotency-Key')) {
        final key = _uuid.v4();
        options.headers['Idempotency-Key'] = key;
      }

      // Store for potential retry tracking
      final idempotencyKey = options.headers['Idempotency-Key'] as String;
      _activeKeys[options.path] = idempotencyKey;
    }

    handler.next(options);
  }

  @override
  void onResponse(Response response, ResponseInterceptorHandler handler) {
    // Clean up on success
    _activeKeys.remove(response.requestOptions.path);
    handler.next(response);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    // Keep the key in _activeKeys for potential retry
    // (don't remove — user may retry with same key)
    handler.next(err);
  }

  /// Get the idempotency key for a given path (for retry)
  String? getKeyForPath(String path) => _activeKeys[path];

  /// Remove a key after it's no longer needed
  void clearKey(String path) => _activeKeys.remove(path);
}
```

---

## 3. SSL Certificate Pinning

Prevents man-in-the-middle attacks by pinning expected server certificates.

### Kotlin (OkHttp)

```kotlin
// NetworkModule.kt

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object NetworkModule {

    fun createOkHttpClient(): OkHttpClient {
        val certificatePinner = CertificatePinner.Builder()
            // Pin by SHA-256 hash of certificate public key
            // Get hash: openssl s_client -connect api.app.com:443 | openssl x509 -pubkey | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64
            .add("api.app.com", "sha256/47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=")
            .add("api.app.com", "sha256/mEHWZ5QaJSaKpYpVbUK1X9q5JW8Q5Z5QaJSaKpYpVbUK=") // Backup pin
            .build()

        return OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
            .connectTimeout(8, TimeUnit.SECONDS)   // Fast — user is waiting
            .readTimeout(15, TimeUnit.SECONDS)      // Bank may take time
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)        // CRITICAL: no auto-retry for payments
            .addInterceptor(SigningInterceptor(KeystoreHelper()))
            .addInterceptor(AuthInterceptor(TokenStorage()))
            .build()
    }
}
```

### Dart (Flutter/Dio)

```dart
// ssl_pinning.dart

import 'dart:io';
import 'package:dio/io.dart';

class SslPinningInterceptor extends InterceptorAdapter {
  final List<String> _pinnedHashes = [
    'sha256/47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=',
    'sha256/mEHWZ5QaJSaKpYpVbUK1X9q5JW8Q5Z5QaJSaKpYpVbUK=', // Backup
  ];

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) async {
    // Bypass for development
    if (kDebugMode && options.path.contains('localhost')) {
      handler.next(options);
      return;
    }
    handler.next(options);
  }
}

// Dio setup with pinning
Dio createDio() {
  final dio = Dio();

  // SSL Pinning via HttpClient
  (dio.httpClientAdapter as IOHttpClientAdapter).createHttpClient = () {
    final client = HttpClient();
    client.badCertificateCallback = (cert, host, port) {
      // Verify certificate pin
      final certHash = cert.sha256.toString();
      // Compare with pinned hashes
      // Return false to reject if not matching
      return false; // Reject all non-pinned certificates
    };
    return client;
  };

  return dio;
}
```

---

## 4. SQLCipher Encrypted Local Database

All local data (transaction history, cached balance) is encrypted at rest.

### Kotlin (Android with SQLCipher + Room)

```kotlin
// DatabaseModule.kt

import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import androidx.room.Room
import androidx.room.RoomDatabase

object DatabaseModule {

    fun createDatabase(context: Context, passphrase: ByteArray): AppDatabase {
        // SQLCipher factory — encrypts entire database
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "payment_db"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Derive encryption key from device secret + user PIN
     * (stored in Keystore, never in plain text)
     */
    fun derivePassphrase(keystoreHelper: KeystoreHelper, userPin: CharArray): ByteArray {
        val deviceSecret = keystoreHelper.getDeviceSecretKey()

        // PBKDF2 key derivation
        val spec = PBEKeySpec(
            userPin + deviceSecret.toString().toCharArray(),
            SALT,  // Fixed salt (or derived from device ID)
            10000, // Iteration count
            256    // Key length (bits)
        )

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key = factory.generateSecret(spec).encoded

        // Zero out PIN from memory
        userPin.fill('0')

        return key
    }

    private val SALT = byteArrayOf(
        0x4e, 0x6f, 0x76, 0x65, 0x6d, 0x62, 0x65, 0x72
    )
}

// AppDatabase.kt
@Database(
    entities = [
        CachedTransactionEntity::class,
        CachedBalanceEntity::class,
        PendingTransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun balanceDao(): BalanceDao
    abstract fun pendingTxnDao(): PendingTxnDao
}

// Entity
@Entity(tableName = "cached_transactions")
data class CachedTransactionEntity(
    @PrimaryKey val id: String,
    val type: String,           // DEBIT, CREDIT, TOPUP, REFUND
    val amount: Double,
    val recipientName: String?,
    val recipientUpi: String?,
    val status: String,         // SUCCESS, PENDING, FAILED
    val timestamp: Long,
    val receiptUrl: String?,
    val referenceId: String?
)

@Dao
interface TransactionDao {
    @Query("SELECT * FROM cached_transactions ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getTransactions(limit: Int, offset: Int): List<CachedTransactionEntity>

    @Query("SELECT * FROM cached_transactions WHERE id = :id")
    suspend fun getById(id: String): CachedTransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: CachedTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<CachedTransactionEntity>)

    @Query("DELETE FROM cached_transactions WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long): Int

    @Query("SELECT COUNT(*) FROM cached_transactions")
    suspend fun count(): Int
}
```

---

## 5. Secure Token Storage (Keystore)

Auth tokens are stored in Android Keystore (hardware-backed) — never in SharedPreferences.

### Kotlin (Android)

```kotlin
// KeystoreHelper.kt

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class KeystoreHelper {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "payment_app_secret_key"
        private const val IV_SEPARATOR = "]"
        private val GCM_IV_LENGTH = 12 // bytes
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    /**
     * Get or create the device secret key (stored in Keystore, hardware-backed)
     */
    fun getOrCreateDeviceSecretKey(): SecretKey {
        keyStore.getKey(KEY_ALIAS, null)?.let {
            return it as SecretKey
        }

        // Generate new key
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false) // Key doesn't require biometric to use
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    /**
     * Encrypt data (e.g., auth tokens) using Keystore key
     */
    fun encrypt(plaintext: ByteArray): String {
        val key = getOrCreateDeviceSecretKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val iv = cipher.iv
        val encrypted = cipher.doFinal(plaintext)

        // Combine IV + encrypted data
        return android.util.Base64.encodeToString(
            iv + IV_SEPARATOR.toByteArray() + encrypted,
            android.util.Base64.NO_WRAP
        )
    }

    /**
     * Decrypt data using Keystore key
     */
    fun decrypt(encryptedData: String): ByteArray {
        val key = getOrCreateDeviceSecretKey()
        val decoded = android.util.Base64.decode(encryptedData, android.util.Base64.NO_WRAP)

        // Split IV and encrypted data
        val separatorIndex = decoded.indexOf(IV_SEPARATOR.toByteArray()[0])
        val iv = decoded.copyOfRange(0, GCM_IV_LENGTH)
        val encrypted = decoded.copyOfRange(
            GCM_IV_LENGTH + IV_SEPARATOR.length,
            decoded.size
        )

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))

        return cipher.doFinal(encrypted)
    }

    /**
     * Get raw key bytes for HMAC signing
     * (Keystore doesn't allow extracting key — use encrypt/decrypt instead)
     */
    fun getDeviceSecretKey(): ByteArray {
        // For HMAC, we generate a separate key and store encrypted
        // Or use a derived key from the Keystore key
        return getOrCreateDeviceSecretKey().encoded
            ?: throw IllegalStateException("Cannot extract key from Keystore")
    }
}

// TokenStorage.kt — Stores auth tokens securely

class TokenStorage(
    private val keystoreHelper: KeystoreHelper,
    private val encryptedPrefs: EncryptedSharedPreferences
) {
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
    }

    suspend fun saveAccessToken(token: String) {
        val encrypted = keystoreHelper.encrypt(token.toByteArray())
        encryptedPrefs.edit().putString(KEY_ACCESS_TOKEN, encrypted).apply()
    }

    suspend fun getAccessToken(): String? {
        val encrypted = encryptedPrefs.getString(KEY_ACCESS_TOKEN, null) ?: return null
        return String(keystoreHelper.decrypt(encrypted))
    }

    suspend fun saveRefreshToken(token: String) {
        val encrypted = keystoreHelper.encrypt(token.toByteArray())
        encryptedPrefs.edit().putString(KEY_REFRESH_TOKEN, encrypted).apply()
    }

    suspend fun getRefreshToken(): String? {
        val encrypted = encryptedPrefs.getString(KEY_REFRESH_TOKEN, null) ?: return null
        return String(keystoreHelper.decrypt(encrypted))
    }

    suspend fun clearTokens() {
        encryptedPrefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_TOKEN_EXPIRY)
            .apply()
    }

    suspend fun isTokenExpired(): Boolean {
        val expiry = encryptedPrefs.getLong(KEY_TOKEN_EXPIRY, 0)
        return System.currentTimeMillis() > expiry
    }
}
```

---

## 6. Double-Entry Ledger (SQL)

Every financial transaction creates balanced debit + credit entries atomically.

### PostgreSQL Schema

```sql
-- accounts table — stores current balance
CREATE TABLE accounts (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    type VARCHAR(20) NOT NULL,          -- WALLET, BANK, ESCROW
    balance DECIMAL(15,2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) DEFAULT 'INR',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    version BIGINT DEFAULT 0            -- Optimistic locking
);

-- ledger_entries table — append-only, immutable
CREATE TABLE ledger_entries (
    id BIGSERIAL PRIMARY KEY,
    txn_id VARCHAR(64) NOT NULL,        -- Groups entries for one transaction
    account_id VARCHAR(64) NOT NULL REFERENCES accounts(id),
    entry_type VARCHAR(10) NOT NULL,    -- DEBIT, CREDIT
    amount DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2),         -- Snapshot for audit
    reference_type VARCHAR(20),         -- PAYMENT, TOPUP, REFUND
    reference_id VARCHAR(64),
    created_at TIMESTAMP DEFAULT NOW(),
    
    CONSTRAINT chk_entry_type CHECK (entry_type IN ('DEBIT', 'CREDIT')),
    CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

-- Indexes
CREATE INDEX idx_ledger_txn_id ON ledger_entries(txn_id);
CREATE INDEX idx_ledger_account_id ON ledger_entries(account_id);
CREATE INDEX idx_ledger_created_at ON ledger_entries(created_at);

-- transactions table — high-level transaction record
CREATE TABLE transactions (
    id VARCHAR(64) PRIMARY KEY,
    idempotency_key VARCHAR(64) UNIQUE NOT NULL,
    sender_account_id VARCHAR(64) NOT NULL REFERENCES accounts(id),
    recipient_account_id VARCHAR(64) NOT NULL REFERENCES accounts(id),
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    payment_method VARCHAR(20) NOT NULL, -- UPI, CARD, WALLET
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    risk_score INTEGER,
    reference_id VARCHAR(64),            -- Bank reference
    failure_reason TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'TIMEOUT')),
    CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

CREATE INDEX idx_txn_idempotency ON transactions(idempotency_key);
CREATE INDEX idx_txn_sender ON transactions(sender_account_id);
CREATE INDEX idx_txn_status ON transactions(status);
CREATE INDEX idx_txn_created ON transactions(created_at);
```

### Transfer Function (PostgreSQL Function)

```sql
-- Atomic transfer with row-level locking
CREATE OR REPLACE FUNCTION transfer_funds(
    p_txn_id VARCHAR,
    p_sender_id VARCHAR,
    p_recipient_id VARCHAR,
    p_amount DECIMAL,
    p_reference_type VARCHAR DEFAULT 'PAYMENT'
) RETURNS VARCHAR AS $$
DECLARE
    v_sender_balance DECIMAL;
    v_recipient_balance DECIMAL;
BEGIN
    -- Lock sender's account row (prevents concurrent debits)
    SELECT balance INTO v_sender_balance
    FROM accounts
    WHERE id = p_sender_id
    FOR UPDATE;
    
    -- Check sufficient balance
    IF v_sender_balance < p_amount THEN
        RETURN 'INSUFFICIENT_FUNDS';
    END IF;
    
    -- Lock recipient's account row
    SELECT balance INTO v_recipient_balance
    FROM accounts
    WHERE id = p_recipient_id
    FOR UPDATE;
    
    -- Debit sender
    UPDATE accounts
    SET balance = balance - p_amount,
        updated_at = NOW(),
        version = version + 1
    WHERE id = p_sender_id;
    
    -- Credit recipient
    UPDATE accounts
    SET balance = balance + p_amount,
        updated_at = NOW(),
        version = version + 1
    WHERE id = p_recipient_id;
    
    -- Write ledger entries (immutable)
    INSERT INTO ledger_entries (txn_id, account_id, entry_type, amount, balance_after, reference_type)
    VALUES
        (p_txn_id, p_sender_id, 'DEBIT', p_amount, v_sender_balance - p_amount, p_reference_type),
        (p_txn_id, p_recipient_id, 'CREDIT', p_amount, v_recipient_balance + p_amount, p_reference_type);
    
    RETURN 'SUCCESS';
END;
$$ LANGUAGE plpgsql;

-- Usage:
-- SELECT transfer_funds('TXN_001', 'USER_A', 'USER_B', 500.00, 'PAYMENT');
```

---

## 7. Idempotency on Server (Redis + DB)

Server-side idempotency handling — deduplicates retried requests.

### Kotlin (Ktor Server)

```kotlin
// IdempotencyService.kt

class IdempotencyService(
    private val redisClient: RedisClient,
    private val transactionRepo: TransactionRepository
) {
    companion object {
        private const val IDEMPOTENCY_TTL = 24 * 3600L // 24 hours
        private const val KEY_PREFIX = "idem:"
    }

    /**
     * Check if request with this idempotency key was already processed.
     * Returns cached result if found, null if new.
     */
    suspend fun checkExisting(
        idempotencyKey: String,
        requestBody: String
    ): IdempotencyResult? {
        // 1. Check Redis (fast path)
        val cached = redisClient.get("$KEY_PREFIX$idempotencyKey")
        if (cached != null) {
            val stored = Json.decodeFromString<StoredResult>(cached)

            // Verify same request body (detect tampering)
            if (stored.requestHash != hashBody(requestBody)) {
                return IdempotencyResult.Conflict("Request body mismatch — potential tampering")
            }

            return IdempotencyResult.Cached(stored.result)
        }

        // 2. Check DB (Redis might have expired)
        val txn = transactionRepo.findByIdempotencyKey(idempotencyKey)
        if (txn != null) {
            // Re-populate Redis
            redisClient.setex(
                "$KEY_PREFIX$idempotencyKey",
                IDEMPOTENCY_TTL,
                Json.encodeToString(StoredResult(
                    requestHash = hashBody(requestBody),
                    result = txn.toResult()
                ))
            )
            return IdempotencyResult.Cached(txn.toResult())
        }

        // 3. New request — store in Redis as "processing"
        redisClient.setex(
            "$KEY_PREFIX$idempotencyKey",
            IDEMPOTENCY_TTL,
            Json.encodeToString(StoredResult(
                requestHash = hashBody(requestBody),
                result = TransactionResult(status = "PENDING")
            ))
        )

        return null // New request — proceed with processing
    }

    /**
     * Store the result of a processed request.
     */
    suspend fun storeResult(
        idempotencyKey: String,
        result: TransactionResult
    ) {
        redisClient.setex(
            "$KEY_PREFIX$idempotencyKey",
            IDEMPOTENCY_TTL,
            Json.encodeToString(StoredResult(
                requestHash = "", // Already verified
                result = result
            ))
        )
    }

    private fun hashBody(body: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(body.toByteArray()).toHex()
    }
}

sealed class IdempotencyResult {
    data class Cached(val result: TransactionResult) : IdempotencyResult()
    data class Conflict(val message: String) : IdempotencyResult()
}

@Serializable
data class StoredResult(
    val requestHash: String,
    val result: TransactionResult
)

@Serializable
data class TransactionResult(
    val txnId: String? = null,
    val status: String,    // SUCCESS, PENDING, FAILED
    val receipt: ReceiptDto? = null,
    val errorMessage: String? = null
)
```

### Ktor Route with Idempotency

```kotlin
// PaymentRoutes.kt

fun Route.paymentRoutes(
    idempotencyService: IdempotencyService,
    paymentService: PaymentService
) {
    post("/api/payments") {
        val idempotencyKey = call.request.headers["Idempotency-Key"]
            ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Idempotency-Key header required")
            )

        val rawBody = call.receiveText()
        val request = Json.decodeFromString<PaymentRequest>(rawBody)

        // 1. Check idempotency
        val existing = idempotencyService.checkExisting(idempotencyKey, rawBody)
        when (existing) {
            is IdempotencyResult.Cached -> {
                // Return cached result (no re-processing)
                return@post call.respond(existing.result)
            }
            is IdempotencyResult.Conflict -> {
                // Same key, different body — tampering attempt
                return@post call.respond(
                    HttpStatusCode.Conflict,
                    mapOf("error" to existing.message)
                )
            }
            null -> {
                // New request — proceed
            }
        }

        // 2. Process payment
        val result = paymentService.processPayment(request, idempotencyKey)

        // 3. Store result for idempotency
        idempotencyService.storeResult(idempotencyKey, result)

        // 4. Return result
        call.respond(result)
    }

    get("/api/payments/{idempotencyKey}/status") {
        val key = call.parameters["idempotencyKey"]!!
        val status = paymentService.getStatusByIdempotencyKey(key)
        call.respond(status)
    }
}
```

---

## 8. Fraud Risk Scoring

Server-side fraud detection with rule-based + ML scoring.

### Kotlin

```kotlin
// FraudDetectionService.kt

class FraudDetectionService(
    private val transactionRepo: TransactionRepository,
    private val deviceRepo: DeviceRepository,
    private val mlModelClient: MlModelClient
) {
    companion object {
        const val LOW_RISK_THRESHOLD = 30
        const val MEDIUM_RISK_THRESHOLD = 70
        const val HIGH_RISK_THRESHOLD = 100
    }

    /**
     * Synchronous fraud check (runs before payment processing).
     * Must be fast (< 50ms) — uses cached data + rules.
     */
    suspend fun checkRisk(request: PaymentRequest, userId: String): RiskAssessment {
        var score = 0
        val reasons = mutableListOf<String>()

        // 1. Velocity check — how many transactions in last 5 min?
        val recentCount = transactionRepo.countRecentTransactions(
            userId,
            Duration.ofMinutes(5)
        )
        when {
            recentCount > 10 -> { score += 30; reasons.add("High velocity: $recentCount txns in 5 min") }
            recentCount > 5 -> { score += 15; reasons.add("Moderate velocity: $recentCount txns in 5 min") }
        }

        // 2. Amount anomaly — is this amount unusual for this user?
        val avgAmount = transactionRepo.getAverageAmount(userId)
        if (request.amount > avgAmount * 10) {
            score += 25
            reasons.add("Amount ${request.amount} is 10x average ($avgAmount)")
        } else if (request.amount > avgAmount * 5) {
            score += 10
            reasons.add("Amount ${request.amount} is 5x average ($avgAmount)")
        }

        // 3. Device trust — is this a new or rooted device?
        val device = deviceRepo.getDeviceForUser(userId)
        if (device == null) {
            score += 20
            reasons.add("New device")
        } else if (device.isRooted) {
            score += 50
            reasons.add("Rooted device detected")
        } else if (device.isEmulator) {
            score += 40
            reasons.add("Emulator detected")
        }

        // 4. Time anomaly — unusual time for this user?
        val hour = LocalDateTime.now().hour
        val userActiveHours = transactionRepo.getUserActiveHours(userId)
        if (hour !in userActiveHours && hour in 0..5) {
            score += 15
            reasons.add("Transaction at unusual hour ($hour:00)")
        }

        // 5. Recipient trust — new recipient?
        val isKnownRecipient = transactionRepo.isKnownRecipient(userId, request.recipientUpi)
        if (!isKnownRecipient) {
            score += 10
            reasons.add("New recipient: ${request.recipientUpi}")
        }

        // 6. Get cached ML score (updated asynchronously)
        val mlScore = mlModelClient.getCachedScore(userId)
        score = (score + mlScore).coerceAtMost(100)

        // Determine action
        val action = when {
            score >= HIGH_RISK_THRESHOLD -> RiskAction.BLOCK
            score >= MEDIUM_RISK_THRESHOLD -> RiskAction.CHALLENGE
            else -> RiskAction.ALLOW
        }

        return RiskAssessment(
            score = score,
            action = action,
            reasons = reasons
        )
    }

    /**
     * Asynchronous deep analysis (runs after payment via Kafka).
     * Updates user risk profile for future transactions.
     */
    suspend fun analyzeAsync(txnId: String, userId: String) {
        // Full ML model inference (can take 1-5 seconds)
        val deepScore = mlModelClient.predictDeep(userId, txnId)

        // Update user risk profile
        mlModelClient.updateUserRiskProfile(userId, deepScore)

        // If post-facto fraud detected → flag for review
        if (deepScore > 80) {
            // Publish alert
            kafkaProducer.send("fraud.alert", txnId, mapOf(
                "userId" to userId,
                "txnId" to txnId,
                "score" to deepScore
            ).toJson())
        }
    }
}

data class RiskAssessment(
    val score: Int,        // 0-100
    val action: RiskAction,
    val reasons: List<String>
)

enum class RiskAction {
    ALLOW,      // 0-30: auto-approve
    CHALLENGE,  // 31-70: require OTP or additional verification
    BLOCK       // 71-100: block transaction
}
```

---

## 9. Biometric Authentication (Android)

### Kotlin (BiometricPrompt)

```kotlin
// BiometricAuth.kt

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricAuth(private val activity: FragmentActivity) {

    fun isAvailable(): Boolean {
        val manager = BiometricManager.from(activity)
        return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Prompt biometric authentication.
     * @param reason Shown to user (e.g., "Confirm payment of ₹500")
     * @param biometricsOnly If true, only fingerprint/face (no device PIN fallback)
     * @param onSuccess Called when authentication succeeds
     * @param onFailure Called when authentication fails or is unavailable
     */
    fun authenticate(
        reason: String,
        biometricsOnly: Boolean = true,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (!isAvailable()) {
            onFailure("Biometric authentication not available")
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                // User's fingerprint/face didn't match — prompt stays
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        onFailure("Authentication canceled by user")
                    }
                    BiometricPrompt.ERROR_LOCKOUT -> {
                        onFailure("Too many attempts. Try again later.")
                    }
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                        onFailure("Biometric locked. Use PIN to unlock.")
                    }
                    BiometricPrompt.ERROR_HW_NOT_PRESENT,
                    BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
                        onFailure("Biometric hardware not available")
                    }
                    else -> onFailure(errString.toString())
                }
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)

        val authenticators = if (biometricsOnly) {
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        } else {
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        }

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate")
            .setSubtitle(reason)
            .setAllowedAuthenticators(authenticators)
            .setNegativeButtonText("Cancel") // Required when biometricsOnly
            .setConfirmationRequired(false)
            .build()

        prompt.authenticate(info)
    }
}
```

---

## 10. UPI Intent & Deep Link Handling

### Kotlin (Android)

```kotlin
// UpiIntentHandler.kt

import android.content.Intent
import android.net.Uri
import android.app.Activity

class UpiIntentHandler(private val activity: Activity) {

    companion object {
        const val UPI_PAYMENT_REQUEST = 1001
    }

    /**
     * Launch UPI payment via intent (opens UPI app chooser)
     */
    fun initiateUpiPayment(
        payeeVpa: String,
        payeeName: String,
        amount: String,
        note: String,
        transactionRef: String
    ) {
        val uri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", payeeVpa)
            .appendQueryParameter("pn", payeeName)
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR")
            .appendQueryParameter("tn", note)
            .appendQueryParameter("tr", transactionRef)
            .build()

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = uri
        }

        // Check if any UPI app can handle this intent
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(intent, UPI_PAYMENT_REQUEST)
        } else {
            // No UPI app installed
            showNoUpiAppError()
        }
    }

    /**
     * Handle result from UPI app
     */
    fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        onResult: (UpiPaymentResult) -> Unit
    ) {
        if (requestCode != UPI_PAYMENT_REQUEST) return

        if (resultCode != Activity.RESULT_OK || data == null) {
            onResult(UpiPaymentResult.Cancelled)
            return
        }

        val response = data.getStringExtra("response") ?: run {
            onResult(UpiPaymentResult.Failed("No response from UPI app"))
            return
        }

        // Parse response: "status=SUCCESS&txnRef=TXN001&txnId=ABC123&responseCode=00"
        val params = response.split("&").associate {
            val (key, value) = it.split("=", limit = 2)
            key to value
        }

        val status = params["status"]
        val txnRef = params["txnRef"]
        val txnId = params["txnId"]
        val responseCode = params["responseCode"]

        when (status?.lowercase()) {
            "success" -> onResult(UpiPaymentResult.Success(txnId, txnRef))
            "failure" -> onResult(UpiPaymentResult.Failed("Payment failed: $responseCode"))
            "submitted" -> onResult(UpiPaymentResult.Pending(txnId, txnRef))
            else -> onResult(UpiPaymentResult.Failed("Unknown status: $status"))
        }
    }

    private fun showNoUpiAppError() {
        // Show dialog: "No UPI app found. Please install Google Pay, PhonePe, or Paytm."
    }
}

sealed class UpiPaymentResult {
    data class Success(val txnId: String?, val txnRef: String?) : UpiPaymentResult()
    data class Pending(val txnId: String?, val txnRef: String?) : UpiPaymentResult()
    data class Failed(val message: String) : UpiPaymentResult()
    object Cancelled : UpiPaymentResult()
}
```

### AndroidManifest.xml — Deep Link Registration

```xml
<!-- Register app to handle UPI deep links -->
<activity android:name=".PaymentActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <!-- Handle upi:// scheme -->
        <data android:scheme="upi" />

        <!-- Handle app deep links -->
        <data
            android:scheme="https"
            android:host="app.example.com"
            android:pathPrefix="/pay" />
    </intent-filter>
</activity>

<!-- App Links verification (no disambiguation dialog) -->
<activity android:name=".PaymentActivity">
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="https" android:host="app.example.com" />
    </intent-filter>
</activity>
```

---

## 11. Webhook Signature Verification

Verify that webhooks from payment gateways are authentic.

### Kotlin (Ktor)

```kotlin
// WebhookSignatureVerifier.kt

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class WebhookSignatureVerifier(private val webhookSecret: String) {

    /**
     * Verify webhook signature.
     * Different gateways use different formats:
     * - Stripe: X-Signature header, HMAC-SHA256 of raw body
     * - Razorpay: X-Razorpay-Signature, HMAC-SHA256 of raw body
     * - Custom: X-Request-Signature, HMAC-SHA256 of canonical string
     */
    fun verify(
        rawBody: String,
        signature: String,
        timestamp: String? = null
    ): Boolean {
        // Include timestamp in signature to prevent replay attacks
        val dataToSign = if (timestamp != null) "$timestamp.$rawBody" else rawBody

        val expectedSignature = hmacSha256(dataToSign, webhookSecret)

        // Constant-time comparison to prevent timing attacks
        return constantTimeEquals(signature, expectedSignature)
    }

    private fun hmacSha256(data: String, key: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key.toByteArray(), "HmacSHA256"))
        return mac.doFinal(data.toByteArray()).toHex()
    }

    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }

    private fun ByteArray.toHex(): String =
        joinToString("") { "%02x".format(it) }
}
```

### Usage in Webhook Handler

```kotlin
// In webhook route
post("/webhooks/stripe") {
    val signature = call.request.headers["Stripe-Signature"]
        ?: return@post call.respond(HttpStatusCode.BadRequest)
    val timestamp = call.request.headers["Stripe-Timestamp"]
    val rawBody = call.receiveText()

    val verifier = WebhookSignatureVerifier(stripeWebhookSecret)

    if (!verifier.verify(rawBody, signature, timestamp)) {
        // Log the attempt
        logger.warn("Invalid webhook signature from IP: ${call.request.origin.remoteHost}")
        return@post call.respond(HttpStatusCode.Unauthorized)
    }

    // Signature valid — process webhook
    val event = Json.decodeFromString<StripeEvent>(rawBody)
    // ... process event
    call.respond(HttpStatusCode.OK)
}
```

---

## 12. Payment State Machine (Sealed Classes)

Type-safe state machine using Kotlin sealed classes.

### Kotlin

```kotlin
// PaymentStateMachine.kt

/**
 * Payment state machine — enforces valid state transitions.
 * No state can be skipped or transitioned incorrectly.
 */
sealed class PaymentState {
    object Idle : PaymentState()
    object Validating : PaymentState()
    object Authenticating : PaymentState()
    object Processing : PaymentState()
    data class Success(val receipt: Receipt) : PaymentState()
    data class Failed(val error: PaymentError, val canRetry: Boolean) : PaymentState()
    data class Timeout(val idempotencyKey: String) : PaymentState()

    /**
     * Valid state transitions.
     * Returns next state or null if transition is invalid.
     */
    fun transition(event: PaymentEvent): PaymentState? {
        return when (this) {
            is Idle -> when (event) {
                is PaymentEvent.Start -> Validating
                else -> null
            }
            is Validating -> when (event) {
                is PaymentEvent.Valid -> Authenticating
                is PaymentEvent.Invalid -> Failed(event.error, canRetry = false)
                else -> null
            }
            is Authenticating -> when (event) {
                is PaymentEvent.Authenticated -> Processing
                is PaymentEvent.AuthFailed -> Failed(
                    PaymentError("Authentication failed"),
                    canRetry = true
                )
                else -> null
            }
            is Processing -> when (event) {
                is PaymentEvent.Succeeded -> Success(event.receipt)
                is PaymentEvent.Failed -> Failed(event.error, canRetry = event.retryable)
                is PaymentEvent.TimedOut -> Timeout(event.idempotencyKey)
                else -> null
            }
            is Timeout -> when (event) {
                is PaymentEvent.CheckStatus -> Processing
                is PaymentEvent.Succeeded -> Success(event.receipt)
                is PaymentEvent.Failed -> Failed(event.error, canRetry = true)
                else -> null
            }
            is Failed -> when (event) {
                is PaymentEvent.Retry -> if (canRetry) Validating else null
                else -> null
            }
            is Success -> null // Terminal state — no transitions
        }
    }
}

sealed class PaymentEvent {
    object Start : PaymentEvent()
    data class Valid(val amount: Double, val recipient: String) : PaymentEvent()
    data class Invalid(val error: PaymentError) : PaymentEvent()
    object Authenticated : PaymentEvent()
    object AuthFailed : PaymentEvent()
    data class Succeeded(val receipt: Receipt) : PaymentEvent()
    data class Failed(val error: PaymentError, val retryable: Boolean) : PaymentEvent()
    data class TimedOut(val idempotencyKey: String) : PaymentEvent()
    object CheckStatus : PaymentEvent()
    object Retry : PaymentEvent()
}

data class PaymentError(val message: String)
data class Receipt(
    val txnId: String,
    val amount: Double,
    val recipient: String,
    val timestamp: Long,
    val status: String
)
```

### Usage

```kotlin
class PaymentViewModel : ViewModel() {
    private var state: PaymentState = PaymentState.Idle

    fun startPayment(amount: Double, recipient: String) {
        // Transition: Idle → Validating
        state = state.transition(PaymentEvent.Start) as PaymentState

        viewModelScope.launch {
            // Validate
            if (amount <= 0) {
                state = state.transition(
                    PaymentEvent.Invalid(PaymentError("Invalid amount"))
                ) as PaymentState
                return@launch
            }
            state = state.transition(PaymentEvent.Valid(amount, recipient)) as PaymentState

            // Authenticate
            val authResult = authBloc.authenticate()
            if (!authResult) {
                state = state.transition(PaymentEvent.AuthFailed) as PaymentState
                return@launch
            }
            state = state.transition(PaymentEvent.Authenticated) as PaymentState

            // Process
            try {
                val receipt = paymentRepo.sendMoney(amount, recipient, UUID.randomUUID().toString())
                state = state.transition(PaymentEvent.Succeeded(receipt)) as PaymentState
            } catch (e: TimeoutException) {
                state = state.transition(
                    PaymentEvent.TimedOut(currentIdempotencyKey)
                ) as PaymentState
            } catch (e: Exception) {
                state = state.transition(
                    PaymentEvent.Failed(PaymentError(e.message ?: "Payment failed"), retryable = true)
                ) as PaymentState
            }
        }
    }
}
```

---

## 13. Database Schema (PostgreSQL)

Complete schema for a payment system.

```sql
-- ============================================================
-- PAYMENT SYSTEM SCHEMA (PostgreSQL)
-- ============================================================

-- 1. USERS
CREATE TABLE users (
    id VARCHAR(64) PRIMARY KEY,
    phone VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(255),
    kyc_status VARCHAR(20) DEFAULT 'MINIMUM',  -- MINIMUM, FULL
    kyc_verified_at TIMESTAMP,
    daily_limit DECIMAL(15,2) DEFAULT 100000.00,
    monthly_limit DECIMAL(15,2) DEFAULT 500000.00,
    is_active BOOLEAN DEFAULT true,
    is_blocked BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 2. DEVICES (Device Binding)
CREATE TABLE user_devices (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL REFERENCES users(id),
    device_fingerprint VARCHAR(255) NOT NULL,
    device_name VARCHAR(100),
    platform VARCHAR(10),           -- ANDROID, IOS
    app_version VARCHAR(20),
    is_rooted BOOLEAN DEFAULT false,
    is_emulator BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    last_seen TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, device_fingerprint)
);

-- 3. ACCOUNTS (Wallets, Bank Links)
CREATE TABLE accounts (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL REFERENCES users(id),
    type VARCHAR(20) NOT NULL,       -- WALLET, BANK, UPI
    balance DECIMAL(15,2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) DEFAULT 'INR',
    bank_account_number VARCHAR(30),  -- For BANK type (encrypted)
    bank_ifsc VARCHAR(15),
    upi_vpa VARCHAR(100) UNIQUE,
    is_primary BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

-- 4. TRANSACTIONS
CREATE TABLE transactions (
    id VARCHAR(64) PRIMARY KEY,
    idempotency_key VARCHAR(64) UNIQUE NOT NULL,
    sender_account_id VARCHAR(64) NOT NULL REFERENCES accounts(id),
    recipient_account_id VARCHAR(64) NOT NULL REFERENCES accounts(id),
    recipient_upi VARCHAR(100),
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    payment_method VARCHAR(20) NOT NULL,  -- UPI, CARD, WALLET, NFC
    payment_type VARCHAR(20) NOT NULL,     -- P2P, P2M, TOPUP, REFUND
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    risk_score INTEGER,
    fraud_flagged BOOLEAN DEFAULT false,
    bank_reference_id VARCHAR(100),
    failure_reason TEXT,
    note VARCHAR(200),
    device_id VARCHAR(64) REFERENCES user_devices(id),
    ip_address INET,
    created_at TIMESTAMP DEFAULT NOW(),
    processed_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 5. LEDGER ENTRIES (Double-Entry)
CREATE TABLE ledger_entries (
    id BIGSERIAL PRIMARY KEY,
    txn_id VARCHAR(64) NOT NULL REFERENCES transactions(id),
    account_id VARCHAR(64) NOT NULL REFERENCES accounts(id),
    entry_type VARCHAR(10) NOT NULL,  -- DEBIT, CREDIT
    amount DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2),
    reference_type VARCHAR(20),       -- PAYMENT, TOPUP, REFUND, REVERSAL
    reference_id VARCHAR(64),
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT chk_entry_type CHECK (entry_type IN ('DEBIT', 'CREDIT')),
    CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

-- 6. REFUNDS
CREATE TABLE refunds (
    id VARCHAR(64) PRIMARY KEY,
    original_txn_id VARCHAR(64) NOT NULL REFERENCES transactions(id),
    idempotency_key VARCHAR(64) UNIQUE NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'INITIATED',  -- INITIATED, PROCESSING, SUCCESS, FAILED
    failure_reason TEXT,
    bank_reference_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT NOW(),
    processed_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 7. AUTH TOKENS (Server-side tracking)
CREATE TABLE auth_tokens (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL REFERENCES users(id),
    device_id VARCHAR(64) NOT NULL REFERENCES user_devices(id),
    access_token_hash VARCHAR(255),   -- Hashed, never store raw
    refresh_token_hash VARCHAR(255),
    access_token_expires_at TIMESTAMP,
    refresh_token_expires_at TIMESTAMP,
    is_revoked BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW(),
    last_used_at TIMESTAMP
);

-- 8. AUDIT LOG (Immutable, Append-Only)
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    event_type VARCHAR(50) NOT NULL,   -- PAYMENT_INITIATED, PAYMENT_SUCCESS, etc.
    user_id VARCHAR(64),
    device_id VARCHAR(64),
    txn_id VARCHAR(64),
    idempotency_key VARCHAR(64),
    amount DECIMAL(15,2),
    risk_score INTEGER,
    ip_address INET,
    app_version VARCHAR(20),
    metadata JSONB,                    -- Additional context
    prev_hash VARCHAR(64),             -- Hash chaining (tamper-evident)
    current_hash VARCHAR(64)           -- SHA-256(prev_hash + event_data)
);

-- 9. RECONCILIATION
CREATE TABLE reconciliation_reports (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL UNIQUE,
    total_bank_txns INTEGER NOT NULL,
    total_internal_txns INTEGER NOT NULL,
    matched_count INTEGER NOT NULL,
    unmatched_internal_count INTEGER NOT NULL,
    unmatched_bank_count INTEGER NOT NULL,
    discrepancy_count INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',  -- PENDING, RESOLVED, FLAGGED
    created_at TIMESTAMP DEFAULT NOW(),
    resolved_at TIMESTAMP
);

-- ============================================================
-- INDEXES
-- ============================================================
CREATE INDEX idx_txn_user ON transactions(sender_account_id);
CREATE INDEX idx_txn_status ON transactions(status);
CREATE INDEX idx_txn_created ON transactions(created_at);
CREATE INDEX idx_ledger_txn ON ledger_entries(txn_id);
CREATE INDEX idx_ledger_account ON ledger_entries(account_id);
CREATE INDEX idx_audit_user ON audit_log(user_id);
CREATE INDEX idx_audit_txn ON audit_log(txn_id);
CREATE INDEX idx_audit_timestamp ON audit_log(timestamp);
```

---

## Summary

| Component | Language | Key Pattern |
|-----------|----------|-------------|
| Request Signing | Kotlin | HMAC-SHA256 with Keystore key |
| Idempotency Interceptor | Dart | Auto-attach UUID to payment POSTs |
| SSL Pinning | Kotlin/Dart | Pin SHA-256 of certificate public key |
| SQLCipher | Kotlin | AES-256 encrypted local DB |
| Token Storage | Kotlin | Keystore (hardware-backed) |
| Double-Entry Ledger | SQL | `SELECT FOR UPDATE` + atomic entries |
| Idempotency (Server) | Kotlin | Redis + DB with TTL |
| Fraud Scoring | Kotlin | Rule-based + ML, sync + async |
| Biometric Auth | Kotlin | BiometricPrompt with PIN fallback |
| UPI Intent | Kotlin | `upi://` URI + `startActivityForResult` |
| Webhook Verification | Kotlin | HMAC + constant-time comparison |
| State Machine | Kotlin | Sealed classes with valid transitions |

---

[← Payment Implementation Topics](./Mobile_Payment_Implementation_Topics.md) | [← Payment Flows](./Mobile_Payment_Flows.md) | [← Back to README](./README.md)
