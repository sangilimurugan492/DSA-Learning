# WebRTC Basics

## Q1: What is WebRTC?

WebRTC (Web Real-Time Communication) is a free, open-source project that enables real-time audio, video, and data communication between browsers and apps without plugins.

### Key characteristics
| Feature | Description |
|---------|-------------|
| P2P | Direct peer-to-peer connection (no server in media path) |
| Real-time | Sub-500ms latency |
| Cross-platform | Android, iOS, Web, Desktop |
| Open standard | W3C and IETF standards |
| Secure | DTLS/SRTP encryption by default |

### WebRTC architecture
```
┌──────────┐     Signaling      ┌──────────┐
│  Peer A  │◄────Server──────►│  Peer B  │
│ (Mobile) │                   │ (Mobile) │
└────┬─────┘                   └────┬─────┘
     │                              │
     └─────── P2P Media ───────────┘
         (audio/video/data)
```

### WebRTC components
| Component | Purpose |
|-----------|---------|
| PeerConnection | Manages P2P connection |
| MediaStream | Audio/video tracks |
| DataChannel | Arbitrary data transfer |
| Signaling | Session negotiation (not part of WebRTC) |

---

## Q2: What is the role of signaling in WebRTC?

WebRTC does **not** define signaling — you must implement it yourself.

### What signaling does
1. **Session negotiation** — exchange SDP offers/answers
2. **ICE candidate exchange** — share network paths
3. **Session management** — join, leave, busy, reject

### Signaling transport options
| Transport | Use Case |
|-----------|---------|
| WebSocket | Most common, real-time |
| Firebase RTDB | Quick setup, no server |
| Socket.IO | Web + mobile, rooms |
| REST polling | Fallback, not recommended |

### Signaling flow
```
Peer A                    Signaling Server                    Peer B
  │                              │                              │
  ├── createOffer ──────────────►│                              │
  │                              ├── offer ────────────────────►│
  │                              │                              ├── createAnswer
  │                              │◄────────── answer ───────────┤
  │◄──────── answer ─────────────┤                              │
  ├── ICE candidates ───────────►│                              │
  │                              ├── ICE candidates ───────────►│
  │                              │◄──────── ICE candidates ────┤
  │◄─── ICE candidates ─────────┤                              │
  │                              │                              │
  └────────── P2P Connection Established ──────────────────────┘
```

### Key point
Signaling is only needed to **set up** the connection. Once P2P is established, media flows directly between peers — signaling server is not in the media path.

---

## Q3: What are SDP, Offer, and Answer?

SDP (Session Description Protocol) describes media capabilities (codecs, resolutions, protocols).

### SDP exchange
```kotlin
// Peer A creates offer
val offer = peerConnection.createOffer(sdpConstraints)
peerConnection.setLocalDescription(offer)

// Send offer to Peer B via signaling
signalingClient.sendOffer(offer.description)

// Peer B receives offer, creates answer
peerConnection.setRemoteDescription(offer)
val answer = peerConnection.createAnswer(sdpConstraints)
peerConnection.setLocalDescription(answer)

// Send answer back to Peer A
signalingClient.sendAnswer(answer.description)

// Peer A receives answer
peerConnection.setRemoteDescription(answer)
// → Connection is being established
```

### What SDP contains
```
v=0
m=audio 9 UDP/TLS/RTP/SAVPF 111 103 104
a=rtpmap:111 opus/48000/2        ← Opus audio codec
m=video 9 UDP/TLS/RTP/SAVPF 96 97
a=rtpmap:96 VP8/90000             ← VP8 video codec
a=fingerprint:sha-256 ...         ← DTLS fingerprint (security)
a=ssrc:1234 cname:user1           ← Stream source
```

---

## Q4: What are ICE, STUN, and TURN?

### NAT problem
Devices behind NAT routers can't be reached directly. ICE solves this.

### ICE (Interactive Connectivity Establishment)
Tries multiple paths to find the best connection:

| Candidate Type | Description | Success Rate |
|---------------|-------------|-------------|
| Host | Direct local IP | ~20% (same LAN only) |
| Server Reflexive (STUN) | Public IP via STUN | ~80% |
| Relay (TURN) | Traffic relayed through TURN | 100% (but costly) |

### STUN (Session Traversal Utilities for NAT)
```
Peer ──"What's my public IP?"──► STUN Server
Peer ◄──"Your IP is 203.0.113.5:42000"── STUN Server
```

### TURN (Traversal Using Relays around NAT)
```
Peer A ──► TURN Server ◄──Peer B
         (relays all media)
```

### When you need TURN
| Scenario | STUN enough? |
|----------|-------------|
| Same WiFi | ✅ Yes |
| Different networks, simple NAT | ✅ Yes |
| Symmetric NAT | ❌ Need TURN |
| Corporate firewall | ❌ Need TURN |
| Strict firewall (blocks UDP) | ❌ Need TURN (TCP) |

### ICE candidate exchange
```kotlin
// When ICE finds a candidate
peerConnection.iceCandidateObserver = object : PeerConnection.Observer {
    override fun onIceCandidate(candidate: IceCandidate) {
        // Send to remote peer via signaling
        signalingClient.sendIceCandidate(candidate)
    }
}

// When receiving a candidate from remote peer
fun onRemoteIceCandidate(candidate: IceCandidate) {
    peerConnection.addIceCandidate(candidate)
}
```

---

## Q5: What are the WebRTC media flows?

```
┌─────────────────────────────────────────────────┐
│                  Peer A (Sender)                 │
│                                                  │
│  Camera ──► VideoCapturer ──► VideoTrack ──┐    │
│                                            │     │
│  Mic ────► AudioRecord ──► AudioTrack ──┐  │     │
│                                          │  │     │
│                          PeerConnection ◄┘──┘     │
│                               │                  │
└───────────────────────────────┼──────────────────┘
                                │
                         SRTP/DTLS (encrypted)
                                │
┌───────────────────────────────┼──────────────────┐
│                  Peer B (Receiver)               │
│                               │                  │
│                          PeerConnection ◄────────┘
│                               │
│                    VideoRenderer ◄── VideoTrack
│                    AudioTrack ──► Speaker
└──────────────────────────────────────────────────┘
```

### Media flow steps
1. **Capture** — Camera/mic → media stream
2. **Encode** — Raw frames → compressed (H.264/VP8/VP9, Opus)
3. **Transport** — RTP packets over SRTP (encrypted)
4. **Decode** — Compressed → raw frames
5. **Render** — Display on screen / play on speaker

---

## Q6: What codecs does WebRTC use?

| Media | Codec | Notes |
|-------|-------|-------|
| Audio | Opus | Default, high quality, variable bitrate |
| Audio | G.711 | Fallback, legacy |
| Video | VP8 | Default on Android, royalty-free |
| Video | VP9 | Better compression, less device support |
| Video | H.264 | Hardware accelerated on most devices |
| Video | AV1 | Newest, best compression, limited support |

### Codec selection
```kotlin
// Force H.264 (hardware acceleration on Android)
val videoConstraints = MediaConstraints().apply {
    mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
}

// Check supported codecs
val codecs = peerConnection.videoCodecs // List of VideoCodec
// Prefer H.264 for battery efficiency on mobile
```

### Mobile considerations
- **H.264** — hardware encoder/decoder → better battery life
- **VP8** — software encoder → more CPU, more battery drain
- **VP9** — best quality but may not have hardware support

---

## Q7: What is a DataChannel?

DataChannel enables arbitrary data transfer over the same P2P connection — no server needed.

```kotlin
// Create data channel
val dataChannelConfig = DataChannel.Init().apply {
    ordered = true
    maxRetransmits = -1  // reliable mode
}
val dataChannel = peerConnection.createDataChannel("chat", dataChannelConfig)

// Send data
val buffer = DataChannel.Buffer(
    ByteBuffer.wrap("Hello!".toByteArray()),
    false
)
dataChannel.send(buffer)

// Receive data
dataChannel.registerObserver(object : DataChannel.Observer {
    override fun onMessage(buffer: DataChannel.Buffer) {
        val data = buffer.data
        val bytes = ByteArray(data.remaining())
        data.get(bytes)
        val message = String(bytes)
        println("Received: $message")
    }
})
```

### DataChannel use cases
| Use Case | Ordered? | Reliable? |
|----------|----------|-----------|
| Chat messages | ✅ | ✅ |
| File transfer | ✅ | ✅ |
| Game state | ❌ | ❌ (low latency) |
| Screen drawing | ❌ | ❌ |
| File sharing | ✅ | ✅ |

### DataChannel vs WebSocket
| Feature | DataChannel | WebSocket |
|---------|-------------|----------|
| Path | P2P (direct) | Via server |
| Latency | Lower | Higher |
| Server needed | No (after setup) | Yes |
| Encryption | DTLS | TLS |
| Max message size | ~16KB (typical) | Unlimited |

---

## 🔗 Related Topics
- [Android WebRTC](AndroidWebRTC.md)
- [WebRTC Scenarios](WebRTCScenarios.md)
