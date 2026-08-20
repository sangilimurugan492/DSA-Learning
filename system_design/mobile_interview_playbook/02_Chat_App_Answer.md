# Structured Interview Answer: Design a Chat Application

> **Question**: *"Design a mobile chat application like WhatsApp/Telegram."*

---

## Step 1 — Clarify Requirements

**Questions:**
- Android, iOS, or cross-platform (Flutter)?
- One-on-one chat, group chat, or both?
- Media support (images, video, voice)?
- End-to-end encryption required?
- Offline message support?
- Read receipts, typing indicators?
- Push notifications?

**Assumed answers:** Flutter, both 1:1 and group, media yes, E2E encryption yes, offline yes, read receipts yes, push yes.

---

## Step 2 — Define Scope

```
IN SCOPE: 1:1 chat, group chat, media sharing, E2E encryption, offline messages, read receipts, push notifications
OUT OF SCOPE: Voice/video calls, status/stories, admin moderation tools
```

---

## Step 3 — Constraints

```
Functional: Send/receive text & media, create groups, read receipts, typing indicators
Non-Functional: 50M users, ~5M concurrent, < 200ms message delivery, E2E encrypted, offline-capable
```

---

## Step 4 — High-Level Architecture

```
                    ┌──────────────┐
                    │  Mobile App  │
                    │   Flutter    │
                    └──────┬───────┘
                           │
              WebSocket (persistent) + REST (media upload)
                           │
                    ┌──────▼───────┐
                    │ API Gateway  │
                    └──────┬───────┘
                           │
       ┌───────────────────┼───────────────────┐
       │                   │                   │
  Message Service    Media Service     Notification Service
  (WebSocket Server)  (S3 + CDN)       (FCM/APNS)
       │                   │                   │
  Message Store      Object Storage     Push Queue
  (MongoDB/Cassandra)                    (Kafka)
```

> "WebSocket for real-time message delivery. REST for media upload (presigned URLs to S3). FCM/APNS for push when app is in background."

---

## Step 5 — Mobile Architecture

```
┌─────────────────────────────┐
│            UI               │
│  ChatScreen, MessageBubble,  │
│  TypingIndicator             │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│          BLoC               │
│  ChatBloc, MessageBloc,     │
│  ContactBloc                │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│         Use Cases           │
│  SendMessageUseCase          │
│  ReceiveMessageUseCase       │
│  SyncMessagesUseCase         │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│      Repository             │
│  MessageRepository           │
│  ChatRepository              │
└──────────┬───────────┬──────┘
           ↓           ↓
    WebSocket +     Local DB
    REST API       (Drift/SQLite)
```

---

## Step 6 — Data Flow

### Sending a Message:
```
User types & sends message
  ↓
MessageBloc → SendMessageUseCase
  ↓
Save to local DB with status = PENDING
  ↓
UI shows message immediately (optimistic) with clock icon
  ↓
Send via WebSocket
  ↓
Server confirms receipt → status = SENT (single tick)
  ↓
Recipient receives → status = DELIVERED (double tick)
  ↓
Recipient reads → status = READ (blue tick)
  ↓
Update local DB → UI updates icon
```

### Receiving a Message:
```
WebSocket receives message
  ↓
Decrypt (E2E)
  ↓
Save to local DB
  ↓
MessageBloc emits NewMessageState
  ↓
UI renders message
  ↓
If app in background → FCM push notification
```

---

## Step 7 — Networking

```
WebSocket:
  - Persistent connection for real-time messages
  - Heartbeat/ping every 30s to keep alive
  - Auto-reconnect with backoff (1s → 2s → 4s → 8s → 16s)
  - On reconnect: sync missed messages (since last_message_id)

REST:
  - Media upload: presigned S3 URL, upload directly to S3
  - Media download: CDN URL with cache headers
  - Message history: GET /messages?conversation_id=X&cursor=Y

Timeouts:
  WebSocket: 30s heartbeat timeout
  REST upload: 60s (large media)
  REST download: 30s
```

**WebSocket Reconnection:**
```
Connection lost
  ↓
Mark as disconnected (UI shows "Connecting...")
  ↓
Wait 1s → reconnect
  ↓
Failed? Wait 2s → reconnect
  ↓
Failed? Wait 4s → reconnect (max 30s backoff)
  ↓
On reconnect: GET /messages/sync?since=last_message_id
  ↓
Process any missed messages
```

---

## Step 8 — Offline Support & Sync

```
User sends message while offline
  ↓
Save to local DB with status = PENDING
  ↓
UI shows message with clock icon
  ↓
Network restored
  ↓
WebSocket reconnects
  ↓
Send pending messages in order
  ↓
Update status to SENT
```

**Message Status State Machine:**
```
PENDING → SENT → DELIVERED → READ
  ↑(offline, queued)  ↑(server ack)  ↑(recipient ack)  ↑(recipient read)
```

**Media Offline:**
- Compress image/video before upload
- Queue upload in WorkManager
- Upload to S3 via presigned URL when network restores

---

## Step 9 — Performance

| Category | Techniques |
|----------|-----------|
| **Startup** | Lazy load chat list from local DB first, sync in background |
| **UI** | ListView.builder for message list (virtualization), const MessageBubble widgets |
| **Network** | WebSocket for messages (no polling), media compression before upload |
| **Memory** | Image cache per chat, dispose WebSocket on chat close, stream subscriptions |
| **Battery** | WebSocket heartbeat 30s (not aggressive), FCM for background (no polling) |

---

## Step 10 — Security

```
End-to-End Encryption (E2E):
  → Each user has a key pair (public/private)
  → Public key shared with contacts via key server
  → Messages encrypted with recipient's public key
  → Only recipient's private key can decrypt
  → Private key stored in Keystore/Keychain (never leaves device)

Signal Protocol (or simplified):
  → X3DH for initial key exchange
  → Double Ratchet for forward secrecy
  → Each message has unique key

Other:
  → TLS 1.2+ for transport (even with E2E, defense in depth)
  → Certificate pinning on WebSocket + REST
  → Media encrypted before upload to S3
  → No server can read message content
```

> "E2E encryption means the server is just a relay — it cannot read message content. Only the recipient's device can decrypt."

---

## Step 11 — Scalability

- WebSocket connection pooling (sticky sessions on load balancer)
- Message history: cursor pagination by message_id (not offset)
- Media: S3 + CDN, presigned URLs (mobile uploads directly to S3, not through API)
- Message store: Cassandra (write-optimized, scales horizontally)
- Push: Kafka queue → FCM/APNS workers (decouple from message service)

---

## Step 12 — Failure Scenarios

| Failure | Response |
|---------|----------|
| WebSocket disconnects | Auto-reconnect with backoff, sync missed messages |
| App in background | FCM push for new messages |
| Server down | Messages queued locally, sent on reconnect |
| Media upload fails | Retry in WorkManager, show retry icon |
| E2E key exchange fails | Show "Security alert" to user |
| Message ordering | Server assigns sequence number per conversation |
| Duplicate messages | Dedup by message_id on client |

---

## Step 13 — Trade-offs

| Decision | Choice | Alternative | Why |
|----------|--------|-------------|-----|
| Real-time | WebSocket | Long polling | WebSocket is bidirectional, lower latency, less battery |
| E2E | Signal Protocol | Server-side encryption | Privacy guarantee, server can't read messages |
| Message store | Cassandra | PostgreSQL | Write-heavy workload, horizontal scaling |
| Media upload | Presigned S3 URL | Through API server | Reduces API server load, direct to storage |
| Local DB | Drift/SQLite | Hive | Relational queries for message ordering, SQL power |

---

## Step 14 — Testing & Observability

| Level | What |
|-------|------|
| Unit | Encryption/decryption, message status transitions, BLoC logic |
| Integration | WebSocket mock server, message sync flow |
| E2E | Send message → receive → read receipt (two-device test) |
| Crash | Crashlytics (sanitize — never log message content) |
| Observability | WebSocket connection uptime, message delivery latency p95 |
