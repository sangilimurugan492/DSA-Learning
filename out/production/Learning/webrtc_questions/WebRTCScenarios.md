# WebRTC Scenarios

## Scenario 1: 1-on-1 Video Call

### Problem
Implement a 1-on-1 video call between two Android devices.

### Solution

```
Peer A (Caller)              Signaling              Peer B (Callee)
    │                           │                        │
    ├── createOffer ───────────►│                        │
    │                           ├── offer ──────────────►│
    │                           │                        ├── setRemoteDescription
    │                           │                        ├── createAnswer
    │                           │◄───────── answer ──────┤
    │◄──────── answer ─────────┤                        │
    ├── setRemoteDescription   │                        │
    ├── ICE candidates ───────►│                        │
    │                           ├── ICE candidates ────►│
    │                           │◄────── ICE candidates ─┤
    │◄─── ICE candidates ───────┤                        │
    │                           │                        │
    └──────── P2P Video Call Established ───────────────┘
```

### Caller (Peer A)
```kotlin
fun startCall(remoteUserId: String) {
    // 1. Create PeerConnection
    val peerConnection = createPeerConnection()
    
    // 2. Add local tracks
    peerConnection.addTrack(localAudioTrack)
    peerConnection.addTrack(localVideoTrack)
    
    // 3. Create offer
    peerConnection.createOffer(object : SdpObserver {
        override fun onCreateSuccess(sdp: SessionDescription) {
            peerConnection.setLocalDescription(sdpObserver, sdp)
            // 4. Send offer via signaling
            signalingClient.sendOffer(remoteUserId, sdp.description)
        }
        override fun onCreateFailure(error: String?) { /* handle */ }
        override fun onSetSuccess() {}
        override fun onSetFailure(error: String?) {}
    }, MediaConstraints())
}
```

### Callee (Peer B)
```kotlin
fun onOfferReceived(callerId: String, offerSdp: String) {
    // 1. Create PeerConnection
    val peerConnection = createPeerConnection()
    
    // 2. Add local tracks
    peerConnection.addTrack(localAudioTrack)
    peerConnection.addTrack(localVideoTrack)
    
    // 3. Set remote description (offer)
    val offer = SessionDescription(SessionDescription.Type.OFFER, offerSdp)
    peerConnection.setRemoteDescription(object : SdpObserver {
        override fun onSetSuccess() {
            // 4. Create answer
            peerConnection.createAnswer(object : SdpObserver {
                override fun onCreateSuccess(answerSdp: SessionDescription) {
                    peerConnection.setLocalDescription(sdpObserver, answerSdp)
                    // 5. Send answer via signaling
                    signalingClient.sendAnswer(callerId, answerSdp.description)
                }
                override fun onCreateFailure(error: String?) { /* handle */ }
                override fun onSetSuccess() {}
                override fun onSetFailure(error: String?) {}
            }, MediaConstraints())
        }
        override fun onSetFailure(error: String?) { /* handle */ }
        override fun onCreateSuccess(sdp: SessionDescription?) {}
        override fun onCreateFailure(error: String?) {}
    }, offer)
}
```

### Key points
- Caller creates offer, callee creates answer
- Both exchange ICE candidates via signaling
- Once connected, media flows P2P (no server)
- Use `UNIFIED_PLAN` SDP semantics

---

## Scenario 2: Group Video Call (SFU)

### Problem
3+ participants need to join a video call. P2P mesh doesn't scale.

### P2P Mesh vs SFU vs MCU

| Architecture | Connections | Bandwidth | Complexity |
|-------------|-------------|-----------|------------|
| P2P Mesh | N*(N-1)/2 | High (N uploads) | Low |
| SFU | N (to server) | Medium (1 upload, N downloads) | Medium |
| MCU | N (to server) | Low (1 upload, 1 download) | High |

### SFU (Selective Forwarding Unit) — recommended for mobile
```
         ┌─────────────┐
Peer A ──►│             │──► Peer A
Peer B ──►│   SFU       │──► Peer B
Peer C ──►│   Server    │──► Peer C
         └─────────────┘

Each peer uploads 1 stream, downloads N-1 streams
```

### Implementation with SFU
```kotlin
class GroupCallManager {
    // One PeerConnection per remote participant
    private val peerConnections = mutableMapOf<String, PeerConnection>()
    
    fun joinRoom(roomId: String) {
        // 1. Connect to SFU via WebSocket
        signalingClient.joinRoom(roomId)
        
        // 2. SFU sends list of existing participants
        // 3. For each participant, create PeerConnection
    }
    
    fun onNewParticipant(userId: String) {
        val pc = createPeerConnection()
        pc.addTrack(localAudioTrack)
        pc.addTrack(localVideoTrack)
        peerConnections[userId] = pc
        
        // Create offer to SFU for this participant
        pc.createOffer(/* ... */)
    }
    
    fun onParticipantLeft(userId: String) {
        peerConnections[userId]?.close()
        peerConnections.remove(userId)
    }
    
    // Limit video quality based on participant count
    fun adjustVideoQuality(participantCount: Int) {
        val (width, height, fps) = when {
            participantCount <= 2 -> Triple(1280, 720, 30)  // 720p
            participantCount <= 4 -> Triple(640, 480, 24)  // 480p
            else -> Triple(320, 240, 15)                   // 240p
        }
        videoCapturer?.changeCaptureFormat(width, height, fps)
    }
}
```

### SFU servers
| Server | Language | Notes |
|--------|----------|-------|
| mediasoup | Node.js | High performance, popular |
| Janus | C | Feature-rich, modular |
| Jitsi | Java/Lua | Open-source, scalable |
| LiveKit | Go | Easy to use, mobile-friendly |

---

## Scenario 3: Screen Share

### Problem
Share the device screen during a video call.

### Solution
```kotlin
class ScreenShareManager(private val context: Context) {
    
    private var screenCapturer: ScreenCapturerAndroid? = null
    private var screenVideoTrack: VideoTrack? = null
    private var mediaProjectionPermissionResultData: Intent? = null
    private val SCREEN_CAPTURE_REQUEST = 1001
    
    // 1. Request screen capture permission
    fun requestScreenCapture(activity: Activity) {
        val mediaProjectionManager = context.getSystemService(
            Context.MEDIA_PROJECTION_SERVICE
        ) as MediaProjectionManager
        activity.startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(),
            SCREEN_CAPTURE_REQUEST
        )
    }
    
    // 2. Handle permission result
    fun onScreenCaptureResult(resultCode: Int, data: Intent) {
        mediaProjectionPermissionResultData = data
        startScreenShare()
    }
    
    // 3. Start screen capture
    private fun startScreenShare() {
        screenCapturer = ScreenCapturerAndroid(
            mediaProjectionPermissionResultData!!, object : MediaProjection.Callback() {
                override fun onStop() {
                    // Screen capture stopped
                    stopScreenShare()
                }
            }
        )
        
        val surfaceTextureHelper = SurfaceTextureHelper.create(
            "ScreenCapture", rootEglBase.eglBaseContext
        )
        val videoSource = factory.createVideoSource(true)  // isScreencast = true
        screenCapturer?.initialize(
            surfaceTextureHelper, context, videoSource.capturerObserver
        )
        screenCapturer?.startCapture(1280, 720, 30)
        
        screenVideoTrack = factory.createVideoTrack("SCREEN_SHARE", videoSource)
        
        // 4. Replace camera track with screen track
        val sender = peerConnection.senders.find { 
            it.track()?.id() == localVideoTrack?.id() 
        }
        sender?.setTrack(screenVideoTrack, true)
    }
    
    // 5. Stop screen share — switch back to camera
    fun stopScreenShare() {
        screenCapturer?.stopCapture()
        screenCapturer?.dispose()
        screenVideoTrack?.dispose()
        
        // Switch back to camera
        val sender = peerConnection.senders.find { 
            it.track()?.id() == screenVideoTrack?.id() 
        }
        sender?.setTrack(localVideoTrack, true)
    }
}
```

### Foreground service requirement (Android 14+)
```kotlin
// Must run foreground service while screen sharing
class ScreenShareService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }
}
```

---

## Scenario 4: Bandwidth Optimization

### Problem
Video call uses too much bandwidth on mobile data. Need to adapt to network conditions.

### Solution

```kotlin
// 1. Set initial bitrate limits
fun setMaxBitrate(peerConnection: PeerConnection, maxBitrateKbps: Int) {
    val sender = peerConnection.senders.find { it.track()?.kind() == "video" }
    sender?.let {
        val params = it.parameters
        if (params.encodings.isEmpty()) {
            params.encodings.add(RtpParameters.Encoding())
        }
        params.encodings[0].maxBitrateBps = maxBitrateKbps * 1000
        it.parameters = params
    }
}

// 2. Adaptive bitrate based on network
fun adaptToNetwork(networkType: Int) {
    val maxBitrate = when (networkType) {
        ConnectivityManager.TYPE_WIFI -> 2_500_000   // 2.5 Mbps
        ConnectivityManager.TYPE_4G -> 1_000_000     // 1 Mbps
        ConnectivityManager.TYPE_3G -> 500_000       // 500 Kbps
        else -> 300_000                                // 300 Kbps
    }
    setMaxBitrate(peerConnection, maxBitrate / 1000)
}

// 3. Simulcast (send multiple qualities)
fun enableSimulcast(peerConnection: PeerConnection) {
    val sender = peerConnection.senders.find { it.track()?.kind() == "video" }
    sender?.let {
        val params = it.parameters
        params.encodings.clear()
        params.encodings.add(RtpParameters.Encoding().apply {
            rid = "low"
            maxBitrateBps = 150_000    // 150 Kbps
            scaleResolutionDownBy = 4.0
        })
        params.encodings.add(RtpParameters.Encoding().apply {
            rid = "mid"
            maxBitrateBps = 500_000    // 500 Kbps
            scaleResolutionDownBy = 2.0
        })
        params.encodings.add(RtpParameters.Encoding().apply {
            rid = "high"
            maxBitrateBps = 2_500_000  // 2.5 Mbps
        })
        it.parameters = params
    }
}

// 4. Lower resolution on poor network
fun degradeVideo() {
    videoCapturer?.changeCaptureFormat(320, 240, 15)  // 240p @ 15fps
}

// 5. Audio-only fallback
fun switchToAudioOnly() {
    localVideoTrack?.setEnabled(false)
    // Keep audio track enabled
}
```

### Bandwidth targets
| Network | Video Bitrate | Resolution | FPS |
|---------|-------------|------------|-----|
| WiFi | 2.5 Mbps | 720p | 30 |
| 4G | 1 Mbps | 480p | 24 |
| 3G | 500 Kbps | 360p | 15 |
| 2G/Poor | Audio only | — | — |

---

## Scenario 5: Call Recording

### Problem
Record the video call locally on the device.

### Solution
```kotlin
class CallRecorder(
    private val eglBaseContext: EglBase.Context,
    private val outputFile: String
) {
    private var videoFileRenderer: VideoFileRenderer? = null
    
    fun startRecording(videoTrack: VideoTrack) {
        videoFileRenderer = VideoFileRenderer(
            outputFile,
            1280, 720, 30,
            eglBaseContext
        )
        videoTrack.addSink(videoFileRenderer!!)
    }
    
    fun stopRecording(videoTrack: VideoTrack) {
        videoTrack.removeSink(videoFileRenderer!!)
        videoFileRenderer?.release()
        videoFileRenderer = null
    }
}

// For audio recording, use MediaRecorder alongside
class AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    
    fun startRecording(outputFile: String) {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile)
            prepare()
            start()
        }
    }
    
    fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
    }
}
```

### Recording considerations
| Approach | Pros | Cons |
|----------|------|------|
| VideoFileRenderer | Native WebRTC, syncs with stream | Video only, no audio |
| MediaRecorder | Audio + video | Separate from WebRTC pipeline |
| MediaProjection | Records entire screen | Requires permission, battery drain |
| Server-side recording | Best quality, all participants | Requires SFU support, server cost |

---

## Scenario 6: Bluetooth Headset Handling

### Problem
Audio should route to Bluetooth headset when connected, and switch back to earpiece when disconnected.

### Solution
```kotlin
class AudioDeviceManager(context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    
    // Start with earpiece (like a phone call)
    fun enableSpeakerphone(enabled: Boolean) {
        audioManager.isSpeakerphoneOn = enabled
    }
    
    // Route to Bluetooth
    fun routeToBluetooth() {
        audioManager.startBluetoothSco()
        audioManager.isBluetoothScoOn = true
    }
    
    // Route to earpiece
    fun routeToEarpiece() {
        audioManager.isBluetoothScoOn = false
        audioManager.stopBluetoothSco()
        audioManager.isSpeakerphoneOn = false
    }
    
    // Listen for Bluetooth headset changes
    val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(
                BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_DISCONNECTED
            )
            when (state) {
                BluetoothHeadset.STATE_CONNECTED -> routeToBluetooth()
                BluetoothHeadset.STATE_DISCONNECTED -> routeToEarpiece()
            }
        }
    }
    
    // Handle audio focus changes (calls, alarms, etc.)
    val audioFocusListener = AudioManager.OnAudioFocusChangeListener { focus ->
        when (focus) {
            AudioManager.AUDIOFOCUS_LOSS -> { /* pause call audio */ }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> { /* duck volume */ }
            AudioManager.AUDIOFOCUS_GAIN -> { /* resume normal */ }
        }
    }
}
```

### Audio routing priority
1. Bluetooth headset (if connected)
2. Wired headset (if plugged in)
3. Speakerphone (if enabled by user)
4. Earpiece (default for calls)

---

## 🔗 Related Topics
- [WebRTC Basics](WebRTCBasics.md)
- [Android WebRTC](AndroidWebRTC.md)
