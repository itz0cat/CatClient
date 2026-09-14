# 🐾 CatClient

> Next-generation Fabric 1.21.11 utility client with Blue Flame aesthetics, ImGui UI, and integrated Itz0Cat Cloud Backend.

![CatClient Icon](src/main/resources/assets/catclient/icon.png)

---

## ✨ Features & Modules

CatClient merges the high-performance ImGui HUD rendering engine and multi-mod suite of BlazeClient with the advanced modules and event system of CodeX:

### 🛡️ Core Engine & UI
- **ImGui Hardware-Accelerated UI**: Sleek, customizable menus and draggable HUD overlays (`Right Shift` to open).
- **Blue Flame Aesthetic**: Electric cyan/neon blue color palette (`#00D2FF`) with deep slate backgrounds (`#0B0E14`).
- **Profiles & Config Manager**: In-game mod profile switcher with instant disk persistence.
- **Discord Rich Presence**: Integrated presence reporting server address and custom client status.

### 🎮 Gameplay & HUD Modules
- **Glued CodeX Modules**:
  - `AimAssistMod`: Smooth, legitimate humanized combat tracking with customizable FOV, distance, aim lock, and Blue Flame ESP.
  - `BlockOverlayMod`: Smooth animated block selection box with custom outline/fill, rainbow mode, and depth pass-through.
  - `FullbrightMod`: Maximum gamma, night vision, and lightmap adjustments.
- **Flagship HUD Suite**:
  - `FPSMod`, `CPSMod`, `PingMod`, `KeystrokesMod`
  - `ArmorMod`, `PotionMod`, `ScoreboardMod`, `CoordsMod`
  - `ServerIPMod`, `TimeMod`, `ReachDisplayMod`
  - `ArrowCountMod`, `PotCountMod`, `TotemCountMod`
- **Utility & Visuals**:
  - `ToggleSprintMod`, `ToggleSneakMod`, `ZoomMod`
  - `FreelookMod`, `HurtCamMod`, `HitColorMod`, `HitboxMod`, `NametagsMod`, `TimeChangerMod`

### ☁️ Itz0Cat Cloud Backend
- Integrated with `https://itz0cat.onrender.com` (with fallback to `https://catgame-backend-btit.onrender.com`):
  - **Online Verification**: Real-time identification of other CatClient users in lobbies and servers with in-game Cat badges.
  - **Cosmetics Engine**: Dynamic cloud capes (Cat Blue Flame, Astelic, Purple Sky, Axolotl, Glow Squid).
  - **Live MOTD & Announcements**: Remote synchronized announcements.

---

## 🛠️ Tech Stack & Ecosystem

- **Loader**: Fabric Loader `>=0.16.0`
- **Target Version**: Minecraft `1.21.11`
- **Java**: Java 21 LTS
- **Libraries**:
  - Dear ImGui Java LWJGL3 Bindings
  - Fabric API
  - Shadow Plugin
  - Java Discord RPC

---

## 📦 Building

To build the client jar (when in desktop/unmetered environment):

```bash
./gradlew build
```

The resulting jar will be in `build/libs/CatClient-1.21.11-1.0.0.jar`.

---

## 📜 License

Distributed under the MIT License.
