<p align="center">
  <img src="assets/cat-client-logo-paw.svg" width="128" height="128" alt="CatClient Logo"/>
</p>

<h1 align="center">CatClient</h1>

<p align="center">
  <strong>A modern, high-performance PvP and utility client mod for Minecraft on Fabric Loader.</strong>
</p>

<p align="center">
  <a href="https://github.com/itz0cat/CatClient/actions/workflows/build.yml"><img src="https://github.com/itz0cat/CatClient/actions/workflows/build.yml/badge.svg" alt="Build Status"/></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-LGPL_v3-blue.svg" alt="License: LGPL-3.0"/></a>
  <a href="https://fabricmc.net"><img src="https://img.shields.io/badge/Minecraft-1.21.11-brightgreen.svg" alt="Minecraft 1.21.11"/></a>
  <a href="https://fabricmc.net"><img src="https://img.shields.io/badge/Loader-Fabric-orange.svg" alt="Fabric Loader"/></a>
  <a href="https://github.com/itz0cat/CatClient/releases"><img src="https://img.shields.io/github/v/release/itz0cat/CatClient" alt="Latest Release"/></a>
  <a href="https://dsc.gg/itz0cat"><img src="https://img.shields.io/badge/Discord-Join%20Us-5865F2?logo=discord&logoColor=white" alt="Discord"/></a>
</p>

---

## 🌟 Overview

**CatClient** is a modern Minecraft utility and enhancement client mod tailored for Minecraft **1.21.11** using **Fabric Loader**. It delivers an extensive array of customizable HUD modules, visual tweaks, PvP quality-of-life improvements, and streamlined client integrations while maintaining high FPS and lightweight resource usage.

CatClient is an open-source fork of [AxolotlClient](https://codeberg.org/moehreag/AxolotlClient), maintained by [itz0cat](https://github.com/itz0cat).

---

## ✨ Features

- **Full HUD Customization**:
  - FPS, Ping, CPS, Armor, Keystrokes, Coordinates, Biome, Direction, Speed, and Server IP displays.
  - Actionbar, Bossbar, Arrow Counter, Hotbar customization, Scoreboard positioning, and Crosshair customizer.
- **PvP & Utility**:
  - Freelook (server-compliant).
  - Dynamic FOV toggle & Custom Zoom.
  - Low Fire and Low Shield adjustments.
  - Hit Color customization and Custom Block Outlines.
  - Motion Blur shader pipeline and Time Changer.
  - Fullbright toggle.
- **Hypixel Integration**:
  - AutoGG / AutoGLHF / AutoGF.
  - LevelHead and Nick Hider.
  - AutoTip and AutoBoop.
- **Privacy & Connectivity**:
  - **Zero Telemetry by Default**: Configurable `apiEnabled` switch (off by default) prevents any external network calls unless explicitly enabled.
  - Custom API backend endpoint configuration.
  - Full support for both online and offline accounts with null-safe session management.
- **Compatibility**:
  - Out-of-the-box support for Mod Menu and World Host.

---

## 📥 Installation

1. Ensure you have [Fabric Loader](https://fabricmc.net/use/) (0.19+ recommended) installed for **Minecraft 1.21.11**.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 1.21.11.
3. Download the latest `CatClient-1.0.0+1.21.11.jar` from [Releases](https://github.com/itz0cat/CatClient/releases).
4. Place both `.jar` files into your `.minecraft/mods` directory.
5. Launch Minecraft using your Fabric profile and press `Right Shift` (default) to configure CatClient in-game.

---

## 🛠️ Building from Source

CatClient builds using standard Gradle and JDK 21:

```bash
git clone https://github.com/itz0cat/CatClient.git
cd CatClient
./gradlew build collectBuilds
```

The compiled mod JAR will be generated in `versions/1.21.11/build/libs/` and `builds/`.

---

## 📜 Credits & Attributions

CatClient is based on and forked from **AxolotlClient** by **moehreag** and contributors:

- **CatClient Maintainer**: [itz0cat](https://github.com/itz0cat)
- **Original AxolotlClient Author**: [moehreag](https://codeberg.org/moehreag)
- **AxolotlClient Contributors**: Dark_Ender, fiphi, kikugie, Moulberry, boomer, YakisikliBaran, DragonEggBedrockBreaking, TheKodeToad, CornetPanique86, and many others.
- **Upstream Repositories**:
  - [AxolotlClient](https://codeberg.org/moehreag/AxolotlClient)
  - [AxolotlClient-config](https://codeberg.org/moehreag/AxolotlClient-config)
  - [AxolotlClient-rendering](https://codeberg.org/moehreag/AxolotlClient-rendering)

For a detailed list of third-party libraries and licenses, see [CREDITS](CREDITS).

---

## 📄 License

CatClient is licensed under the [GNU Lesser General Public License v3.0 (LGPL-3.0)](LICENSE).
All modifications made to the upstream source code are marked and retained in compliance with the LGPL-3.0.
