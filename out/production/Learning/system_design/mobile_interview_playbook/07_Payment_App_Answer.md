# Structured Interview Answer: Design a Payment Application

> **Question**: *"Design a mobile payment application like Google Pay/PhonePe/PayPal."*

---

## Step 1 — Clarify Requirements

**Questions:**
- P2P (person-to-person) transfers, merchant payments, or both?
- NFC/tap-to-pay, QR code, or UPI-style?
- Bank account linking or wallet-based?
- Biometric authentication for transactions?
- Transaction history and receipts?
- International or domestic only?

**Assumed:** Flutter, P2P + merchant, QR + UPI-style, bank-linked, biometric yes, history yes, domestic.

---

## Step 2 — Define Scope

```
IN SCOPE: Send/receive money (P2P), pay merchants via QR, bank account linking, biometric auth per transaction, transaction history, push notifications
OUT OF SCOPE: Credit lines/loans, international transfers, investment/trading, merchant dashboard
```

---

## Step 3 — Constraints

```
Functional: Send money, receive money, QR scan to pay, bank linking, biometric auth, transaction history
Non-Functional: 100M users, ~5M concurrent at peak, < 3s transaction confirmation, ZERO data loss, PCI-DSS compliant
```

---

## Step 4 — High-Level Architecture

```
                    ┌──────────────┐
                    │  Mobile App  │
                    │   Flutter    │
                    └──────┬───────┘
                           │
         REST (transactions) + WebSocket (status) + FCM
                           │
                    ┌──────▼───────┐
                    │ API Gateway  │
                    │ (mTLS, WAF)  │
                    └──────┬───────┘
                           │
    ┌──────────────────────┼──────────────────────┐
    │                      │                      │
 Payment Service     Bank Linking Service    Notification Service
 (initiate, execute)  (UPI/bank API)         (FCM push)
    │                      │                      │
 Fraud Detection      Wallet/Balance          Kafka (events)
 (ML model, rules)    Ledger                  │
    │                      │                      │
    └──────────────────────┼──────────────────────┘
                           │
                    ┌──────▼───────┐
                    │ PostgreSQL   │
                    │ (ACID)       │
                    │ + Redis      │
                    │ + Kafka      │
                    └──────────────┘
```

> "Payment systems require ACID transactions — PostgreSQL with serializable isolation. Every transaction is double-entry booked. Fraud detection runs asynchronously via Kafka but can block high-risk transactions."

---

## Step 5 — Mobile Architecture

```
┌─────────────────────────────┐
│            UI               │
│  HomeScreen, SendMoney,     │
│  QRScanner, TransactionHistory│
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│          BLoC               │
│  PaymentBloc, QRBloc,        │
│  TransactionBloc, AuthBloc   │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│         Use Cases           │
│  SendMoneyUseCase             │
│  ScanQRUseCase                │
│  RequestMoneyUseCase          │
│  AuthenticateTransactionUseCase│
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│      Repository             │
│  PaymentRepository            │
│  BankRepository               │
│  TransactionRepository        │
└──────────┬───────────┬──────┘
           ↓           ↓
      REST API       Local DB
      (Dio)        (SQLCipher/Drift)
```

---

## Step 6 — Data Flow

### Send Money (P2P):
```
User enters amount + selects recipient
  ↓
Biometric authentication (fingerprint/face)
  ↓
AuthenticateTransactionUseCase verifies:
  → Biometric result
  → Amount within daily limit
  → Device not flagged (root/jailbreak check)
  ↓
POST /api/payments (idempotency key = UUID)
  Body: { recipient_id, amount, currency, note, idempotency_key }
  ↓
Server: validate → fraud check → debit sender → credit recipient
  ↓
WebSocket push: PAYMENT_SUCCESS
  ↓
UI shows success animation + receipt
  ↓
FCM push to recipient: "You received ₹X"
```

### QR Payment (Merchant):
```
User scans merchant QR code
  ↓
Parse QR: { merchant_id, amount (optional) }
  ↓
If amount not in QR → user enters amount
  ↓
Biometric auth
  ↓
POST /api/payments (idempotency key)
  Body: { merchant_id, amount, idempotency_key }
  ↓
Server: validate → fraud check → debit user → credit merchant
  ↓
UI shows "Payment Successful" + merchant sees confirmation
```

| Data | Strategy | TTL |
|------|----------|-----|
| Balance | Network-first (always fresh) | N/A |
| Transaction history | Cache-first | 5 min |
| Bank accounts | Local (encrypted) + sync | N/A |
| Contacts (for P2P) | Cache-first | 1 hour |
| Receipts | Local DB (encrypted) | Permanent |

---

## Step 7 — Networking

```
REST:
  POST /payments (idempotency key — CRITICAL)
  GET /transactions?cursor=xxx (history, cursor pagination)
  connectTimeout: 8s (users are waiting for payment)
  receiveTimeout: 10s

Retry Policy:
  GET requests: retry 3x (safe)
  POST /payments: NEVER retry blindly! Idempotency key prevents duplicates
    → If timeout: check transaction status before retry
    → GET /payments/{idempotency_key}/status
    → If PENDING → wait, don't retry
    → If FAILED → safe to retry with SAME idempotency key

WebSocket:
  /ws/payments/{user_id}
  Push: payment status updates (SUCCESS, FAILED, PENDING)
  Used for real-time confirmation (not polling)

Security headers:
  → mTLS (mutual TLS) between mobile and API
  → Request signing (HMAC) on payment requests
  → Device fingerprint in headers
```

**Idempotency is THE most critical pattern:**
> "For payments, idempotency is non-negotiable. Every payment gets a UUID idempotency key. If the request times out, the client does NOT retry blindly — it first checks the status using the idempotency key. Only if the transaction is confirmed FAILED does it retry with the same key."

---

## Step 8 — Offline Support

```
Payments CANNOT happen offline — bank API requires network.
  → If offline: show "No internet, cannot process payment"

Cached data:
  → Transaction history (local DB, cache-first — read offline)
  → Bank accounts (encrypted local DB)
  → Contacts for P2P

Pending actions:
  → Request money: can be queued (FCM to recipient when online)
  → Send money: MUST be online — no offline payments
```

---

## Step 9 — Performance

| Category | Techniques |
|----------|-----------|
| **Startup** | Load balance + recent transactions from local DB immediately |
| **UI** | QR scanner using camera2, smooth payment success animation, ListView.builder for history |
| **Network** | 8s timeout (fast — user waiting), idempotency check before retry |
| **Memory** | QR scanner released when not active, transaction list paginated |
| **Battery** | No background polling (WebSocket + FCM only), NFC only when tap-to-pay active |

---

## Step 10 — Security (MOST CRITICAL)

```
Authentication:
  → OAuth2 for app login
  → BIOMETRIC for every transaction (fingerprint/face)
  → PIN as fallback (if biometric fails)

Transaction Security:
  → Idempotency key per transaction (prevent duplicates)
  → HMAC request signing (prevent tampering)
  → Device fingerprinting (detect cloned apps)
  → Root/jailbreak detection (block on rooted devices)

Data at Rest:
  → SQLCipher for local DB (transaction history, bank details)
  → EncryptedSharedPreferences for sensitive prefs
  → No card numbers stored on device (tokenized)

Network:
  → mTLS (mutual TLS) — client certificate + server certificate
  → Certificate pinning
  → TLS 1.3 enforced
  → No sensitive data in URL (use POST body)

Fraud Prevention:
  → Server-side ML model (velocity check, amount patterns, device trust)
  → Risk score per transaction → block high-risk, challenge medium-risk
  → Geolocation anomaly detection

Compliance:
  → PCI-DSS (if handling cards)
  → RBI/financial regulations (for India/UPI)
  → Data localization (transactions stored in-country)
```

> "Security is the #1 priority for payment apps. Every transaction requires biometric auth, every request is signed with HMAC, the local DB is encrypted with SQLCipher, and rooted devices are blocked entirely."

---

## Step 11 — Scalability

- PostgreSQL with read replicas (transaction history is read-heavy)
- Kafka for event streaming (every payment → fraud check, analytics, notifications)
- Redis for: session cache, rate limiting, idempotency key dedup
- Bank API gateway: connection pooling, circuit breaker (if bank API is down, queue + retry)
- Fraud detection: async via Kafka (doesn't block payment unless high-risk)

---

## Step 12 — Failure Scenarios

| Failure | Response |
|---------|----------|
| Network timeout on payment | DO NOT retry. Check status via idempotency key. |
| Bank API down | Queue transaction, show "Processing", retry via Kafka consumer |
| Biometric fails | PIN fallback (3 attempts → lock for 30 min) |
| Rooted device detected | Block app entirely, show "Security risk" |
| Fraud detection flags | Challenge with OTP/biometric, or block + notify |
| Double-spend attempt | Server enforces: check balance atomically (SELECT FOR UPDATE) |
| App crash during payment | On relaunch: check pending transaction status, show result |
| Duplicate request | Idempotency key → server returns same result |

---

## Step 13 — Trade-offs

| Decision | Choice | Alternative | Why |
|----------|--------|-------------|-----|
| DB | PostgreSQL (ACID) | NoSQL | Financial data requires ACID, no compromises |
| Idempotency | UUID per transaction | Server-generated ID | Client generates → survives network failure |
| Retry | Check-then-retry | Blind retry | Prevents duplicate payments — critical for trust |
| Auth | Biometric per transaction | App-level only | Defense in depth — every payment verified |
| Fraud | Async (Kafka) | Sync (block every payment) | Don't add latency unless high-risk |
| Local DB | SQLCipher | Plain SQLite | Financial data encrypted at rest |

---

## Step 14 — Testing & Observability

| Level | What |
|-------|------|
| Unit | Idempotency logic, amount validation, biometric flow, BLoC states |
| Integration | Payment flow with mocked bank API, fraud check |
| E2E | Full P2P transfer + QR payment (test environment) |
| Security | Penetration testing, OWASP MASVS compliance, root detection |
| Observability | Transaction success rate p99, bank API latency, fraud block rate, crash-free rate 99.9%+ |
| Compliance | PCI-DSS audit, RBI compliance, data residency audit |
