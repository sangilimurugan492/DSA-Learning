# Example: Design a Payment Application (Google Pay/PhonePe-style)

> **Full 60-minute mock interview answer using the 14-step framework.**
>
> **Prompt**: *"Design a mobile payment application like Google Pay/PhonePe for 100 million users."*

---

## Step 1 — Clarify Requirements (0–5 min)

**You ask:**
1. Platform? → *"Android and iOS, Flutter cross-platform."*
2. Payment methods? → *"UPI, wallet, cards, bank transfer."*
3. QR code payments? → *"Yes, scan & pay."*
4. P2P transfers? → *"Yes, send money to contacts."*
5. Transaction history? → *"Yes, with receipts."*
6. Offline support? → *"Limited — QR scan offline, payment requires network."*
7. Security level? → *"Highest — biometric auth, encryption, fraud detection."*
8. Scale? → *"100M users, ~50M daily transactions."*

**Summary:**
- **Functional**: UPI payment, P2P transfer, QR scan & pay, wallet, transaction history, bill payments, biometric auth
- **Non-functional**: <2s transaction completion, bank-grade security, 100M users, 50M txns/day, zero data leaks

---

## Step 2 — Define Scope (5 min)

**In scope:**
- UPI payment flow (scan QR, send to contact, send to UPI ID)
- Wallet management (balance, top-up, spend)
- Transaction history with receipts
- Biometric authentication (fingerprint/face)
- Push notifications (transaction alerts)

**Out of scope:**
- Merchant dashboard (separate app)
- Loan/credit features
- International transfers
- Crypto

---

## Step 3 — Identify Constraints (5 min)

```
100M users, ~50M daily transactions
Peak: festival season, salary day (1st-5th) → 5x traffic
Avg transaction: ₹500
Payment latency: <2s end-to-end
UPI settlement: real-time (IMPS)
Security: PCI DSS compliance, RBI guidelines
Devices: 80% Android (many low-end), biometric support varies
Zero tolerance for data breach
```

---

## Step 4 — High-Level Architecture (5–10 min)

```
┌───────────────────────────────────────────────────────┐
│              Mobile App (Flutter)                       │
│  ┌──────────┐ ┌────────┐ ┌──────────┐ ┌─────────────┐ │
│  │  Home /  │ │ Send   │ │ Scan QR  │ │  History    │ │
│  │  Wallet  │ │ Money  │ │  Pay     │ │  Receipts   │ │
│  └─────┬────┘ └───┬────┘ └────┬─────┘ └──────┬──────┘ │
│        └──────────┴───────────┴──────────────┘        │
│                    │                                    │
│         ┌──────────┼──────────┐                        │
│         ▼          ▼          ▼                        │
│     REST API   Biometric   Local DB                    │
│   (payments,  (local_auth  (Drift + SQLCipher:         │
│    history)    for auth)    encrypted txn cache)       │
│                                    QR cache)           │
└──────────────┬────────────────────────────────────────┘
               │
        ┌──────▼───────┐
        │ API Gateway  │ + WAF (Web Application Firewall)
        │ + Rate Limit │
        └──────┬───────┘
       ┌───────┼────────────┐
       ▼       ▼            ▼
  ┌────────┐ ┌──────────┐ ┌───────────┐
  │Payment  │ │  Wallet  │ │  Notif    │
  │ Service │ │ Service  │ │  Service  │
  └────┬───┘ └────┬─────┘ └─────┬─────┘
       │          │             │
  ┌────▼───┐ ┌───▼────┐ ┌──────▼──────┐
  │UPI /   │ │PostgreSQL│ │Kafka +    │
  │Bank    │ │(wallets, │ │FCM/APNS   │
  │Gateway │ │ txns)   │ └───────────┘
  └────────┘ └────────┘
       │
  ┌────▼────────────┐
  │ Fraud Detection  │
  │ Service (ML)     │
  └─────────────────┘

Redis ← Session cache, rate limiting, idempotency keys
Kafka ← Transaction events (audit, notifications, analytics)
```

---

## Step 5 — Mobile Architecture (10–25 min)

### Flutter Clean Architecture:

```
┌─────────────────────────────────────────────┐
│              Presentation                     │
│  Widgets: HomePage, SendMoneyPage,           │
│           ScanQRPage, TransactionHistory,     │
│           ReceiptPage, BiometricPrompt       │
│  BLoCs: AuthBloc, PaymentBloc,               │
│         WalletBloc, HistoryBloc              │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                Domain                         │
│  Use Cases:                                   │
│  - AuthenticateUserUseCase (biometric/PIN)   │
│  - SendMoneyUseCase                           │
│  - ScanQRPayUseCase                           │
│  - GetTransactionHistoryUseCase               │
│  - CheckBalanceUseCase                        │
│  - TopUpWalletUseCase                         │
│  Repositories (abstract):                     │
│  - PaymentRepository                          │
│  - WalletRepository                           │
│  - AuthRepository                             │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                  Data                         │
│  Remote: PaymentApi, WalletApi (Dio)         │
│  Auth: BiometricAuth (local_auth)            │
│  QR: MobileScanner (camera + decode)         │
│  Local: TransactionDao (Drift + SQLCipher)   │
│  Security: SecureStorage (Keystore/Keychain)  │
└─────────────────────────────────────────────┘
```

### Key Design: PaymentBloc (Critical — Money Movement)

```
┌──────────────────────────────────────────────┐
│              PaymentBloc                       │
├──────────────────────────────────────────────┤
│  CRITICAL: Every payment must be:             │
│  1. Authenticated (biometric/PIN)             │
│  2. Idempotent (idempotency key per txn)      │
│  3. Atomic (all-or-nothing)                   │
│  4. Auditable (full transaction log)          │
│  5. Retryable (safe retry with same key)     │
├──────────────────────────────────────────────┤
│  Payment Flow:                                │
│  1. User enters amount + recipient            │
│  2. Biometric/PIN authentication              │
│  3. Generate idempotency_key (UUID)           │
│  4. POST /api/payments (with key)             │
│  5. Show "Processing..." state                │
│  6. On success: show receipt, update wallet   │
│  7. On failure: show error, DON'T retry       │
│     automatically (user-initiated retry only) │
└──────────────────────────────────────────────┘
```

### Key Design: AuthBloc (Security Gate)

```
┌──────────────────────────────────────────────┐
│              AuthBloc                          │
├──────────────────────────────────────────────┤
│  Responsibilities:                             │
│  - App launch: biometric or PIN to unlock     │
│  - Before every payment: biometric re-auth    │
│  - Session timeout: re-auth after 5 min idle  │
│  - Failed attempts: lock after 5 tries        │
│                                                │
│  Auth methods (in priority):                  │
│  1. Biometric (fingerprint/face) — preferred  │
│  2. PIN (6-digit) — fallback                   │
│  3. Password — recovery only                   │
└──────────────────────────────────────────────┘
```

### Why (Lead-Level Justification):

> *"For a payment app, security is paramount. Every payment action requires biometric re-authentication — session auth is not sufficient. The PaymentBloc enforces idempotency with UUID keys to prevent duplicate transactions on network retry. Payments are never auto-retried — only user-initiated retries. The AuthBloc acts as a security gate with session timeout and attempt locking. All local data is encrypted with SQLCipher, and no sensitive data (PIN, card numbers) is ever stored locally."*

---

## Step 6 — Data Flow (25–30 min)

### UPI Payment Flow:

```
SendMoneyPage (user enters amount + UPI ID)
  → AuthBloc.add(RequestBiometricAuth)
    → Biometric prompt → user authenticates
    → AuthBloc emits Authenticated
  → PaymentBloc.add(SendMoney(amount, upiId))
    → SendMoneyUseCase.call()
      → Generate idempotency_key (UUID)
      → PaymentRepository.sendMoney()
        → POST /api/payments
          Headers:
            Idempotency-Key: <UUID>
            Authorization: Bearer <token>
          Body: { amount, recipient_upi, payment_method: "UPI" }
        → PaymentBloc emits Processing
        → Server:
          → Validate sender balance
          → Fraud detection check (ML)
          → Initiate UPI transaction via bank gateway
          → On success: debit sender, credit recipient
          → Return transaction receipt
        → PaymentBloc emits PaymentSuccess(receipt)
        → Save receipt to Drift (encrypted)
        → Update wallet balance
        → Show receipt screen
        → Push notification: "₹500 sent to xxx"
```

### QR Scan & Pay Flow:

```
ScanQRPage (camera opens)
  → MobileScanner detects QR code
  → Parse QR: upi://pay?pa=merchant@upi&pn=Merchant&am=100
  → If amount in QR: pre-fill payment screen
  → If no amount: user enters amount
  → AuthBloc → biometric re-auth
  → PaymentBloc.add(PayViaQR(qrData, amount))
    → Same flow as SendMoney (idempotency key, POST /api/payments)
```

### Transaction History Flow:

```
HistoryPage
  → HistoryBloc.add(LoadHistory(cursor))
    → GetTransactionHistoryUseCase.call()
      → Check Drift (encrypted cache)
        → Has cache? → show immediately
        → Fetch from API: GET /api/transactions?cursor=xxx
        → Update Drift cache
        → emit HistoryLoaded
```

---

## Step 7 — Networking (30–35 min)

### REST API Design:

```
GET  /api/wallet/balance                   → Wallet balance
POST /api/payments                          → Initiate payment (idempotency key)
GET  /api/transactions?cursor=             → Transaction history (paginated)
GET  /api/transactions/{id}               → Transaction detail / receipt
POST /api/wallet/topup                     → Top-up wallet
GET  /api/contacts/sync                    → Sync contacts for P2P
POST /api/auth/verify-pin                  → Verify PIN (server-side validation)
```

### Timeout & Retry Policy (Critical for Payments):

```
Payment API:
  → Connect timeout: 8s
  → Receive timeout: 15s (bank settlement may take time)
  → Max retries: 0 (NO auto-retry for payments!)
  → User-initiated retry only, with same idempotency key

Non-payment APIs (history, balance):
  → Connect timeout: 10s
  → Receive timeout: 10s
  → Max retries: 2 (safe for GET)
```

> **Critical Rule**: Payment POST requests are **never auto-retried**. If a payment times out, the app shows "Transaction status unknown — check history." The idempotency key ensures that if the user manually retries, the server deduplicates.

### Idempotency (Non-Negotiable):

```
POST /api/payments
Headers:
  Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000
  Authorization: Bearer <access_token>
  X-Device-ID: <device_fingerprint>
Body: { amount: 500, recipient: "user@upi", method: "UPI" }

Server behavior:
  → First request with key: process payment, store result
  → Retry with same key: return stored result (no duplicate charge)
  → Different request, same key: return 409 Conflict
```

### Request Signing (Additional Security):

```
Each payment request includes:
  → X-Request-Signature: HMAC-SHA256(payload, device_secret_key)
  → Server verifies signature to detect tampering
  → Device secret key stored in Keystore (never leaves device)
```

---

## Step 8 — Offline Support & Sync (35–40 min)

### Offline Behavior:

| Action | Offline Handling |
|--------|-----------------|
| View transaction history | Show cached history from Drift (encrypted). Mark "offline." |
| Check balance | Show last known balance with timestamp. "May not be current." |
| Send money | **Block**: "Internet required for payments." |
| Scan QR | ✅ Camera + QR decode works offline. Payment blocked until online. |
| View receipt | Show cached receipt from Drift. |
| Open app | Biometric/PIN auth works offline. |

### Minimal Offline Strategy:
> Payment apps are **online-only for transactions** (money movement requires real-time bank communication). Offline support is limited to read-only cached data (history, receipts, balance snapshot).

### Local Cache (Encrypted with SQLCipher):

```sql
CREATE TABLE cached_transactions (
  id TEXT PRIMARY KEY,
  type TEXT,              -- DEBIT, CREDIT, TOPUP
  amount REAL,
  recipient_name TEXT,
  recipient_upi TEXT,
  status TEXT,            -- SUCCESS, PENDING, FAILED
  timestamp INTEGER,
  receipt_url TEXT,
  reference_id TEXT
);

CREATE TABLE cached_balance (
  id INTEGER PRIMARY KEY DEFAULT 1,
  balance REAL,
  last_updated INTEGER
);
```

---

## Step 9 — Performance (40–45 min)

| Category | Optimization |
|----------|-------------|
| **Startup** | Init: auth + Drift (encrypted). Home screen loads from cached balance. Defer contacts sync, analytics. |
| **UI** | Payment flow: minimal screens (amount → auth → processing → receipt). `const` widgets. Animated success checkmark. Haptic feedback on payment success. |
| **Network** | Payment API: no retry, direct call. History: cursor pagination (20/page). Compress (gzip). ETag for balance cache. |
| **Memory** | Transaction list: keep max 200 in memory. Receipts: lazy load. QR scanner: release camera immediately after scan. |
| **Battery** | No background polling. FCM for transaction notifications. No location tracking. |
| **QR Scan** | Use ML Kit for on-device QR detection (no network needed). Camera autofocus optimization. |

### Payment Latency Breakdown (Target: <2s):

```
1. Biometric auth:        ~300ms (hardware)
2. API request + network: ~200ms
3. Server validation:     ~100ms
4. Fraud check:            ~100ms (cached model)
5. UPI/Bank processing:   ~800ms (external)
6. Response + UI update:  ~100ms
Total:                   ~1.6s
```

---

## Step 10 — Security (45–50 min) — Most Critical Section

### Multi-Layer Security:

| Layer | Implementation |
|-------|---------------|
| **App Unlock** | Biometric (fingerprint/face) or 6-digit PIN. Lock after 5 min idle. Lock after 5 failed PIN attempts (30-min cooldown). |
| **Payment Auth** | Biometric re-auth before EVERY payment. PIN fallback if biometric unavailable. |
| **Auth (API)** | OAuth2 with short-lived access tokens (5 min). Refresh token (7 days). Token rotation on refresh. |
| **Token Storage** | Android Keystore (hardware-backed) / iOS Keychain (Secure Enclave). Never SharedPreferences. |
| **API Security** | SSL pinning (all endpoints). TLS 1.3. Request signing (HMAC-SHA256). API key in header, rotated via remote config. |
| **Idempotency** | UUID per payment. Server deduplicates. Prevents double-charge on retry. |
| **Fraud Detection** | Server-side ML model: device fingerprint, location, amount, frequency, recipient. Block suspicious transactions. |
| **Local DB** | SQLCipher (AES-256). Encryption key derived from device + user PIN. |
| **Screen Security** | `FLAG_SECURE` (Android) — block screenshots/screen recording. |
| **Root/Jailbreak** | Detect rooted device. Block app on rooted (or restrict to view-only). |
| **Code Obfuscation** | R8/ProGuard (Android), `--obfuscate` (Flutter). |
| **No Sensitive Data Stored** | No PIN, no card number, no bank details in local DB. Only transaction history (encrypted). |
| **Secure Logging** | No financial data in logs. No PII. Debug-only. |
| **Device Binding** | One account per device. New device requires OTP + re-KYC. |

### Biometric Authentication Flow:

```
User taps "Pay ₹500"
  → AuthBloc.add(RequestBiometricAuth(reason: "Confirm payment of ₹500"))
    → local_auth.authenticate(biometricsOnly: true)
    → If success: emit Authenticated → proceed with payment
    → If fail: emit AuthFailed
      → If biometric unavailable: fall back to PIN
      → PIN entered → verify with server (POST /api/auth/verify-pin)
      → If PIN correct: proceed
      → If 5 wrong PINs: lock app for 30 minutes
```

### Token Security:

```
Login:
  → OTP verification
  → Server issues: access_token (5 min) + refresh_token (7 days)
  → Tokens stored in Keystore/Keychain
  → Access token attached to every API request via interceptor

Auto-refresh:
  → 401 response → RefreshTokenInterceptor
  → Use refresh token to get new access token
  → Retry original request
  → If refresh fails: force logout, clear tokens

Token invalidation:
  → Logout: clear tokens from Keystore
  → Device change: invalidate all tokens for user
  → Suspicious activity: force re-auth
```

---

## Step 11–12 — Scalability & Failure Scenarios (50–55 min)

### Scalability:
- Payment Service: stateless, auto-scaling. Idempotency keys in Redis (24h TTL).
- Database: PostgreSQL with read replicas (history queries), sharded by user_id for wallets.
- Fraud Detection: ML model in Redis (cached scoring), async deep analysis via Kafka.
- UPI Gateway: connection pooling to bank APIs, circuit breaker on bank outage.
- Kafka: transaction events → audit log, notifications, analytics, fraud analysis.

### Failure Scenarios:

| Scenario | Handling |
|----------|---------|
| Payment API timeout | Show "Status unknown — check transaction history." DON'T auto-retry. User can check history (server will have result if processed). |
| Bank/UPI gateway down | Server returns specific error: "Bank service unavailable." Show user-friendly message. |
| Double-tap on pay button | Debounce in UI (disable button after first tap). Idempotency key on server. |
| Network drops mid-transaction | Server completes if request was received. Client shows "check history." Receipt synced on reconnect. |
| Biometric sensor fails | Fall back to PIN. If PIN also fails 5x → lock app. |
| App killed during processing | On reopen: check transaction status via GET /api/transactions/{id}. Show appropriate result. |
| Fraud detection blocks payment | Show "Transaction blocked for security. Contact support." with reference ID. |
| Rooted device detected | Show warning. Block payments (view-only mode). |
| Token expired during payment | Auto-refresh token, retry payment with same idempotency key. |
| Duplicate payment (network retry) | Idempotency key ensures server returns original result. No double-charge. |

---

## Step 13–14 — Trade-offs & Testing (55–60 min)

### Trade-offs:

| Decision | Trade-off |
|----------|-----------|
| No auto-retry for payments | Safer (no accidental double-charge), but worse UX if timeout was transient. User must manually check/retry. |
| Biometric for every payment | Most secure, but slight friction. Acceptable for financial apps. |
| SQLCipher (encrypted local DB) | Security benefit, ~10-15% performance overhead. Essential for payment data. |
| Online-only transactions | Can't pay offline. Acceptable — real-time bank communication is mandatory. |
| Device binding (1 device per account) | Strong security, but inconvenient for multi-device users. Can add second device with re-KYC. |
| Short access token (5 min) | Frequent refreshes, but limits token theft window. Essential for financial apps. |

### Testing & Observability:

| Type | Scope |
|------|-------|
| **Unit** | Idempotency key generation, amount validation, QR parsing, biometric fallback logic |
| **Integration** | Payment API with mock bank gateway, idempotency deduplication, token refresh flow |
| **E2E** | Full payment: scan QR → enter amount → biometric → pay → receipt → history. Failure scenarios: timeout, bank down, fraud block. |
| **Security Testing** | Penetration testing, certificate pinning bypass attempts, rooted device behavior, SQLCipher key extraction attempts |
| **Observability** | Crashlytics (crashes), custom events (payment success rate, avg latency, fraud block rate, biometric success rate), audit log (every payment attempt with full metadata) |

---

## Summary

> *"The app uses Flutter Clean Architecture with BLoC. Every payment requires biometric re-authentication via AuthBloc. The PaymentBloc enforces idempotency with UUID keys — no payment is auto-retried; only user-initiated retries with the same key (server deduplicates). Security is multi-layered: biometric/PIN auth, OAuth2 with 5-minute access tokens stored in Keystore/Keychain, SSL pinning, request signing (HMAC-SHA256), SQLCipher for local DB, FLAG_SECURE against screenshots, rooted device detection, and server-side fraud detection. Transactions are online-only (bank communication required), but history and balance are cached locally (encrypted). The app targets <2s payment latency with zero tolerance for data breaches or double-charges."*

---

[← Back to Guide](./Mobile_System_Design_Interview_Guide.md)
