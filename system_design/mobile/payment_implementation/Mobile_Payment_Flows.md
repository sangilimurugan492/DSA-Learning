# Mobile Payment Implementation — End-to-End Flows

> Detailed sequence diagrams, step-by-step flows, and code examples for every critical payment scenario.

---

## Table of Contents

1. [UPI P2P Payment Flow](#1-upi-p2p-payment-flow)
2. [QR Scan & Pay Flow (Merchant)](#2-qr-scan--pay-flow-merchant)
3. [Wallet Top-Up Flow](#3-wallet-top-up-flow)
4. [Payment Timeout & Recovery Flow](#4-payment-timeout--recovery-flow)
5. [Refund Flow](#5-refund-flow)
6. [Token Refresh During Payment](#6-token-refresh-during-payment)
7. [Biometric Auth with PIN Fallback](#7-biometric-auth-with-pin-fallback)
8. [Webhook Processing Flow (Server)](#8-webhook-processing-flow-server)
9. [Reconciliation Flow (Server)](#9-reconciliation-flow-server)
10. [Offline → Online Sync Flow](#10-offline--online-sync-flow)

---

## 1. UPI P2P Payment Flow

### 1.1 Sequence Diagram

```
┌───────┐         ┌───────────┐        ┌──────────┐        ┌───────┐        ┌───────┐
│ User  │         │ Mobile App│        │  Server  │        │ Fraud │        │ Bank/ │
│       │         │ (BLoC)    │        │ (API)    │        │ Check │        │ NPCI  │
└───┬───┘         └─────┬─────┘        └────┬─────┘        └───┬───┘        └───┬───┘
    │                   │                   │                  │                │
    │ 1. Enter amount   │                   │                  │                │
    │   + recipient     │                   │                  │                │
    ├──────────────────►│                   │                  │                │
    │                   │                   │                  │                │
    │                   │ 2. Request biometric auth             │                │
    │   ◄───────────────┤                   │                  │                │
    │ 3. Biometric OK   │                   │                  │                │
    ├──────────────────►│                   │                  │                │
    │                   │                   │                  │                │
    │                   │ 4. Generate idempotency key (UUID)    │                │
    │                   │                   │                  │                │
    │                   │ 5. POST /api/payments                │                │
    │                   │   Headers:                           │                │
    │                   │     Idempotency-Key: <UUID>          │                │
    │                   │     Authorization: Bearer <token>     │                │
    │                   │     X-Request-Signature: <HMAC>      │                │
    │                   │   Body: {amount, recipient, method}  │                │
    │                   ├──────────────────►│                  │                │
    │                   │                   │                  │                │
    │                   │                   │ 6. Validate input │                │
    │                   │                   │   + check balance│                │
    │                   │                   │   (SELECT FOR UPDATE)             │
    │                   │                   │                  │                │
    │                   │                   │ 7. Fraud check ──►                │
    │                   │                   │   (sync, ~50ms)  │                │
    │                   │                   │   ◄── risk score  │                │
    │                   │                   │                  │                │
    │                   │                   │ 8. Initiate UPI  │                │
    │                   │                   ├─────────────────────────────────►│
    │                   │                   │                  │   NPCI routes  │
    │                   │                   │                  │   to banks     │
    │                   │                   │                  │                │
    │                   │                   │                  │   9. Debit     │
    │                   │                   │                  │   sender bank  │
    │                   │                   │                  │                │
    │                   │                   │                  │   10. Credit   │
    │                   │                   │                  │   recipient    │
    │                   │                   │                  │   bank        │
    │                   │                   │                  │                │
    │                   │                   │  ◄───────────────────────────────┤
    │                   │                   │  11. Bank confirms SUCCESS        │
    │                   │                   │                  │                │
    │                   │                   │ 12. Write ledger entries          │
    │                   │                   │   (DEBIT sender, CREDIT receiver) │
    │                   │                   │                  │                │
    │                   │                   │ 13. Store idempotency result      │
    │                   │                   │   (Redis: key → {status: SUCCESS}) │
    │                   │                   │                  │                │
    │                   │                   │ 14. Publish event to Kafka        │
    │                   │                   │   → Notification service          │
    │                   │                   │   → Analytics                     │
    │                   │                   │   → Audit log                     │
    │                   │                   │                  │                │
    │                   │  ◄────────────────┤  15. Return {     │                │
    │                   │                    │    txn_id,        │                │
    │                   │                    │    status: SUCCESS,               │
    │                   │                    │    receipt }      │                │
    │                   │                   │                  │                │
    │                   │ 16. Save receipt to local DB (encrypted)               │
    │                   │                   │                  │                │
    │                   │ 17. Update wallet balance (local cache)                │
    │                   │                   │                  │                │
    │ 18. Show success  │                   │                  │                │
    │   + receipt       │                   │                  │                │
    │   ◄───────────────┤                   │                  │                │
    │                   │                   │                  │                │
    │                   │ 19. FCM push to recipient: "₹500 received"             │
    │                   │                   │                  │                │
```

### 1.2 Kotlin Code — PaymentBloc (Flutter/Dart equivalent shown as Kotlin for readability)

```kotlin
// PaymentBloc.kt — Core payment state machine

sealed class PaymentState {
    object Idle : PaymentState()
    object Authenticating : PaymentState()
    object Processing : PaymentState()
    data class Success(val receipt: Receipt) : PaymentState()
    data class Failed(val error: PaymentError) : PaymentState()
    data class Timeout(val idempotencyKey: String) : PaymentState()
}

sealed class PaymentEvent {
    data class SendMoney(val amount: Double, val recipientUpi: String) : PaymentEvent()
    data class Retry(val idempotencyKey: String) : PaymentEvent()
    object CheckStatus : PaymentEvent()
}

class PaymentBloc(
    private val authBloc: AuthBloc,
    private val paymentRepository: PaymentRepository,
    private val receiptDao: ReceiptDao
) {

    private var currentIdempotencyKey: String? = null

    suspend fun handleEvent(event: PaymentEvent): PaymentState {
        return when (event) {
            is PaymentEvent.SendMoney -> sendMoney(event.amount, event.recipientUpi)
            is PaymentEvent.Retry -> retry(event.idempotencyKey)
            PaymentEvent.CheckStatus -> checkStatus()
        }
    }

    private suspend fun sendMoney(amount: Double, recipientUpi: String): PaymentState {
        // Step 1: Validate amount
        if (amount <= 0) return PaymentState.Failed(PaymentError("Invalid amount"))
        if (amount > DAILY_LIMIT) return PaymentState.Failed(PaymentError("Exceeds daily limit"))

        // Step 2: Biometric authentication (REQUIRED before every payment)
        val authResult = authBloc.requestBiometricAuth("Confirm payment of ₹$amount")
        if (authResult !is AuthState.Authenticated) {
            return PaymentState.Failed(PaymentError("Authentication failed"))
        }

        // Step 3: Generate idempotency key (BEFORE sending request)
        currentIdempotencyKey = UUID.randomUUID().toString()

        // Step 4: Send payment request
        return try {
            val response = paymentRepository.sendMoney(
                amount = amount,
                recipientUpi = recipientUpi,
                idempotencyKey = currentIdempotencyKey!!
            )
            // Step 5: Save receipt locally (encrypted)
            receiptDao.insert(response.receipt.toEntity())
            PaymentState.Success(response.receipt)
        } catch (e: TimeoutException) {
            // CRITICAL: Do NOT auto-retry. Show "check status" state.
            PaymentState.Timeout(currentIdempotencyKey!!)
        } catch (e: Exception) {
            PaymentState.Failed(PaymentError(e.message ?: "Payment failed"))
        }
    }

    private suspend fun retry(idempotencyKey: String): PaymentState {
        // Before retrying: CHECK STATUS FIRST
        return try {
            val status = paymentRepository.checkStatus(idempotencyKey)
            when (status) {
                TransactionStatus.SUCCESS -> {
                    // Already processed — show receipt
                    val receipt = paymentRepository.getReceipt(idempotencyKey)
                    PaymentState.Success(receipt)
                }
                TransactionStatus.PENDING -> {
                    // Still processing — don't retry, just wait
                    PaymentState.Processing
                }
                TransactionStatus.FAILED -> {
                    // Safe to retry with SAME idempotency key
                    currentIdempotencyKey = idempotencyKey
                    val response = paymentRepository.sendMoney(
                        amount = /* original amount */,
                        recipientUpi = /* original recipient */,
                        idempotencyKey = idempotencyKey
                    )
                    PaymentState.Success(response.receipt)
                }
            }
        } catch (e: Exception) {
            PaymentState.Failed(PaymentError("Status check failed"))
        }
    }

    private suspend fun checkStatus(): PaymentState {
        val key = currentIdempotencyKey ?: return PaymentState.Failed(PaymentError("No transaction"))
        return try {
            val status = paymentRepository.checkStatus(key)
            when (status) {
                TransactionStatus.SUCCESS -> {
                    val receipt = paymentRepository.getReceipt(key)
                    PaymentState.Success(receipt)
                }
                TransactionStatus.PENDING -> PaymentState.Processing
                TransactionStatus.FAILED -> PaymentState.Failed(PaymentError("Transaction failed"))
            }
        } catch (e: Exception) {
            PaymentState.Timeout(key)
        }
    }

    companion object {
        private const val DAILY_LIMIT = 100_000.0 // ₹1,00,000
    }
}
```

### 1.3 API Request Example

```kotlin
// PaymentApi.kt — Retrofit interface

interface PaymentApi {

    @POST("api/payments")
    suspend fun sendPayment(
        @Header("Idempotency-Key") idempotencyKey: String,
        @Header("Authorization") authToken: String,
        @Header("X-Request-Signature") signature: String,
        @Header("X-Device-ID") deviceId: String,
        @Body request: PaymentRequest
    ): Response<PaymentResponse>

    @GET("api/payments/{idempotencyKey}/status")
    suspend fun checkStatus(
        @Path("idempotencyKey") key: String,
        @Header("Authorization") authToken: String
    ): Response<TransactionStatusResponse>

    @GET("api/transactions")
    suspend fun getTransactions(
        @Query("cursor") cursor: String?,
        @Header("Authorization") authToken: String
    ): Response<PaginatedTransactions>
}

data class PaymentRequest(
    val amount: Double,
    val recipient_upi: String,
    val payment_method: String = "UPI",
    val note: String? = null
)

data class PaymentResponse(
    val txn_id: String,
    val status: String,        // SUCCESS, PENDING, FAILED
    val receipt: ReceiptDto
)
```

---

## 2. QR Scan & Pay Flow (Merchant)

### 2.1 Sequence Diagram

```
┌───────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌───────┐
│ User  │    │ QR Scanner│   │  App     │    │  Server  │    │ Bank/ │
│       │    │ (Camera)  │    │ (BLoC)   │    │  (API)   │    │ NPCI  │
└───┬───┘    └─────┬────┘    └────┬─────┘    └────┬─────┘    └───┬───┘
    │              │              │               │              │
    │ 1. Open camera│              │               │              │
    ├─────────────►│              │               │              │
    │              │               │               │              │
    │              │ 2. ML Kit detects QR          │              │
    │              │   Raw: "upi://pay?pa=merchant@upi&am=100"  │
    │              │              │               │              │
    │              │ 3. Parse QR  │               │              │
    │              ├─────────────►│               │              │
    │              │              │               │              │
    │              │              │ 4. Validate VPA format       │
    │              │              │   regex: ^[a-z0-9._-]+@[a-z]+$│
    │              │              │               │              │
    │              │              │ 5. Verify merchant VPA       │
    │              │              │   (GET /api/merchants/verify)│
    │              │              ├──────────────►│              │
    │              │              │  ◄────────────┤ {valid: true}│
    │              │              │               │              │
    │ 6. Show payment screen      │               │              │
    │   (VPA + amount pre-filled) │               │              │
    │   ◄────────────────────────┤               │              │
    │              │              │               │              │
    │ 7. Confirm payment          │               │              │
    ├─────────────────────────────►               │              │
    │              │              │               │              │
    │              │              │ 8. Biometric auth            │
    │   ◄─────────────────────────┤               │              │
    │ 9. Biometric OK             │               │              │
    ├─────────────────────────────►               │              │
    │              │              │               │              │
    │              │              │ 10. Generate idempotency key │
    │              │              │ 11. POST /api/payments       │
    │              │              ├──────────────►│─────────────►│
    │              │              │               │  12. Process  │
    │              │              │               │  (same as P2P)│
    │              │              │  ◄────────────┤  ◄───────────┤
    │              │              │  13. {status: SUCCESS}        │
    │              │              │               │              │
    │ 14. Show "Payment Successful"               │              │
    │   + merchant name + amount  │               │              │
    │   ◄─────────────────────────┤               │              │
```

### 2.2 QR Parsing Code

```kotlin
// QrParser.kt

data class UpiQrData(
    val payeeAddress: String,      // pa
    val payeeName: String,         // pn
    val amount: Double?,           // am (nullable — user enters if missing)
    val currency: String,         // cu
    val transactionNote: String?, // tn
    val transactionRef: String?,  // tr
    val merchantCode: String?     // mc
)

object QrParser {

    private val UPI_VPA_REGEX = Regex("^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$")

    fun parse(rawQr: String): Result<UpiQrData> {
        return when {
            rawQr.startsWith("upi://") -> parseUpiQr(rawQr)
            rawQr.startsWith("000201") -> parseEmvQr(rawQr) // EMVCo format
            else -> Result.failure(IllegalArgumentException("Unsupported QR format"))
        }
    }

    private fun parseUpiQr(uri: String): Result<UpiQrData> {
        val params = uri.removePrefix("upi://pay?").split("&")
            .associate {
                val (key, value) = it.split("=", limit = 2)
                key to value
            }

        val vpa = params["pa"] ?: return Result.failure(
            IllegalArgumentException("Missing payee address (pa)")
        )

        if (!UPI_VPA_REGEX.matches(vpa)) {
            return Result.failure(IllegalArgumentException("Invalid VPA format"))
        }

        val amount = params["am"]?.toDoubleOrNull()
        if (amount != null && amount <= 0) {
            return Result.failure(IllegalArgumentException("Invalid amount in QR"))
        }

        return Result.success(
            UpiQrData(
                payeeAddress = vpa,
                payeeName = params["pn"] ?: "",
                amount = amount,
                currency = params["cu"] ?: "INR",
                transactionNote = params["tn"],
                transactionRef = params["tr"],
                merchantCode = params["mc"]
            )
        )
    }

    private fun parseEmvQr(raw: String): Result<UpiQrData> {
        // EMVCo mQR parsing — TLV format
        // Implementation depends on spec version
        return Result.failure(IllegalArgumentException("EMVCo QR parsing not implemented"))
    }
}
```

### 2.3 QR Scanner Widget (Flutter/Dart)

```dart
// qr_scanner_page.dart
class QrScannerPage extends StatefulWidget {
  @override
  State<QrScannerPage> createState() => _QrScannerPageState();
}

class _QrScannerPageState extends State<QrScannerPage> {
  MobileScannerController cameraController = MobileScannerController();
  bool _isProcessing = false;

  @override
  void dispose() {
    // CRITICAL: Release camera immediately to free resources
    cameraController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Scan QR Code')),
      body: MobileScanner(
        controller: cameraController,
        onDetect: (capture) {
          if (_isProcessing) return; // Prevent multiple scans
          final List<Barcode> barcodes = capture.barcodes;
          if (barcodes.isNotEmpty) {
            _isProcessing = true;
            final rawQr = barcodes.first.rawValue;
            _handleQrScan(rawQr);
          }
        },
      ),
    );
  }

  void _handleQrScan(String? rawQr) {
    if (rawQr == null) {
      _showError('Invalid QR code');
      return;
    }

    final result = QrParser.parse(rawQr);
    result.fold(
      onSuccess: (qrData) {
        // Stop camera before navigating
        cameraController.stop();
        // Navigate to payment confirmation screen
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(
            builder: (_) => PaymentConfirmationPage(qrData: qrData),
          ),
        );
      },
      onFailure: (error) => _showError(error.message),
    );
  }
}
```

---

## 3. Wallet Top-Up Flow

### 3.1 Sequence Diagram

```
┌───────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ User  │    │   App    │    │  Server  │    │   Bank   │
└───┬───┘    └─────┬────┘    └────┬─────┘    └─────┬────┘
    │              │              │                │
    │ 1. Enter top-up amount      │                │
    │   (₹1000)   │              │                │
    ├─────────────►│              │                │
    │              │              │                │
    │              │ 2. Biometric auth              │
    │  ◄───────────┤              │                │
    │ 3. Auth OK   │              │                │
    ├─────────────►│              │                │
    │              │              │                │
    │              │ 4. POST /api/wallet/topup    │
    │              │   {amount: 1000, source: "BANK"}│
    │              ├─────────────►│                │
    │              │              │                │
    │              │              │ 5. Initiate    │
    │              │              │   debit from   │
    │              │              │   bank account│
    │              │              ├───────────────►│
    │              │              │                │
    │              │              │ 6. Bank debits │
    │              │              │   ₹1000 from  │
    │              │              │   linked bank  │
    │              │              │  ◄─────────────┤
    │              │              │                │
    │              │              │ 7. Credit wallet│
    │              │              │   (ledger entry)│
    │              │              │   CREDIT wallet 5000→6000│
    │              │              │                │
    │              │  ◄───────────┤ 8. Return {   │
    │              │              │   status: SUCCESS,│
    │              │              │   new_balance: 6000}│
    │              │              │                │
    │              │ 9. Update local cache         │
    │              │   balance = 6000              │
    │              │              │                │
    │ 10. Show new balance        │                │
    │   "₹1000 added"            │                │
    │   ◄───────────┤              │                │
```

### 3.2 Ledger Entries for Top-Up

```
transaction_id | account_id      | entry_type | amount  | balance_after
----------------|-----------------|------------|---------|-------------
TOPUP_001       | USER_BANK_ACCT  | DEBIT      | 1000.00 | 4000.00
TOPUP_001       | USER_WALLET     | CREDIT     | 1000.00 | 6000.00
```

> Note: Bank debit is external (bank's ledger). Internal ledger only records the wallet credit and a corresponding "bank settlement" debit account.

---

## 4. Payment Timeout & Recovery Flow

> **This is the most critical flow to get right.** Mishandling timeouts causes double charges.

### 4.1 Decision Tree

```
Payment request sent
        │
        ▼
   Response received?
   ┌────┴────┐
   YES       NO (timeout)
   │         │
   ▼         ▼
 Handle   GET /payments/{idempotencyKey}/status
 response         │
                  ▼
            Status received?
            ┌────┴────┐
            YES       NO (server unreachable)
            │         │
            ▼         ▼
         Status?   Show "Transaction status unknown.
         ┌──┴──┐    Check transaction history later."
         │     │    Save idempotency key locally.
         ▼     ▼    On next app launch: auto-check status.
      PENDING  SUCCESS/FAILED
         │     │
         ▼     ▼
      Show  Handle accordingly
      "Processing..."
      (poll 3x,
       5s interval)
```

### 4.2 Timeout Handling Code

```kotlin
// PaymentRepository.kt

class PaymentRepository(
    private val api: PaymentApi,
    private val pendingTxnDao: PendingTxnDao
) {

    suspend fun sendMoney(
        amount: Double,
        recipientUpi: String,
        idempotencyKey: String
    ): PaymentResult {
        // Save pending transaction locally BEFORE sending
        // (so we can check status on app relaunch)
        pendingTxnDao.insert(
            PendingTxn(
                idempotencyKey = idempotencyKey,
                amount = amount,
                recipient = recipientUpi,
                timestamp = System.currentTimeMillis(),
                status = "PENDING"
            )
        )

        return try {
            val response = api.sendPayment(
                idempotencyKey = idempotencyKey,
                authToken = getAuthToken(),
                signature = generateSignature(amount, recipientUpi),
                deviceId = getDeviceId(),
                request = PaymentRequest(amount, recipientUpi)
            )

            // Update pending txn status
            pendingTxnDao.updateStatus(idempotencyKey, response.status)

            when (response.status) {
                "SUCCESS" -> {
                    pendingTxnDao.delete(idempotencyKey) // Clean up
                    PaymentResult.Success(response.receipt)
                }
                "PENDING" -> PaymentResult.Pending(response.txnId)
                "FAILED" -> {
                    pendingTxnDao.delete(idempotencyKey)
                    PaymentResult.Failed(response.errorMessage)
                }
                else -> PaymentResult.Unknown
            }
        } catch (e: TimeoutException) {
            // CRITICAL: Do NOT retry. Return Timeout state.
            // Pending txn remains in local DB for status check.
            PaymentResult.Timeout(idempotencyKey)
        } catch (e: IOException) {
            // Network error — same as timeout
            PaymentResult.Timeout(idempotencyKey)
        }
    }

    suspend fun checkStatus(idempotencyKey: String): TransactionStatus {
        return try {
            val response = api.checkStatus(idempotencyKey, getAuthToken())
            when (response.status) {
                "SUCCESS" -> {
                    pendingTxnDao.delete(idempotencyKey)
                    TransactionStatus.SUCCESS
                }
                "PENDING" -> TransactionStatus.PENDING
                "FAILED" -> {
                    pendingTxnDao.delete(idempotencyKey)
                    TransactionStatus.FAILED
                }
                else -> TransactionStatus.UNKNOWN
            }
        } catch (e: Exception) {
            TransactionStatus.UNKNOWN
        }
    }

    // Called on app launch to check any pending transactions
    suspend fun checkPendingTransactions() {
        val pendingTxns = pendingTxnDao.getAllPending()
        for (txn in pendingTxns) {
            // If pending for > 1 hour, check status
            if (System.currentTimeMillis() - txn.timestamp > 3600_000) {
                checkStatus(txn.idempotencyKey)
            }
        }
    }
}

sealed class PaymentResult {
    data class Success(val receipt: Receipt) : PaymentResult()
    data class Pending(val txnId: String) : PaymentResult()
    data class Failed(val error: String) : PaymentResult()
    data class Timeout(val idempotencyKey: String) : PaymentResult()
    object Unknown : PaymentResult()
}
```

### 4.3 UI States for Timeout

```
┌─────────────────────────────────┐
│  ⏳                             │
│  Transaction status unknown     │
│                                 │
│  Your payment of ₹500 to       │
│  john@upi is being verified.   │
│                                 │
│  We'll update you once the      │
│  status is confirmed.           │
│                                 │
│  [Check Status]  [View History] │
└─────────────────────────────────┘
```

> **Never show "Payment Failed" on timeout.** The payment may have succeeded — the response was just lost.

---

## 5. Refund Flow

### 5.1 Sequence Diagram

```
┌───────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ User  │    │   App    │    │  Server  │    │   Bank   │
└───┬───┘    └─────┬────┘    └────┬─────┘    └─────┬────┘
    │              │              │                │
    │ 1. Select txn to refund    │                │
    ├─────────────►│              │                │
    │              │              │                │
    │              │ 2. POST /api/refunds         │
    │              │   {original_txn_id, amount} │
    │              ├─────────────►│                │
    │              │              │                │
    │              │              │ 3. Verify:     │
    │              │              │   - Original txn is SUCCESS │
    │              │              │   - Refund amount ≤ original│
    │              │              │   - Not already fully refunded│
    │              │              │                │
    │              │              │ 4. Create refund record │
    │              │              │   refund_id = RFD_001    │
    │              │              │   linked to TXN_001     │
    │              │              │                │
    │              │              │ 5. Reverse ledger entries│
    │              │              │   (new transaction)      │
    │              │              │   DEBIT recipient (reverse original credit)│
    │              │              │   CREDIT sender (reverse original debit)│
    │              │              │                │
    │              │              │ 6. Initiate bank refund  │
    │              │              ├───────────────►│
    │              │              │                │
    │              │              │  ◄─────────────┤ 7. Bank processes
    │              │              │                │   (T+1 to T+3)
    │              │              │                │
    │              │  ◄───────────┤ 8. Return {   │
    │              │              │   refund_id,  │
    │              │              │   status: INITIATED│
    │              │              │   eta: "3 business days"│
    │              │              │                │
    │ 9. Show "Refund initiated"  │                │
    │   + ETA                     │                │
    │   ◄───────────┤              │                │
```

### 5.2 Refund Ledger Entries

```
Original Payment (TXN_001):
  DEBIT  USER_A     500.00   (balance: 4500.00)
  CREDIT USER_B     500.00   (balance: 1500.00)

Refund (RFD_001, linked to TXN_001):
  CREDIT USER_A     500.00   (balance: 5000.00)  ← reversed
  DEBIT  USER_B     500.00   (balance: 1000.00)  ← reversed
```

### 5.3 Refund Validation Code

```kotlin
// RefundService.kt (Server-side)

class RefundService(
    private val transactionRepo: TransactionRepository,
    private val ledgerRepo: LedgerRepository
) {
    suspend fun processRefund(
        originalTxnId: String,
        refundAmount: Double,
        idempotencyKey: String
    ): RefundResult {

        // 1. Fetch original transaction
        val originalTxn = transactionRepo.findById(originalTxnId)
            ?: return RefundResult.Failed("Original transaction not found")

        // 2. Original must be SUCCESS
        if (originalTxn.status != "SUCCESS") {
            return RefundResult.Failed("Can only refund successful transactions")
        }

        // 3. Check idempotency (already processed?)
        ledgerRepo.findByIdempotencyKey(idempotencyKey)?.let {
            return RefundResult.AlreadyProcessed(it.result)
        }

        // 4. Calculate total already refunded
        val totalRefunded = transactionRepo.getTotalRefundedForTxn(originalTxnId)
        val remaining = originalTxn.amount - totalRefunded

        if (refundAmount > remaining) {
            return RefundResult.Failed(
                "Refund amount ₹$refundAmount exceeds refundable amount ₹$remaining"
            )
        }

        // 5. Process refund atomically
        return ledgerRepo.executeInTransaction {
            // Reverse original entries
            ledgerRepo.insert(
                LedgerEntry(
                    txnId = "RFD_$idempotencyKey",
                    accountId = originalTxn.senderId,
                    entryType = "CREDIT",  // Reverse original DEBIT
                    amount = refundAmount
                )
            )
            ledgerRepo.insert(
                LedgerEntry(
                    txnId = "RFD_$idempotencyKey",
                    accountId = originalTxn.recipientId,
                    entryType = "DEBIT",  // Reverse original CREDIT
                    amount = refundAmount
                )
            )

            // Update balances
            accountRepo.adjustBalance(originalTxn.senderId, +refundAmount)
            accountRepo.adjustBalance(originalTxn.recipientId, -refundAmount)

            // Store idempotency result
            ledgerRepo.storeIdempotencyResult(idempotencyKey, "SUCCESS")

            RefundResult.Success("RFD_$idempotencyKey", refundAmount)
        }
    }
}
```

---

## 6. Token Refresh During Payment

### 6.1 Scenario

User initiates payment → access token expired (401) → must refresh → retry payment with **same idempotency key**.

### 6.2 Flow

```
1. POST /api/payments (Idempotency-Key: ABC-123)
   → 401 Unauthorized (token expired)

2. RefreshTokenInterceptor intercepts 401
   → POST /api/auth/refresh (with refresh_token)
   → 200 OK → new access_token
   → Store new token in Keystore

3. Retry original request:
   POST /api/payments (Idempotency-Key: ABC-123)  ← SAME KEY!
   → 200 OK → SUCCESS

4. If refresh also fails (refresh token expired):
   → Force logout
   → Clear tokens
   → Show "Session expired, please login again"
   → Payment NOT processed (safe — idempotency key never reached server)
```

### 6.3 Interceptor Code (Dart/Dio)

```dart
// refresh_token_interceptor.dart

class RefreshTokenInterceptor extends Interceptor {
  final TokenStorage tokenStorage;
  final AuthApi authApi;
  final Dio dio;

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) async {
    if (err.response?.statusCode != 401) {
      return handler.next(err);
    }

    // Don't retry if the failing request IS the refresh endpoint
    if (err.requestOptions.path == '/api/auth/refresh') {
      await tokenStorage.clearTokens();
      return handler.next(err); // Will trigger logout in auth bloc
    }

    // Try to refresh the token
    final refreshResult = await authApi.refreshToken(
      refreshToken: await tokenStorage.getRefreshToken(),
    );

    if (refreshResult.isSuccess) {
      // Save new tokens
      await tokenStorage.saveAccessToken(refreshResult.accessToken!);
      await tokenStorage.saveRefreshToken(refreshResult.refreshToken!);

      // Retry original request with new token
      final opts = err.requestOptions;
      opts.headers['Authorization'] = 'Bearer ${refreshResult.accessToken}';

      // CRITICAL: Preserve Idempotency-Key header for payment retries
      // (it's already in opts.headers — don't touch it)

      final response = await dio.fetch(opts);
      return handler.resolve(response);
    } else {
      // Refresh failed — force logout
      await tokenStorage.clearTokens();
      return handler.next(err);
    }
  }
}
```

---

## 7. Biometric Auth with PIN Fallback

### 7.1 Decision Flow

```
User taps "Pay ₹500"
        │
        ▼
   Is biometric available?
   ┌────────┴────────┐
   YES                NO
   │                  │
   ▼                  ▼
   Prompt biometric   Show PIN entry
   (fingerprint/face) screen
   │                  │
   ┌──┴──┐            │
   OK    FAIL         │
   │     │            │
   │     ▼            │
   │  Retry?          │
   │  (max 3)         │
   │  ┌──┴──┐         │
   │  YES    NO        │
   │  │       │        │
   │  │        ▼        │
   │  │     Show PIN   │
   │  │     entry      │
   │  ▼      │         │
   │  Retry  │         │
   │  biometric        │
   │         │         │
   ▼         ▼         ▼
   PROCEED   PROCEED   PROCEED
                        │
                        ▼
                   POST /api/auth/verify-pin
                   (server-side verification)
                        │
                   ┌────┴────┐
                   OK         WRONG
                   │          │
                   ▼          ▼
                 PROCEED   Increment fail counter
                           ┌──────┴──────┐
                           < 5 attempts    ≥ 5 attempts
                           │               │
                           ▼               ▼
                         Show PIN       LOCK APP
                         again          (30 min cooldown)
```

### 7.2 AuthBloc Code

```kotlin
// AuthBloc.kt

class AuthBloc(
    private val biometricAuth: BiometricAuth,
    private val authApi: AuthApi,
    private val secureStorage: SecureStorage
) {
    private var pinFailCount = 0
    private var lockUntil: Long = 0

    suspend fun requestBiometricAuth(reason: String): AuthState {
        // Check if app is locked
        if (System.currentTimeMillis() < lockUntil) {
            val remaining = (lockUntil - System.currentTimeMillis()) / 60000
            return AuthState.Locked("App locked. Try again in $remaining min.")
        }

        // Try biometric first
        if (biometricAuth.isAvailable()) {
            return try {
                val result = biometricAuth.authenticate(reason, biometricsOnly = true)
                if (result) {
                    pinFailCount = 0
                    AuthState.Authenticated
                } else {
                    // Biometric failed — fall back to PIN
                    AuthState.PinRequired
                }
            } catch (e: Exception) {
                // Biometric error — fall back to PIN
                AuthState.PinRequired
            }
        } else {
            // No biometric — use PIN
            return AuthState.PinRequired
        }
    }

    suspend fun verifyPin(pin: String): AuthState {
        if (System.currentTimeMillis() < lockUntil) {
            return AuthState.Locked("App locked. Try again later.")
        }

        return try {
            val response = authApi.verifyPin(pin)
            if (response.isValid) {
                pinFailCount = 0
                AuthState.Authenticated
            } else {
                pinFailCount++
                if (pinFailCount >= 5) {
                    lockUntil = System.currentTimeMillis() + (30 * 60 * 1000) // 30 min
                    AuthState.Locked("Too many wrong attempts. Locked for 30 min.")
                } else {
                    AuthState.PinFailed("Wrong PIN. ${5 - pinFailCount} attempts left.")
                }
            }
        } catch (e: Exception) {
            AuthState.Error("PIN verification failed. Check network.")
        }
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object PinRequired : AuthState()
    data class PinFailed(val message: String) : AuthState()
    data class Locked(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
```

---

## 8. Webhook Processing Flow (Server)

### 8.1 Sequence Diagram

```
┌──────────┐         ┌──────────────┐         ┌──────────────┐
│ Payment  │         │  Your Server │         │  Kafka       │
│ Gateway  │         │  (Webhook    │         │  (Events)    │
│ (Stripe/ │         │   Handler)   │         │              │
│ Razorpay)│         └──────┬───────┘         └──────┬───────┘
└─────┬────┘                │                        │
      │                     │                        │
      │ 1. POST /webhooks/payments                   │
      │   Headers:                                   │
      │     X-Signature: <HMAC>                      │
      │   Body: {event: "payment.success", txn_id}  │
      ├────────────────────►│                        │
      │                     │                        │
      │                     │ 2. Verify signature     │
      │                     │   HMAC(webhook_secret,  │
      │                     │         raw_body)       │
      │                     │   == X-Signature?       │
      │                     │                        │
      │                     │ 3. Check idempotency    │
      │                     │   (already processed?)  │
      │                     │   Redis: webhook:{txn_id}│
      │                     │                        │
      │  ◄───────────────────┤ 4. 200 OK (acknowledge)│
      │                     │   (must respond fast!)  │
      │                     │                        │
      │                     │ 5. Parse event          │
      │                     │   payment.success →    │
      │                     │   update txn status     │
      │                     │                        │
      │                     │ 6. Publish to Kafka ────►
      │                     │   topic: payment.events │
      │                     │   key: txn_id           │
      │                     │                        │
      │                     │                        │ 7. Consumers:
      │                     │                        │   → Notification: send FCM
      │                     │                        │   → Analytics: update metrics
      │                     │                        │   → Audit: append to log
      │                     │                        │   → Reconciliation: match
```

### 8.2 Webhook Handler Code (Kotlin/Ktor)

```kotlin
// WebhookHandler.kt

routing {
    post("/webhooks/payments") {
        val signature = call.request.headers["X-Signature"]
            ?: return@post call.respond(HttpStatusCode.BadRequest)
        val rawBody = call.receiveText()

        // 1. Verify signature (CRITICAL — prevent spoofed webhooks)
        val expectedSig = HmacUtils.hmacSha256Hex(
            webhookSecret,
            rawBody
        )
        if (signature != expectedSig) {
            return@post call.respond(HttpStatusCode.Unauthorized)
        }

        // 2. Parse webhook payload
        val webhook = Json.decodeFromString<WebhookEvent>(rawBody)

        // 3. Check idempotency (webhooks can be delivered multiple times)
        val alreadyProcessed = redisClient.get("webhook:${webhook.txnId}")
        if (alreadyProcessed != null) {
            // Already processed — just acknowledge
            return@post call.respond(HttpStatusCode.OK)
        }

        // 4. Acknowledge IMMEDIATELY (gateway expects fast 200)
        // Process asynchronously
        call.respond(HttpStatusCode.OK)

        // 5. Process webhook asynchronously
        launch {
            try {
                when (webhook.event) {
                    "payment.success" -> {
                        transactionService.markSuccess(webhook.txnId)
                    }
                    "payment.failed" -> {
                        transactionService.markFailed(webhook.txnId, webhook.reason)
                    }
                    "refund.completed" -> {
                        refundService.markCompleted(webhook.refundId)
                    }
                }

                // 6. Mark as processed (Redis, 7-day TTL)
                redisClient.setex(
                    "webhook:${webhook.txnId}",
                    7 * 24 * 3600,
                    "processed"
                )

                // 7. Publish event for downstream consumers
                kafkaProducer.send(
                    "payment.events",
                    webhook.txnId,
                    webhook.toJson()
                )
            } catch (e: Exception) {
                // If processing fails, webhook will be retried by gateway
                // (we already returned 200, so we need a dead letter queue)
                kafkaProducer.send("webhook.dlq", webhook.txnId, webhook.toJson())
            }
        }
    }
}
```

---

## 9. Reconciliation Flow (Server)

### 9.1 Daily Reconciliation Process

```
┌──────────────────────────────────────────────────────────────┐
│                    RECONCILIATION JOB (Daily 2 AM)            │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  1. Fetch bank statement for yesterday                      │
│     → Bank API: GET /statements?date=2026-08-17             │
│     → Returns: [{ref_id, amount, type, timestamp}]          │
│                                                              │
│  2. Fetch internal transactions for yesterday               │
│     → DB: SELECT * FROM transactions WHERE date = '2026-08-17'│
│                                                              │
│  3. Match records:                                          │
│     FOR EACH bank_txn:                                      │
│       → Find internal_txn by reference_id                   │
│       → IF found AND amount matches:                        │
│           → MARK as MATCHED ✅                               │
│       → IF found BUT amount mismatch:                       │
│           → FLAG as DISCREPANCY 🚨                          │
│       → IF NOT found:                                       │
│           → FLAG as UNMATCHED_BANK 🚨                       │
│                                                              │
│     FOR EACH internal_txn NOT matched:                      │
│       → FLAG as UNMATCHED_INTERNAL 🚨                       │
│                                                              │
│  4. Generate reconciliation report:                         │
│     → Total matched: 49,998                                 │
│     → Total unmatched_internal: 1 (investigate)             │
│     → Total unmatched_bank: 1 (investigate)                 │
│     → Total discrepancies: 0                                │
│                                                              │
│  5. Auto-resolve where possible:                            │
│     → UNMATCHED_INTERNAL + bank delay → wait 24h, re-check  │
│     → UNMATCHED_BANK + timing diff → match with grace window │
│                                                              │
│  6. Alert for manual review:                                │
│     → Slack alert for any DISCREPANCY                        │
│     → Email report to finance team                          │
│     → Create JIRA ticket for investigation                  │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### 9.2 Reconciliation Code

```kotlin
// ReconciliationJob.kt

class ReconciliationJob(
    private val bankApi: BankApi,
    private val transactionRepo: TransactionRepository,
    private val reconciliationRepo: ReconciliationRepository
) {
    // Runs daily at 2 AM via scheduler
    suspend fun run(date: LocalDate): ReconciliationReport {
        // 1. Fetch bank statement
        val bankTxns = bankApi.getStatement(date)

        // 2. Fetch internal transactions
        val internalTxns = transactionRepo.findByDate(date)

        // 3. Match
        val matched = mutableListOf<MatchedTransaction>()
        val unmatchedInternal = mutableListOf<Transaction>()
        val unmatchedBank = mutableListOf<BankTransaction>()
        val discrepancies = mutableListOf<Discrepancy>()

        val matchedInternalIds = mutableSetOf<String>()

        for (bankTxn in bankTxns) {
            val internalTxn = internalTxns.find {
                it.referenceId == bankTxn.refId && !matchedInternalIds.contains(it.id)
            }

            when {
                internalTxn == null -> {
                    unmatchedBank.add(bankTxn)
                }
                internalTxn.amount == bankTxn.amount -> {
                    matched.add(MatchedTransaction(internalTxn, bankTxn))
                    matchedInternalIds.add(internalTxn.id)
                }
                else -> {
                    discrepancies.add(
                        Discrepancy(
                            internalTxn = internalTxn,
                            bankTxn = bankTxn,
                            type = DiscrepancyType.AMOUNT_MISMATCH
                        )
                    )
                    matchedInternalIds.add(internalTxn.id)
                }
            }
        }

        // Any internal txns not matched
        for (internalTxn in internalTxns) {
            if (!matchedInternalIds.contains(internalTxn.id)) {
                unmatchedInternal.add(internalTxn)
            }
        }

        // 4. Build report
        val report = ReconciliationReport(
            date = date,
            totalBankTxns = bankTxns.size,
            totalInternalTxns = internalTxns.size,
            matched = matched.size,
            unmatchedInternal = unmatchedInternal.size,
            unmatchedBank = unmatchedBank.size,
            discrepancies = discrepancies.size,
            details = ReconciliationDetails(
                unmatchedInternalList = unmatchedInternal,
                unmatchedBankList = unmatchedBank,
                discrepancyList = discrepancies
            )
        )

        // 5. Save report
        reconciliationRepo.save(report)

        // 6. Alert if issues
        if (report.hasIssues()) {
            alertService.sendReconciliationAlert(report)
        }

        return report
    }
}
```

---

## 10. Offline → Online Sync Flow

### 10.1 Flow Diagram

```
                    ┌─────────────────┐
                    │   APP OFFLINE    │
                    ├─────────────────┤
                    │ User can:       │
                    │ ✅ View history (cached) │
                    │ ✅ View balance (stale)  │
                    │ ✅ Scan QR (camera)      │
                    │ ❌ Send money (blocked)  │
                    │ ❌ Top-up (blocked)      │
                    └────────┬────────┘
                             │
                    Network restored
                             │
                    ┌────────▼────────┐
                    │  APP ONLINE      │
                    ├─────────────────┤
                    │ 1. Sync pending  │
                    │    transactions  │
                    │    (check status)│
                    │                  │
                    │ 2. Refresh       │
                    │    balance       │
                    │    (network-first)│
                    │                  │
                    │ 3. Fetch new     │
                    │    transactions  │
                    │    since last    │
                    │    sync cursor   │
                    │                  │
                    │ 4. Update local  │
                    │    cache          │
                    │    (encrypted)   │
                    │                  │
                    │ 5. Push any      │
                    │    queued non-   │
                    │    financial     │
                    │    actions       │
                    │    (profile,     │
                    │     contacts)    │
                    └─────────────────┘
```

### 10.2 Sync Code

```kotlin
// SyncManager.kt

class SyncManager(
    private val paymentRepo: PaymentRepository,
    private val transactionDao: TransactionDao,
    private val balanceDao: BalanceDao,
    private val api: PaymentApi
) {
    private var lastSyncCursor: String? = null

    // Called when network becomes available
    suspend fun sync() {
        // 1. Check pending transactions (from timeouts)
        paymentRepo.checkPendingTransactions()

        // 2. Refresh balance
        try {
            val balance = api.getBalance(getAuthToken())
            balanceDao.update(BalanceEntity(
                balance = balance.amount,
                lastUpdated = System.currentTimeMillis()
            ))
        } catch (e: Exception) {
            // Keep stale balance — don't block sync
        }

        // 3. Fetch new transactions since last sync
        try {
            var cursor = lastSyncCursor
            do {
                val page = api.getTransactions(cursor, getAuthToken())
                transactionDao.insertAll(page.transactions.map { it.toEntity() })
                cursor = page.nextCursor
            } while (cursor != null)

            lastSyncCursor = cursor
        } catch (e: Exception) {
            // Partial sync is OK — will retry next time
        }

        // 4. Push queued non-financial actions
        processQueuedActions()
    }

    private suspend fun processQueuedActions() {
        val queued = queuedActionDao.getAll()
        for (action in queued) {
            try {
                when (action.type) {
                    "SYNC_CONTACTS" -> api.syncContacts(action.payload)
                    "UPDATE_PROFILE" -> api.updateProfile(action.payload)
                    "REQUEST_MONEY" -> api.requestMoney(action.payload)
                }
                queuedActionDao.delete(action.id)
            } catch (e: Exception) {
                // Will retry next sync
                break
            }
        }
    }
}
```

### 10.3 Connectivity Monitoring

```kotlin
// ConnectivityMonitor.kt

class ConnectivityMonitor(context: Context) {
    private val connectivityManager =
        context.getSystemService<ConnectivityManager>()!!

    fun observe(): Flow<NetworkState> = callbackFlow {
        val callback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(NetworkState.Online)
            }
            override fun onLost(network: Network) {
                trySend(NetworkState.Offline)
            }
        }

        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder()
                .addCapability(NET_CAPABILITY_INTERNET)
                .build(),
            callback
        )

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }
}

sealed class NetworkState {
    object Online : NetworkState()
    object Offline : NetworkState()
}

// Usage in ViewModel/BLoC:
class MainViewModel(
    private val connectivityMonitor: ConnectivityMonitor,
    private val syncManager: SyncManager
) : ViewModel() {

    init {
        viewModelScope.launch {
            connectivityMonitor.observe().collect { state ->
                when (state) {
                    NetworkState.Online -> {
                        _uiState.value = _uiState.value.copy(isOnline = true)
                        syncManager.sync() // Auto-sync on reconnect
                    }
                    NetworkState.Offline -> {
                        _uiState.value = _uiState.value.copy(isOnline = false)
                    }
                }
            }
        }
    }
}
```

---

## Summary: Flow Comparison Table

| Flow | Online? | Idempotency Key? | Biometric? | Auto-Retry? |
|------|---------|-----------------|------------|-------------|
| UPI P2P Payment | ✅ | ✅ | ✅ | ❌ |
| QR Scan & Pay | ✅ | ✅ | ✅ | ❌ |
| Wallet Top-Up | ✅ | ✅ | ✅ | ❌ |
| Refund | ✅ | ✅ | ✅ | ❌ |
| View History | Cache-first | N/A | App unlock | N/A |
| Check Balance | Network-first | N/A | App unlock | N/A |
| Sync on Reconnect | ✅ | N/A | N/A | ✅ (safe GET) |

---

[← Payment Implementation Topics](./Mobile_Payment_Implementation_Topics.md) | [← Back to Mobile System Design](../README.md)
