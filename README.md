# 🚀 SystemUpdaterApp

**SystemUpdaterApp** is a custom Android **system-level updater application** that installs **A/B (seamless) OTA updates** using Android’s internal `update_engine` APIs.

Unlike the official Google sample, this implementation fetches **OTA update configurations from a custom backend server**, making it closer to how real production OTA systems work on custom ROMs and OEM devices.

---

## ⭐ Key Highlights

- A/B (seamless) OTA update support  
- OTA configurations fetched from a backend server  
- Streaming & non-streaming update support  
- Pause / Resume / Stop / Reset update flow  
- A/B slot switching control  
- Update progress, engine status, and error reporting  
- Built using Android **System APIs (`@SystemApi`)**

---

## 🔍 What This Project Demonstrates

- Real-world **OTA update architecture**
- Android **system / privileged app development**
- Use of **hidden Android APIs**
- Client ↔ backend integration for OTA delivery
- Interaction with Android `update_engine`
- Managing update lifecycle and edge cases

---

## 📦 Core Features

- 🌐 Fetch OTA update configurations from a backend server
- 📋 Display available OTA updates in the app UI
- ▶ Apply OTA packages using `update_engine`
- ⏸️ Pause and resume ongoing updates
- 🛑 Stop or reset update attempts
- 🔄 Switch active A/B slot after update completion
- 📊 Show current build, update progress, engine status, and errors

---

## 🧪 Tested Environment

- ✔ Custom Android OS (AOSP-based)
- ✔ Physical Android device provided by client
- ✔ Devices supporting **A/B partitioning**
- ✔ Rooted / system-level environment

> ⚠️ This app is intended for **custom ROMs, OEM builds, or rooted devices**.  
> It is **not designed for Play Store distribution**.

---

## 💻 Tech Stack

- Android (Kotlin)
- A/B Seamless OTA Updates
- Android `update_engine`
- System / Privileged App APIs
- Backend-driven OTA configuration system

---

## 🏗️ Project Scope

This project focuses on **system-level update workflows** rather than UI polish.  
It is intended for:
- Android platform engineers
- OEM / firmware teams
- Developers working with AOSP or custom ROMs

---

## 📜 License

Apache License 2.0  
https://www.apache.org/licenses/LICENSE-2.0
