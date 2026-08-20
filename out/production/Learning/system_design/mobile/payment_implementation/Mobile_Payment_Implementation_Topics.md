# Mobile Payment Implementation — Important Topics

> A comprehensive reference covering every critical topic you must know when implementing a mobile payment system (Google Pay / PhonePe / PayPal / Stripe style).

---

## Table of Contents

1. [Payment Methods & Protocols](#1-payment-methods--protocols)
2. [Idempotency](#2-idempotency)
3. [Transaction State Machine](#3-transaction-state-machine)
4. [Double-Entry Ledger](#4-double-entry-ledger)
5. [Authentication & Authorization](#5-authentication--authorization)
6. [Tokenization & PCI-DSS](#6-tokenization--pci-dss)
7. [Encryption (At Rest & In Transit)](#7-encryption-at-rest--in-transit)
8. [Fraud Detection & Risk Scoring](#8-fraud-detection--risk-scoring)
9. [Reconciliation & Settlement](#9-reconciliation--settlement)
10. [Webhooks & Async Status Updates](#10-webhooks--async-status-updates)
11. [Refunds, Chargebacks & Disputes](#11-refunds-chargebacks--disputes)
12. [Offline Support & Caching](#12-offline-support--caching)
13. [Deep Linking & UPI Intent](#13-deep-linking--upi-intent)
14. [QR Code Standards & Parsing](#14-qr-code-standards--parsing)
15. [Compliance & Regulatory](#15-compliance--regulatory)
16. [Observability & Audit Logging](#16-observability--audit-logging)
17. [Testing Strategies for Payments](#17-testing-strategies-for-payments)
18. [Common Pitfalls & Anti-Patterns](#18-common-pitfalls--anti-patterns)

---

## 1. Payment Methods & Protocols

### 1.1 UPI (Unified Payments Interface) — India

| Concept | Details |
|---------|---------|
| **VPA (Virtual Payment Address)** | `name@bank` — replaces bank account details |
| **UPI ID vs. UPI Number** | ID is `name@bank`; Number is a 8–11 digit alias |
| **NPCI** | Switching/routing authority — all UPI transactions go through NPCI |
| **PSP (Payment Service Provider)** | The app provider (e.g., PhonePe, Google Pay) |
| **Remitter & Beneficiary** | Sender = remitter; Receiver = beneficiary |
| **Mandate API** | Recurring payments (subscriptions, SIPs) |
| **Collect Request** | Payee requests money from payer (P2P collect) |
| **Transaction Types** | P2P, P2M (merchant), P2G (government), P2B (bill pay) |

**UPI Transaction Flow (Simplified):**
```
PSP App → PSP Backend → NPCI → Beneficiary Bank → (credit)
                     ↘ Remitter Bank → (debit)
```

### 1.2 Card Payments (Visa/Mastercard/Amex)

| Concept | Details |
|---------|---------|
| **PAN** | Primary Account Number (card number) — **never store raw** |
| **Acquirer** | Bank that processes for the merchant |
| **Issuer** | Bank that issued the card to the customer |
| **Authorization** | Reserve funds (hold) — not a charge yet |
| **Capture** | Confirm the charge (converts hold to debit) |
| **Auth + Capture** | Most common: authorize now, capture on fulfillment |
| **3-D Secure (3DS)** | Additional OTP/biometric verification for cards |
| **PCI-DSS** | Standard for handling card data — **tokenize to avoid** |

### 1.3 Wallets

| Concept | Details |
|---------|---------|
| **Prepaid Wallet** | Stored value — load from bank, spend from wallet |
| **Closed Wallet** | Single merchant (e.g., Amazon Pay) |
| **Semi-Closed** | Multiple merchants but no cash withdrawal (e.g., Paytm) |
| **Open Wallet** | Can withdraw to bank (requires full KYC) |
| **Top-up** | Load money into wallet from bank/card |
| **Wallet-to-Bank** | Withdraw from wallet to bank account |

### 1.4 NFC / Contactless

| Concept | Details |
|---------|---------|
| **HCE (Host Card Emulation)** | Android — emulates a card via software |
| **Tokenized PAN** | Device-specific token replaces real PAN |
| **Tap-to-Pay** | NFC + tokenization — Google Pay / Apple Pay style |
| **EMVCo Tokenization** | Standard for tokenizing card data for NFC |

---

## 2. Idempotency

> **The single most important concept in payment systems.** Without idempotency, network retries cause double charges.

### 2.1 What Is Idempotency?

An operation is idempotent if executing it once has the same effect as executing it multiple times.

### 2.2 Idempotency Key

```
Client generates: UUID v4 (e.g., 550e8400-e29b-41d4-a716-446655440000)
Sent in header:   Idempotency-Key: <UUID>
```

### 2.3 Server-Side Behavior

| Request | Server Action |
|---------|---------------|
| First request with key | Process payment → store result with key |
| Retry with same key + same body | Return stored result (no re-processing) |
| Retry with same key + different body | Return **409 Conflict** (potential tampering) |
| Key expired (TTL passed) | Process as new transaction |

### 2.4 Storage

- **Redis**: `idempotency_key → {status, result, created_at}` with 24h TTL
- **PostgreSQL**: `idempotency_key` column with UNIQUE constraint

### 2.5 Client-Side Rules

```
1. Generate idempotency key BEFORE sending request
2. On timeout: DO NOT retry blindly
3. First: GET /payments/{idempotency_key}/status
4. If PENDING → wait (server still processing)
5. If FAILED → safe to retry with SAME key
6. If SUCCESS → show receipt (already processed)
7. Never generate a new key for the same logical transaction
```

### 2.6 Idempotency Key Lifecycle

```
[Generated] → [Sent with request] → [Stored on server]
                                        ↓
                              [TTL: 24 hours]
                                        ↓
                                  [Expired/Removed]
```

---

## 3. Transaction State Machine

Every payment moves through a well-defined state machine. No state transitions are skipped.

```
                    ┌─────────────┐
                    │   CREATED   │  (client initiated, not yet sent)
                    └──────┬──────┘
                           ↓
                    ┌─────────────┐
                    │   PENDING   │  (sent to server/bank, awaiting response)
                    └──────┬──────┘
                     ┌─────┴──────┐
                     ↓            ↓
              ┌──────────┐  ┌──────────┐
              │  SUCCESS │  │  FAILED  │
              └──────────┘  └──────────┘
                     │            │
                     ↓            ↓
              [Receipt]     [Error Screen]
                               ↓
                        ┌──────────┐
                        │ RETRYING │  (user-initiated, same idempotency key)
                        └──────────┘
```

### 3.1 State Transition Rules

| From | To | Trigger |
|------|-----|--------|
| CREATED | PENDING | Request sent to server |
| PENDING | SUCCESS | Bank confirms debit/credit |
| PENDING | FAILED | Bank rejects / fraud block / insufficient funds |
| FAILED | PENDING | User-initiated retry (same idempotency key) |
| SUCCESS | (terminal) | No further transitions |
| PENDING | TIMEOUT | No response within threshold → check status |

### 3.2 Terminal vs. Non-Terminal States

- **Terminal**: SUCCESS, FAILED (after max retries)
- **Non-Terminal**: CREATED, PENDING, TIMEOUT
- **Rule**: Only terminal states are shown as final to the user. Non-terminal states show "Processing..."

---

## 4. Double-Entry Ledger

Every financial system uses double-entry bookkeeping. Every transaction has two entries: a debit and a credit. They must always balance.

### 4.1 Ledger Entry Structure

```
transaction_id | account_id | entry_type | amount  | balance_after
---------------|------------|------------|---------|-------------
TXN_001        | USER_A     | DEBIT      | 500.00  | 4500.00
TXN_001        | USER_B     | CREDIT     | 500.00  | 1500.00
```

### 4.2 Rules

1. **Every transaction = 1 debit + 1 credit** (minimum)
2. **Sum of debits = sum of credits** (always balanced)
3. **Atomic**: both entries committed in a single DB transaction
4. **Immutable**: ledger entries are never updated — only appended
5. **Balance = sum of all entries for an account**

### 4.3 SQL Example (PostgreSQL)

```sql
BEGIN;

-- Lock sender's account row (prevent concurrent debits)
SELECT balance FROM accounts WHERE id = 'USER_A' FOR UPDATE;

-- Check sufficient balance
-- (application logic: if balance < amount → ROLLBACK)

-- Debit sender
INSERT INTO ledger_entries (txn_id, account_id, entry_type, amount)
VALUES ('TXN_001', 'USER_A', 'DEBIT', 500.00);

-- Credit receiver
INSERT INTO ledger_entries (txn_id, account_id, entry_type, amount)
VALUES ('TXN_001', 'USER_B', 'CREDIT', 500.00);

-- Update balances
UPDATE accounts SET balance = balance - 500.00 WHERE id = 'USER_A';
UPDATE accounts SET balance = balance + 500.00 WHERE id = 'USER_B';

COMMIT;
```

### 4.4 Why `SELECT ... FOR UPDATE`?

Without row-level locking, two concurrent transactions could both read the same balance and both succeed — causing a negative balance (double-spend). `FOR UPDATE` locks the row until the transaction commits.

---

## 5. Authentication & Authorization

### 5.1 Authentication Layers

| Layer | Purpose | Implementation |
|-------|---------|----------------|
| **App Unlock** | Prevent unauthorized app access | Biometric / PIN at launch |
| **Transaction Auth** | Verify user intent for each payment | Biometric re-auth before EVERY payment |
| **Session Auth** | API access | OAuth2 access token (5 min) + refresh token (7 days) |
| **Device Auth** | Bind account to device | Device fingerprint + OTP for new device |

### 5.2 Biometric Authentication Flow

```
User taps "Pay"
  → AuthBloc.add(RequestBiometricAuth(reason: "Confirm ₹500 to John"))
    → local_auth.authenticate(biometricsOnly: true)
      → Success → emit Authenticated → proceed to payment
      → Fail/Unavailable → fall back to PIN
        → PIN entered → POST /api/auth/verify-pin (server-side)
          → Correct → proceed
          → Wrong → increment fail counter
            → 5 wrong → lock app 30 min
```

### 5.3 Token Management

```
Login Flow:
  OTP → verify → server issues:
    access_token  (TTL: 5 min)  → stored in Keystore/Keychain
    refresh_token (TTL: 7 days) → stored in Keystore/Keychain

API Request:
  Authorization: Bearer <access_token>

Token Expired (401):
  → RefreshTokenInterceptor intercepts 401
  → Uses refresh_token to get new access_token
  → Retries original request
  → If refresh fails → force logout, clear tokens

Security:
  → Tokens NEVER in SharedPreferences
  → Tokens NEVER in logs
  → Access token short-lived (limits theft window)
  → Refresh token rotated on each use
```

### 5.4 Device Binding

```
First login:
  → OTP verified
  → Generate device_id (fingerprint: hardware + app signature)
  → Register device_id with server

Subsequent logins:
  → Check device_id matches
  → If new device → OTP + re-KYC → register new device_id
  → If old device → revoke tokens for that device
```

---

## 6. Tokenization & PCI-DSS

### 6.1 What Is Tokenization?

Replace sensitive data (card number, bank account) with a non-sensitive token. The real data is stored in a secure vault; the token is used everywhere else.

```
Real PAN: 4111 1111 1111 1111
Token:    tok_7f3a9b2c1d8e5f6a
```

### 6.2 Why Tokenize?

| Without Tokenization | With Tokenization |
|---------------------|-------------------|
| Store PAN in DB → PCI-DSS scope = entire system | Store token → PCI-DSS scope = vault only |
| DB breach = card numbers leaked | DB breach = useless tokens |
| Heavy compliance burden | Minimal compliance burden |

### 6.3 PCI-DSS Requirements (Summary)

| Requirement | Description |
|-------------|-------------|
| Network security | Firewalls, no default passwords |
| Card data protection | Encrypt at rest, tokenize |
| Vulnerability management | Patch management, AV scanning |
| Access control | Unique IDs, MFA, least privilege |
| Monitoring | Log all access to card data |
| Security policy | Incident response, training |

### 6.4 Mobile-Specific Tokenization

- **Android**: `WalletCard` / HCE token (Google Pay tokenized PAN)
- **iOS**: Apple Pay token (Secure Element stores tokenized PAN)
- **App-level**: Never store raw card numbers — always use payment gateway tokens (Stripe, Braintree, Razorpay)

---

## 7. Encryption (At Rest & In Transit)

### 7.1 In Transit

| Layer | Implementation |
|-------|----------------|
| TLS 1.3 | All API communication |
| Certificate Pinning | Prevent MITM — pin server certificate or public key |
| mTLS (mutual TLS) | Client certificate + server certificate (high-security) |
| Request Signing | HMAC-SHA256(payload, device_secret) — tamper detection |

### 7.2 At Rest (Mobile)

| Data | Storage | Encryption |
|------|---------|------------|
| Transaction history | SQLite (Drift) | SQLCipher (AES-256) |
| Auth tokens | Android Keystore / iOS Keychain | Hardware-backed |
| User preferences | EncryptedSharedPreferences | AES-256 |
| Card tokens | Keystore/Keychain | Hardware-backed |
| PIN | **NEVER stored** | Verified server-side only |

### 7.3 Request Signing (HMAC)

```
1. Build canonical string: method + path + timestamp + body_hash
2. Sign with device_secret_key (stored in Keystore): HMAC-SHA256
3. Add header: X-Request-Signature: <hex_signature>
4. Server verifies signature → detects tampering
```

### 7.4 Key Management

```
Device Secret Key:
  → Generated on first launch
  → Stored in Android Keystore (hardware-backed, never extracted)
  → Used for: request signing, local DB encryption key derivation

Local DB Encryption Key:
  → Derived from: device_secret_key + user_PIN (PBKDF2)
  → If PIN changes → re-encrypt DB with new derived key
```

---

## 8. Fraud Detection & Risk Scoring

### 8.1 Risk Signals

| Signal | Example |
|--------|---------|
| **Velocity** | 10 transactions in 5 min → suspicious |
| **Amount anomaly** | User usually pays ₹500, suddenly pays ₹50,000 |
| **Device trust** | New device, rooted device, emulator |
| **Location anomaly** | Transaction from different country than usual |
| **Time anomaly** | Transaction at 3 AM when user usually transacts at noon |
| **Recipient trust** | New recipient never seen before |
| **Behavioral** | Unusual typing pattern, screen navigation |

### 8.2 Risk Score Model

```
Risk Score = f(velocity, amount, device, location, time, recipient, history)

Score Range:
  0–30  → LOW risk    → auto-approve
  31–70 → MEDIUM risk → challenge (OTP / biometric re-verify)
  71–100→ HIGH risk   → block + notify user
```

### 8.3 Synchronous vs. Asynchronous

| Mode | When | Latency Impact |
|------|------|----------------|
| **Synchronous** | High-risk transactions | Blocks payment until decision |
| **Asynchronous** | Low/medium risk | Runs via Kafka after payment — for post-facto analysis |

### 8.4 Implementation

```
Payment Request → Server
  → Synchronous fraud check (rules + cached ML score) → ~50ms
    → LOW → proceed
    → MEDIUM → challenge (OTP)
    → HIGH → block
  → Async: publish event to Kafka → deep ML analysis → update user risk profile
```

---

## 9. Reconciliation & Settlement

### 9.1 What Is Reconciliation?

Matching internal transaction records with bank/processor records to ensure they agree. Critical for financial integrity.

### 9.2 Reconciliation Flow

```
Daily:
  1. Download bank statement (or API)
  2. For each bank transaction → find matching internal record
  3. Match by: amount, timestamp, reference_id
  4. Categorize:
     → MATCHED ✅
     → UNMATCHED_INTERNAL (internal record, no bank entry) → investigate
     → UNMATCHED_BANK (bank entry, no internal record) → investigate
  5. Generate reconciliation report
  6. Resolve discrepancies (refund, retry, manual review)
```

### 9.3 Settlement

| Concept | Details |
|---------|---------|
| **T+0** | Same-day settlement (UPI) |
| **T+1** | Next business day (cards) |
| **T+2/T+3** | International cards |
| **Settlement file** | Batch file from processor → bank transfer |

### 9.4 Why It Matters

- Unreconciled transactions = money leaks
- Regulatory requirement (RBI, PCI-DSS)
- Audit trail for financial compliance
- Detects: duplicate processing, missed credits, processor errors

---

## 10. Webhooks & Async Status Updates

### 10.1 Why Async?

Bank/UPI processing can take 2–10 seconds. The client should not hold the connection open. Instead:

1. Client initiates payment → server returns `PENDING` + `transaction_id`
2. Server processes asynchronously (bank call, fraud check)
3. Server pushes result to client via WebSocket / FCM / webhook

### 10.2 WebSocket Flow

```
Client connects: wss://api.app.com/ws/payments/{user_id}

Client → POST /api/payments → Server returns { txn_id, status: PENDING }
Client shows "Processing..."

Server (async):
  → Bank processes → result received
  → Server pushes via WebSocket: { txn_id, status: SUCCESS }
  → Client updates UI → receipt screen

If WebSocket disconnects:
  → Fallback: FCM push notification
  → Fallback: client polls GET /api/payments/{txn_id}/status (max 3 polls)
```

### 10.3 Webhook (Server-to-Server)

Used when integrating with payment gateways (Stripe, Razorpay):

```
Gateway → POST your_webhook_url
  Body: { event: "payment.success", txn_id, amount }
  → Verify signature (HMAC)
  → Update internal transaction status
  → Return 200 OK (acknowledge)
  → If not 200 → gateway retries (exponential backoff)
```

### 10.4 Webhook Reliability

- **Signature verification**: prevent spoofed webhooks
- **Idempotent processing**: same webhook may arrive multiple times
- **Retry with backoff**: if your server is down, gateway retries
- **Dead letter queue**: after N retries, move to manual review

---

## 11. Refunds, Chargebacks & Disputes

### 11.1 Refund Flow

```
User requests refund → POST /api/refunds
  → Original txn must be SUCCESS
  → Refund amount ≤ original amount
  → Generate refund_id (linked to original txn_id)
  → Reverse the ledger entries:
    → Debit recipient (original credit reversed)
    → Credit sender (original debit reversed)
  → Bank processes refund → T+1 to T+3
```

### 11.2 Refund States

```
REFUND_INITIATED → REFUND_PROCESSING → REFUND_SUCCESS
                                       → REFUND_FAILED
```

### 11.3 Chargebacks (Card Payments)

```
Cardholder disputes charge with their bank
  → Bank issues chargeback
  → Merchant (you) receives chargeback notification
  → Submit evidence (receipt, delivery proof)
  → Bank rules: 
    → In favor of merchant → chargeback reversed
    → In favor of cardholder → funds returned to cardholder
```

### 11.4 Partial vs. Full Refund

| Type | Rule |
|------|------|
| Full | Refund entire original amount |
| Partial | Refund portion (e.g., damaged item) |
| Multiple partials | Sum of all partials ≤ original amount |

---

## 12. Offline Support & Caching

### 12.1 What Works Offline

| Feature | Offline? | Notes |
|---------|----------|-------|
| View transaction history | ✅ | From encrypted local cache (Drift + SQLCipher) |
| View receipts | ✅ | Cached locally |
| View balance (stale) | ✅ | Last known balance + timestamp |
| Biometric/PIN auth | ✅ | Local verification |
| QR scan | ✅ | Camera + decode works offline |
| Send money | ❌ | Bank communication required |
| Top-up wallet | ❌ | Bank communication required |

### 12.2 Caching Strategy

```
Transaction History:
  → Cache-first: show local cache immediately
  → Background fetch from API
  → Update cache with new data
  → Cursor pagination (don't load all at once)

Balance:
  → Network-first: always try to fetch fresh
  → On failure: show cached + "Last updated: 2 min ago"
  → Never show stale balance without timestamp

Receipts:
  → Permanent local storage (encrypted)
  → Lazy load receipt images from CDN
```

### 12.3 Pending Actions Queue

```
Actions that CAN be queued (non-financial):
  → Request money (sends FCM when online)
  → Update profile
  → Sync contacts

Actions that CANNOT be queued (financial):
  → Send money (must be real-time)
  → Top-up wallet (must be real-time)
  → Refund (must be real-time)
```

---

## 13. Deep Linking & UPI Intent

### 13.1 UPI Deep Link Format

```
upi://pay?pa=merchant@upi&pn=MerchantName&am=100&cu=INR&tn=Order123

Parameters:
  pa  → Payee Address (VPA)
  pn  → Payee Name
  am  → Amount
  cu  → Currency (INR)
  tn  → Transaction Note
  tr  → Transaction Reference ID
  mc  → Merchant Code
  url → Merchant URL
```

### 13.2 Intent Flow (Android)

```kotlin
// Create UPI payment intent
val uri = Uri.parse("upi://pay?pa=merchant@upi&pn=Merchant&am=100&cu=INR&tn=Order123")
val intent = Intent(Intent.ACTION_VIEW).apply {
    data = uri
}
// Launch UPI app chooser
startActivityForResult(intent, UPI_PAYMENT_REQUEST_CODE)

// Handle result
onActivityResult(requestCode, UPI_PAYMENT_REQUEST_CODE, data) {
    val response = data?.getStringExtra("response")
    // Parse: "status=SUCCESS&txnRef=Order123"
}
```

### 13.3 App Link / Universal Link

```
https://app.example.com/pay?txn_id=TXN_001
  → Opens app directly (no disambiguation dialog)
  → Falls back to web if app not installed
```

### 13.4 Deep Link Security

- **Verify source**: only accept deep links from trusted domains
- **Validate parameters**: amount > 0, VPA format valid
- **Prevent deep link hijacking**: use Android App Links / iOS Universal Links (verified association)
- **Never auto-execute payment from deep link**: always show confirmation screen

---

## 14. QR Code Standards & Parsing

### 14.1 UPI QR Format

```
Format: upi://pay?pa=...&pn=...&am=...&cu=INR&tn=...

Static QR:   Fixed merchant VPA + optional amount
Dynamic QR:  Unique per transaction (includes txn reference, expiry)
```

### 14.2 EMVCo QR (Card-based)

```
Format: Starts with "000201" (EMVCo mQR)
  → Contains: merchant ID, MCC, amount, currency
  → Used for cross-border / card-based QR payments
```

### 14.3 Parsing Flow

```
Camera → ML Kit QR detection → raw string
  → Parse scheme:
    → "upi://" → UPI payment flow
    → "000201..." → EMVCo flow
    → Other → show "Unsupported QR"
  → Extract: VPA, name, amount (if present)
  → Pre-fill payment screen
  → If amount missing → user enters amount
  → Biometric auth → payment
```

### 14.4 QR Security

- **Validate VPA format**: regex `^[a-zA-Z0-9.\-_]{2,256}@[a-zA-Z]{2,64}$`
- **Amount validation**: > 0, within daily limit
- **Merchant verification**: check merchant VPA against server whitelist
- **Tamper detection**: signed QR (HMAC in QR payload) for dynamic QRs

---

## 15. Compliance & Regulatory

### 15.1 Key Regulations

| Regulation | Region | Scope |
|-----------|--------|-------|
| **PCI-DSS** | Global | Card data handling |
| **RBI Guidelines** | India | UPI, wallets, data localization |
| **GDPR** | EU | User data privacy |
| **PSD2** | EU | Open banking, SCA (Strong Customer Auth) |
| **CCPA** | California | Data privacy |
| **AML/KYC** | Global | Anti-money laundering, Know Your Customer |

### 15.2 Data Localization (India — RBI)

```
All payment data must be stored in India:
  → Transaction records: Indian data centers
  → User PII: Indian data centers
  → Can process abroad but must store locally
```

### 15.3 KYC Levels

| Level | Requirements | Limits |
|-------|-------------|--------|
| **Minimum KYC** | Phone + self-declaration | ₹10,000/month wallet |
| **Full KYC** | Aadhaar/PAN + biometric | ₹1,00,000+ wallet, all features |

### 15.4 Audit Trail Requirements

- Every transaction: who, what, when, from where
- Immutable audit log (append-only)
- Retention: 7–10 years (varies by regulation)
- Tamper-evident (hash chaining)

---

## 16. Observability & Audit Logging

### 16.1 Key Metrics

| Metric | Target | Why |
|--------|--------|-----|
| Payment success rate | > 99.5% | Core business metric |
| p99 payment latency | < 2s | User experience |
| Bank API latency | < 800ms | External dependency |
| Fraud block rate | < 0.1% | False positives hurt UX |
| Crash-free rate | > 99.9% | App stability |
| Token refresh rate | Monitor | High rate = token issues |

### 16.2 Audit Log Structure

```json
{
  "timestamp": "2026-08-18T17:26:30Z",
  "event_type": "PAYMENT_INITIATED",
  "user_id": "USR_12345",
  "device_id": "DEV_abc123",
  "txn_id": "TXN_001",
  "idempotency_key": "550e8400-...",
  "amount": 500.00,
  "currency": "INR",
  "recipient": "user@upi",
  "payment_method": "UPI",
  "risk_score": 15,
  "ip_address": "10.0.0.1",
  "app_version": "3.2.1"
}
```

### 16.3 What NOT to Log

- ❌ Card numbers (PAN)
- ❌ PINs / passwords
- ❌ Full bank account numbers
- ❌ Auth tokens
- ❌ Biometric data

### 16.4 Structured Logging

```
✅ Good: { "event": "PAYMENT_SUCCESS", "txn_id": "TXN_001", "amount": 500 }
❌ Bad:  "Payment of 500 succeeded for user John Doe (4111...1111)"
```

---

## 17. Testing Strategies for Payments

### 17.1 Test Pyramid

```
        ┌─────────┐
        │   E2E   │  Full payment flow (sandbox bank)
        ├─────────┤
        │Integration│  API + mock bank gateway
        ├─────────┤
        │  Unit   │  Idempotency, validation, state machine
        └─────────┘
```

### 17.2 Critical Test Cases

| Category | Test Case |
|----------|-----------|
| **Idempotency** | Retry with same key → same result, no double charge |
| **Concurrency** | Two simultaneous payments from same account → no double-spend |
| **Timeout** | Payment times out → status check → correct state shown |
| **Bank down** | Bank API returns 500 → queued, user sees "Processing" |
| **Fraud block** | High-risk transaction → blocked, user notified |
| **Biometric fail** | Biometric unavailable → PIN fallback works |
| **Offline** | Send money offline → blocked with message |
| **Double-tap** | Rapid double-tap on Pay → only one transaction |
| **App kill** | Kill app during payment → reopen → correct status shown |
| **Refund** | Refund full + partial → ledger balanced |
| **Reconciliation** | Bank statement matches internal records |

### 17.3 Chaos Testing

- Kill payment service mid-transaction → recovery correct?
- Network partition between app and server → graceful degradation?
- Redis (idempotency cache) down → fallback to DB?
- Bank gateway slow (10s) → timeout handling correct?

### 17.4 Security Testing

- Penetration testing (OWASP MASVS)
- Certificate pinning bypass attempts
- Root/jailbreak detection bypass
- SQLCipher key extraction attempts
- Replay attack (capture + replay signed request)
- Tampered request (modify body, keep signature → should fail)

---

## 18. Common Pitfalls & Anti-Patterns

### 18.1 ❌ Auto-retrying Payments

```
WRONG: Payment times out → auto-retry → double charge
RIGHT: Payment times out → check status → retry only if FAILED
```

### 18.2 ❌ Storing Sensitive Data Locally

```
WRONG: Store card number in SharedPreferences
RIGHT: Tokenize → store token in Keystore → never store PAN
```

### 18.3 ❌ No Idempotency Key

```
WRONG: POST /payments (no key) → retry → double charge
RIGHT: POST /payments with Idempotency-Key header → server deduplicates
```

### 18.4 ❌ Balance Check Without Lock

```
WRONG: SELECT balance → check → UPDATE (race condition → double spend)
RIGHT: SELECT ... FOR UPDATE → check → UPDATE (row locked)
```

### 18.5 ❌ Trusting Client-Side Validation

```
WRONG: Client validates amount > 0 → server trusts it
RIGHT: Server validates ALL inputs independently
```

### 18.6 ❌ Showing "Failed" on Timeout

```
WRONG: Timeout → show "Payment Failed" (might have succeeded!)
RIGHT: Timeout → show "Status Unknown — Check History"
```

### 18.7 ❌ Long-lived Access Tokens

```
WRONG: Access token valid for 30 days
RIGHT: Access token 5 min + refresh token 7 days (limits theft window)
```

### 18.8 ❌ No Reconciliation

```
WRONG: Trust that all transactions processed correctly
RIGHT: Daily reconciliation with bank → catch discrepancies
```

### 18.9 ❌ Logging Sensitive Data

```
WRONG: Log("Payment of ₹500 from card 4111...1111 to John")
RIGHT: Log("PAYMENT_SUCCESS txn_id=TXN_001 amount=500")
```

### 18.10 ❌ No Device Binding

```
WRONG: Same account on unlimited devices → stolen credentials = full access
RIGHT: One device per account → new device requires OTP + re-KYC
```

---

## Quick Reference: Payment Implementation Checklist

- [ ] Idempotency key on every payment request
- [ ] No auto-retry for payments (check status first)
- [ ] Biometric re-auth before every payment
- [ ] Tokens in Keystore/Keychain (never SharedPreferences)
- [ ] SQLCipher for local DB
- [ ] SSL pinning on all endpoints
- [ ] Request signing (HMAC-SHA256)
- [ ] Double-entry ledger with `SELECT FOR UPDATE`
- [ ] Transaction state machine (no skipped states)
- [ ] Fraud detection (sync for high-risk, async for low-risk)
- [ ] Webhook signature verification
- [ ] Daily reconciliation with bank
- [ ] No sensitive data in logs
- [ ] Root/jailbreak detection
- [ ] FLAG_SECURE (block screenshots)
- [ ] Offline: read-only cache, no offline payments
- [ ] Audit log (immutable, append-only)
- [ ] E2E tests with sandbox bank
- [ ] Chaos testing (service down, network partition)
- [ ] Compliance: PCI-DSS, RBI, data localization

---

[← Back to Mobile System Design](../README.md) | [Payment Flows →](./Mobile_Payment_Flows.md)
