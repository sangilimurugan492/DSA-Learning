# SSL Pinning

## Q1: What is SSL/TLS and why is it not enough?

SSL/TLS encrypts traffic between app and server. But it's vulnerable to **Man-in-the-Middle (MITM)** attacks.

### How MITM works
```
App ──► [Attacker Proxy] ──► Server
         Attacker has their own cert
         App trusts it because system trust store accepts it
```

### Why system trust store isn't enough
| Issue | Description |
|-------|-------------|
| User-installed CAs | Users can install custom CAs (Burp, Charles) |
| Corporate CAs | MDM tools push corporate root CAs |
| Compromised CAs | A CA can be hacked or coerced |
| Android user CA store | Apps trust user-installed CAs by default (pre-Android 7) |

### Solution: SSL Pinning
Pin the server's certificate or public key so the app **only** trusts your server — not any CA.

---

## Q2: How do you implement certificate pinning with OkHttp?

```kotlin
val client = OkHttpClient.Builder()
    .certificatePinner(
        CertificatePinner.Builder()
            .add("api.example.com", "sha256/ABC123...=")  // Primary pin
            .add("api.example.com", "sha256/DEF456...=")  // Backup pin
            .build()
    )
    .build()
```

### How to get the pin
```bash
# Get the pin from your server's certificate
echo | openssl s_client -connect api.example.com:443 2>/dev/null | \
  openssl x509 -pubkey -noout | \
  openssl pkey -pubin -outform der | \
  openssl dgst -sha256 -binary | \
  openssl enc -base64
# Output: sha256/ABC123...=
```

### Or programmatically
```kotlin
// Log pins for debugging
fun printCertificatePins(url: String) {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    client.newCall(request).execute().use { response ->
        val pins = response.handshake?.peerCertificates?.map { cert ->
            CertificatePinner.pin(cert)
        }
        println("Pins: $pins")
    }
}
```

---

## Q3: What are the types of pinning?

| Type | What's Pinned | Pros | Cons |
|------|--------------|------|------|
| Certificate Pin | Full certificate | Simple | Breaks on cert renewal |
| Public Key Pin | Public key only | Survives cert renewal | Still breaks on key rotation |
| SPKI Pin | Subject Public Key Info | Most flexible | Slightly more complex |
| Hash Pin | SHA-256 of SPKI | Compact, standard | Need to compute hash |

### Recommended: SPKI hash pinning
```kotlin
// Pin the public key hash, not the certificate
// This survives certificate renewal (same key pair)
CertificatePinner.Builder()
    .add("api.example.com", "sha256/ABC123...=")  // SPKI hash
    .add("api.example.com", "sha256/DEF456...=")  // Backup (different key)
```

### Always have a backup pin
```kotlin
// If you only pin one key and it rotates → app breaks
// Always pin:
// 1. Current key
// 2. Backup key (different key pair, stored securely)
// When rotating: switch to backup, add new backup
```

---

## Q4: How do you use Network Security Config?

```xml
<!-- res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <!-- Pin for production domain -->
    <domain-config>
        <domain includeSubdomains="true">api.example.com</domain>
        <pin-set expiration="2025-12-31">
            <pin digest="SHA-256">ABC123...=</pin>
            <pin digest="SHA-256">DEF456...=</pin>  <!-- Backup -->
        </pin-set>
    </domain-config>

    <!-- Allow cleartext for localhost (debugging) -->
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="false">localhost</domain>
        <domain includeSubdomains="false">10.0.2.2</domain>
    </domain-config>

    <!-- Trust user CAs only in debug -->
    <debug-overrides>
        <trust-anchors>
            <certificates src="user" />
            <certificates src="system" />
        </trust-anchors>
    </debug-overrides>
</network-security-config>
```

### Manifest reference
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ... >
</application>
```

### Key features
| Feature | Description |
|---------|-------------|
| `pin-set expiration` | Pin expires on date (forces app update) |
| `debug-overrides` | Trust user CAs only in debug builds |
| `cleartextTrafficPermitted` | Allow HTTP for specific domains |
| `domain includeSubdomains` | Apply to subdomains too |

---

## Q5: How do you handle certificate rotation?

### Problem
Your server's certificate expires. If you pinned the certificate (not the public key), the app breaks.

### Solution: Pin the public key + backup
```kotlin
// 1. Pin current public key
// 2. Pin backup public key (different key pair)
// 3. When rotating:
//    a. Server switches to backup key
//    b. App still works (backup pin matches)
//    c. Push app update with new primary + new backup
//    d. Server switches to new primary key
//    e. App still works (new primary pin matches)
```

### Rotation timeline
```
Week 1:  Server uses Key A | App pins: A (primary), B (backup)
Week 2:  Server switches to Key B | App pins: A, B → works (B matches)
Week 3:  Push app update | App pins: B (primary), C (new backup)
Week 4:  Server switches to Key C | App pins: B, C → works (C matches)
```

### Expiration safety
```xml
<!-- Set expiration so old pins stop working -->
<pin-set expiration="2025-12-31">
    <pin digest="SHA-256">ABC123...=</pin>
</pin-set>
<!-- After 2025-12-31, pin is ignored → app can connect with new cert -->
```

---

## Q6: How do you debug SSL pinning issues?

### Common issues
| Issue | Cause | Fix |
|-------|-------|-----|
| `SSLPeerUnverifiedException` | Pin mismatch | Verify pin hash is correct |
| Connection works in debug, fails in release | Debug overrides | Check `debug-overrides` config |
| App breaks after cert renewal | Pinned certificate, not key | Pin public key instead |
| All requests fail | Wrong domain in pin | Check domain matches exactly |

### Debug logging
```kotlin
// Enable OkHttp logging to see handshake details
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.HEADERS
}

// Custom interceptor to log certificate info
class SslDebugInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        response.handshake?.let { handshake ->
            Log.d("SSL", "Cipher: ${handshake.cipherSuite}")
            Log.d("SSL", "Peer certs: ${handshake.peerCertificates.map { it.subjectX500Principal }}")
        }
        return response
    }
}
```

### Testing with Charles/Burp
```xml
<!-- Allow proxy in debug only -->
<debug-overrides>
    <trust-anchors>
        <certificates src="user" />  <!-- Trust Charles/Burp CA -->
    </trust-anchors>
</debug-overrides>
```

---

## Q7: What are SSL pinning best practices?

### Do's
- ✅ Pin the **public key** (SPKI hash), not the certificate
- ✅ Always have a **backup pin** (different key pair)
- ✅ Use **Network Security Config** (native, no library needed)
- ✅ Set **expiration dates** on pins
- ✅ Test pinning with Charles/Burp in debug
- ✅ Pin all API domains (not just the main one)

### Don'ts
- ❌ Don't pin the certificate (breaks on renewal)
- ❌ Don't have only one pin (no backup = app breaks on rotation)
- ❌ Don't hardcode pins in code (use XML config)
- ❌ Don't trust user CAs in release builds
- ❌ Don't forget to update pins before they expire
- ❌ Don't pin CDN domains (they rotate frequently)

### Pinning checklist
- [ ] Pin public key hash (SPKI), not certificate
- [ ] At least 2 pins (primary + backup)
- [ ] Set expiration date
- [ ] Test in debug with proxy
- [ ] Test in release without proxy
- [ ] Document rotation process
- [ ] Monitor pin expiration dates

---

## 🔗 Related Topics
- [Keystore](Keystore.md)
- [Data Encryption](DataEncryption.md)
- [Security Scenarios](SecurityScenarios.md)
