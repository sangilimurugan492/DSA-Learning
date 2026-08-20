# Example: Design a Chat Application (WhatsApp-style)

> **Full 60-minute mock interview answer using the 14-step framework.**
>
> **Prompt**: *"Design a mobile chat application like WhatsApp for 50 million users."*

---

## Step 1 — Clarify Requirements (0–5 min)

**You ask:**
1. Platform? → *"Both Android and iOS, Flutter cross-platform."*
2. 1-on-1 chats only or group chats? → *"Both, groups up to 256 members."*
3. Media sharing? → *"Yes, images, videos, voice notes, documents."*
4. Real-time delivery? → *"Yes, messages should appear instantly."*
5. Offline support? → *"Yes, user can compose messages offline; send when online."*
6. Read receipts / typing indicators? → *"Yes, both."*
7. End-to-end encryption? → *"Yes, E2E encryption required."*
8. Push notifications? → *"Yes, for incoming messages when app is in background."*

**Summary:**
- **Functional**: 1-on-1 chat, group chat, media sharing, read receipts, typing indicators, push notifications
- **Non-functional**: Real-time delivery (<1s), E2E encryption, offline compose, 50M users, ~5M DAU

---

## Step 2 — Define Scope (5 min)

**In scope:**
- 1-on-1 and group messaging
- Text, image, video, voice note, document sharing
- Read receipts, typing indicators, online presence
- Push notifications
- Offline message composition and queue

**Out of scope (for this interview):**
- Voice/video calls (mention as future)
- Status/stories (mention as future)
- Message search across history (mention as future)

---

## Step 3 — Identify Constraints (5 min)

```
50M total users, ~5M DAU
~20M messages/day
Avg message size: 1KB (text), 2MB (image), 10MB (video)
Peak concurrent: ~500K WebSocket connections
Message delivery latency: <1 second
Media stored: S3/GCS via CDN
Device constraints: must work on 2GB RAM low-end Android
```

---

## Step 4 — High-Level Architecture (5–10 min)

```
┌───────────────────────────────────────────────────────┐
│                  Mobile App (Flutter)                   │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────────┐ │
│  │ Chat List│ │  Chat    │ │  Media   │ │  Settings │ │
│  │  Screen  │ │  Screen  │ │  Picker  │ │  Screen   │ │
│  └─────┬────┘ └────┬─────┘ └─────┬────┘ └─────┬─────┘ │
│        └───────────┴────────────┴─────────────┘       │
│                    │                                    │
│         ┌──────────┼──────────┐                        │
│         ▼          ▼          ▼                        │
│    WebSocket    REST API   Local DB                    │
│   (real-time)   (media,    (Drift -                    │
│    client       presence,   messages,                  │
│                 history)   queue)                      │
└──────────────┬────────────────────────────────────────┘
               │
        ┌──────▼───────┐
        │ API Gateway  │ (REST: auth, media, history)
        │ + WS Gateway │ (WebSocket: real-time messages)
        └──────┬───────┘
       ┌───────┼───────┐
       ▼       ▼       ▼
  ┌────────┐ ┌──────┐ ┌──────────┐
  │ Message│ │Media │ │ Presence │
  │ Service│ │Service│ │ Service  │
  └───┬────┘ └──┬───┘ └────┬─────┘
      │         │          │
  ┌───▼────┐ ┌──▼────┐ ┌───▼─────┐
  │Cassandra│ │ S3/   │ │  Redis  │
  │(msgs)  │ │ GCS   │ │(presence)│
  └────────┘ │ + CDN │ └─────────┘
             └──────┘
  
  Kafka ← Event stream for message delivery, push notifications
  FCM/APNS ← Push notifications
```

---

## Step 5 — Mobile Architecture (10–25 min)

### Flutter Clean Architecture for Chat:

```
┌─────────────────────────────────────────────┐
│              Presentation                     │
│  ┌───────────────────────────────────────┐   │
│  │  Widgets (ChatListPage, ChatPage)      │   │
│  └────────────────┬──────────────────────┘   │
│                   ↓                           │
│  ┌───────────────────────────────────────┐   │
│  │  BLoC (ChatListBloc, ChatBloc,         │   │
│  │        PresenceBloc)                   │   │
│  │  Events: SendMessage, MessageReceived,│   │
│  │         LoadHistory, TypingStarted     │   │
│  │  States: ChatLoaded, MessageSent,     │   │
│  │         Typing, Error                  │   │
│  └────────────────┬──────────────────────┘   │
└───────────────────┼───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                Domain                         │
│  ┌───────────────────────────────────────┐   │
│  │  Use Cases                              │   │
│  │  - SendMessageUseCase                   │   │
│  │  - ReceiveMessageUseCase                │   │
│  │  - LoadChatHistoryUseCase               │   │
│  │  - UploadMediaUseCase                   │   │
│  │  - MarkAsReadUseCase                    │   │
│  └────────────────┬──────────────────────┘   │
│                   ↓                           │
│  ┌───────────────────────────────────────┐   │
│  │  Repository Interfaces                  │   │
│  │  - MessageRepository (abstract)         │   │
│  │  - MediaRepository (abstract)          │   │
│  │  - PresenceRepository (abstract)        │   │
│  └────────────────┬──────────────────────┘   │
└───────────────────┼───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                  Data                         │
│  ┌──────────────┐  ┌──────────────────────┐  │
│  │ Real-time     │  │ Local (Drift)        │  │
│  │ - WebSocket   │  │ - MessageDao         │  │
│  │   Client      │  │ - ConversationDao    │  │
│  │ - Presence    │  │ - PendingQueueDao     │  │
│  └──────────────┘  └──────────────────────┘  │
│  ┌──────────────┐  ┌──────────────────────┐  │
│  │ Remote (REST) │  │ Media                │  │
│  │ - MessageApi  │  │ - UploadApi (S3)     │  │
│  │ - MediaApi    │  │ - ImageCacheManager   │  │
│  └──────────────┘  └──────────────────────┘  │
└─────────────────────────────────────────────┘
```

### WebSocket Client Architecture:

```
┌──────────────────────────────────────────────┐
│              WebSocketManager                 │
├──────────────────────────────────────────────┤
│  Responsibilities:                             │
│  - Maintain persistent WebSocket connection    │
│  - Auto-reconnect with backoff                │
│  - Heartbeat/ping-pong (every 30s)            │
│  - Message serialization/deserialization      │
│  - Stream-based event broadcast to BLoCs      │
│  - Connection state stream (connected/        │
│    disconnected/reconnecting)                  │
├──────────────────────────────────────────────┤
│  Events (server → client):                    │
│  - NEW_MESSAGE                                │
│  - MESSAGE_DELIVERED                          │
│  - MESSAGE_READ                               │
│  - TYPING_INDICATOR                           │
│  - PRESENCE_UPDATE                            │
│  - GROUP_UPDATE                               │
├──────────────────────────────────────────────┤
│  Events (client → server):                   │
│  - SEND_MESSAGE                               │
│  - ACK_MESSAGE                                │
│  - MARK_READ                                  │
│  - TYPING                                     │
└──────────────────────────────────────────────┘
```

### Why (Lead-Level Justification):

> *"I use Clean Architecture to separate concerns: WebSocket handling is in the data layer, business logic in Use Cases, and presentation in BLoC. The WebSocketManager is a singleton that broadcasts events as streams — BLoCs subscribe to relevant streams. This allows the ChatBloc to react to new messages while the PresenceBloc independently tracks online status. The Repository abstraction means I can test the entire flow with a mock WebSocket."*

---

## Step 6 — Data Flow (25–30 min)

### Send Message Flow:

```
ChatPage (user taps send)
  → ChatBloc.add(SendMessage(text, conversationId))
    → SendMessageUseCase.call()
      → Generate message_id (UUID)
      → Generate idempotency_key (UUID)
      → Save to Drift (messages table, status = PENDING)
      → ChatBloc emits MessageSent (optimistic UI update)
      → MessageRepository.send(message, idempotency_key)
        → If WebSocket connected:
            → Send via WebSocket (with idempotency_key)
        → If WebSocket disconnected:
            → Enqueue in pending_queue table
            → WorkManager will retry when connected
      → Server ACK:
            → Update message status = DELIVERED
            → ChatBloc emits MessageDelivered
```

### Receive Message Flow:

```
WebSocketManager receives NEW_MESSAGE event
  → Broadcast to MessageRepository
    → Save to Drift (messages table)
    → Update conversation's last_message
    → Notify ChatBloc (if conversation is open) → emit MessageReceived
    → Notify ChatListBloc → update chat list preview
    → If app in background:
        → Trigger local notification with message preview
```

### Message Status Lifecycle:

```
PENDING → SENT → DELIVERED → READ
  │
  └─ If offline: stays PENDING until WebSocket reconnects
     WorkManager flushes pending queue on reconnect
```

---

## Step 7 — Networking (30–35 min)

### Dual-Channel Strategy:

| Channel | Protocol | Use Case |
|---------|----------|----------|
| **WebSocket** | WSS | Real-time messages, typing, presence, read receipts |
| **REST** | HTTPS | Media upload/download, chat history pagination, profile |
| **FCM/APNS** | Push | Background notifications |

### WebSocket Connection Management:

```dart
class WebSocketManager {
  // Reconnection with exponential backoff
  // 1s → 2s → 4s → 8s → 16s → 30s (cap)
  
  // Heartbeat: ping every 30s, expect pong within 10s
  // If no pong → assume dead → reconnect
  
  // Connection states:
  // CONNECTED → DISCONNECTED → RECONNECTING → CONNECTED
  
  // On reconnect:
  // 1. Re-subscribe to active conversations
  // 2. Request missed messages (using last_message_id)
  // 3. Flush pending queue
}
```

### REST API Design:

```
GET  /api/conversations                    → List conversations (paginated)
GET  /api/conversations/{id}/messages      → Message history (cursor pagination)
POST /api/media/upload                     → Upload media (presigned URL → S3)
GET  /api/media/{id}                       → Download media (CDN URL)
PUT  /api/messages/{id}/read               → Mark message as read
```

### Media Upload Flow:

```
1. Client requests presigned URL: POST /api/media/upload { type, size }
2. Server returns: { upload_url, media_id }
3. Client uploads directly to S3: PUT { upload_url } (binary)
4. Client sends message with media_id via WebSocket
5. Server verifies upload, processes (thumbnail, transcoding)
6. Server broadcasts message to recipients
```

> **Why presigned URLs?** Offloads media upload traffic from API servers directly to S3. Reduces backend load and improves upload speed.

---

## Step 8 — Offline Support & Sync (35–40 min)

### Offline Message Queue (Drift):

```sql
CREATE TABLE pending_messages (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  message_id TEXT,           -- UUID
  idempotency_key TEXT,      -- UUID for dedup
  conversation_id TEXT,
  payload TEXT,              -- JSON (text, media_id, etc.)
  status TEXT,              -- PENDING, SENT, FAILED
  created_at INTEGER,
  retry_count INTEGER DEFAULT 0
);
```

### Offline Behavior:

```
[Offline] User composes message
  → Save to messages table (status = PENDING)
  → Save to pending_messages table
  → Show in chat with clock icon (pending)
  → No retry attempt (WebSocket disconnected)

[WebSocket reconnects]
  → Flush pending_messages in order (ORDER BY created_at)
  → Send each via WebSocket with idempotency_key
  → Update message status to SENT
  → Remove from pending_messages
```

### Missed Messages:

```
On WebSocket reconnect:
  → Send: { type: SYNC_REQUEST, last_message_id: <last_received_id> }
  → Server responds with all messages after last_message_id
  → Client saves to Drift, updates UI
  → This fills any gap during disconnection
```

### Media Offline:
- Images/videos are downloaded lazily
- Once downloaded, cached locally (Drift path + file on disk)
- On offline view: show cached thumbnail, allow tap to view cached full image
- If not cached and offline: show placeholder "Tap to download when online"

---

## Step 9 — Performance (40–45 min)

| Category | Optimization |
|----------|-------------|
| **Startup** | Lazy load: only WebSocket + Drift on launch. Chat history loads on screen open. Defer analytics 3s. |
| **UI** | `ListView.builder` with `AutomaticKeepAliveClientMixin` for chat list. `CustomScrollView` for chat page with `ScrollController` for auto-scroll. `const` widgets. `RepaintBoundary` around message bubbles. |
| **Network** | WebSocket multiplexing (single connection for all chats). Cursor-based history pagination (50 messages/page). Media: progressive JPEG, WebP for thumbnails. |
| **Memory** | Chat page: keep only last 200 messages in memory. Older messages lazy-loaded on scroll up. Image cache: 100MB. Voice note: stream playback, don't load entire file. |
| **Battery** | WebSocket heartbeat 30s (not 5s). Presence updates batched (update every 60s, not real-time). FCM for background notifications (no polling). Media download only on Wi-Fi (optional setting). |

### Chat List Optimization:
- Conversation preview: store `last_message` + `unread_count` in Drift
- Chat list reads entirely from local DB — no API call needed
- WebSocket updates local DB → BLoC reacts → UI updates
- **Zero network calls to render chat list** (fully offline-capable)

---

## Step 10 — Security (45–50 min)

### End-to-End Encryption (E2EE):

```
Key Exchange (Signal Protocol / X3DH):
  1. Each user has: identity key pair + signed prekey + one-time prekeys
  2. To send first message: Alice fetches Bob's prekey bundle from server
  3. Alice derives shared secret using X3DH
  4. All subsequent messages use ratcheting (Double Ratchet algorithm)
  5. Each message has unique key → forward secrecy

Message encryption:
  Plaintext → AES-256-GCM (with session key) → Ciphertext
  Ciphertext → sent via WebSocket → server CANNOT read (no key)

Media encryption:
  1. Encrypt media on device: AES-256 with random key
  2. Upload encrypted blob to S3
  3. Share decryption key via E2E message (separate from media)
  4. Recipient downloads encrypted blob → decrypt locally
```

### Other Security Measures:

| Topic | Implementation |
|-------|---------------|
| **Auth** | OAuth2, access token in Keystore/Keychain |
| **Transport** | TLS 1.2+ for REST, WSS for WebSocket |
| **Certificate Pinning** | Pin API gateway + WebSocket gateway certs |
| **Local DB** | Encrypted with SQLCipher (Drift + SQLCipher) |
| **Key Storage** | E2E keys in Keystore (hardware-backed on supported devices) |
| **Screen Security** | `FLAG_SECURE` (Android) / screenshot prevention (iOS) to block screen recording |
| **Biometric Lock** | Optional app lock with biometric (fingerprint/face) |
| **No PII in logs** | Debug-only logging, message content never logged |

---

## Step 11–12 — Scalability & Failure Scenarios (50–55 min)

### Scalability:
- WebSocket Gateway: horizontally scalable with sticky sessions (connection affinity)
- Message Service: stateless, auto-scaling behind load balancer
- Cassandra: partitioned by `conversation_id` for even distribution
- Redis: presence state with TTL (auto-expire offline users)
- Kafka: async event processing for push notifications, analytics
- CDN: media distribution globally

### Failure Scenarios:

| Scenario | Handling |
|----------|---------|
| WebSocket disconnects | Auto-reconnect with backoff. Messages queued locally. Sync on reconnect. |
| WebSocket server down | Multiple WS gateway instances behind LB. Client reconnects to healthy node. |
| Message delivery fails | Stored in pending queue. WorkManager retries with backoff. |
| Media upload fails | Retry 3x. If still failing, mark media as failed, allow user to resend. |
| Server doesn't ACK message | Client retransmits with same idempotency_key. Server deduplicates. |
| App killed in background | FCM/APNS delivers push notification. On app open, sync missed messages. |
| Local DB corruption | Detect corruption, rebuild from server (fetch history via REST). |
| Device change | E2E keys cannot be transferred (by design). New device = new key pair. Old messages inaccessible (security feature). |

---

## Step 13–14 — Trade-offs & Testing (55–60 min)

### Trade-offs:

| Decision | Trade-off |
|----------|-----------|
| WebSocket vs polling | WebSocket: real-time, persistent connection, battery-efficient. Polling: simpler but latency + battery cost. Chose WebSocket. |
| E2E encryption | Strong privacy, but no server-side search, no cloud backup of messages (or encrypted backup). Device change = lost history. |
| Cassandra vs PostgreSQL | Cassandra: write-optimized, scales horizontally. PostgreSQL: ACID, but harder to shard at 20M msgs/day. Chose Cassandra for message store. |
| Single WebSocket vs multiple | Single connection: less resource usage, simpler. Multiple: isolation but more battery. Chose single multiplexed connection. |
| Local DB encryption (SQLCipher) | Security benefit, but ~10-15% performance overhead. Acceptable for chat. |
| Presigned URL media upload | Offloads traffic from API, but adds complexity (two-step upload). Worth it at scale. |

### Testing & Observability:

| Type | Scope |
|------|-------|
| **Unit** | Use Cases, Repository, BLoC, WebSocket message parsing, encryption/decryption |
| **Integration** | WebSocketManager + mock server, Drift repository with in-memory DB |
| **E2E** | Two-device test: send message → receive → read receipt → typing indicator |
| **Performance** | Message send latency, WebSocket reconnect time, large group (256 members) message fan-out |
| **Observability** | Crashlytics (crashes), Firebase Performance (WS latency), custom metrics (message delivery rate, pending queue depth) |

---

## Summary

> *"The app uses Flutter Clean Architecture with BLoC. Real-time communication is via a single multiplexed WebSocket with auto-reconnect and heartbeat. Messages are stored locally in Drift (with SQLCipher encryption) and synced on reconnect using a pending queue with idempotency keys. All messages are end-to-end encrypted using the Signal Protocol (X3DH + Double Ratchet). Media is encrypted on-device and uploaded via presigned URLs to S3/CDN. The chat list is fully offline-capable — it reads from local DB with zero network calls. Push notifications via FCM/APNS ensure delivery when the app is backgrounded."*

---

[← Back to Guide](./Mobile_System_Design_Interview_Guide.md)
