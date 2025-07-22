# 🎁 [Hypixel] DailyRewards

<div align="center">

[![GitHub Release](https://img.shields.io/github/v/release/Cqseur/DailyRewards?style=for-the-badge&logo=github&color=181717)](https://github.com/Cqseur/DailyRewards/releases)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.5-green?style=for-the-badge&logo=minecraft)](https://minecraft.net)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/hypixel-dailyrewards?style=for-the-badge&logo=modrinth&color=00AF5C)](https://modrinth.com/mod/hypixel-dailyrewards)
[![GitHub Issues](https://img.shields.io/github/issues/Cqseur/DailyRewards?style=for-the-badge&color=red)](https://github.com/Cqseur/DailyRewards/issues)
[![License](https://img.shields.io/badge/License-LGPL--3.0-blue?style=for-the-badge)](https://github.com/Cqseur/DailyRewards/blob/main/LICENSE)

**🎁 Automate Hypixel Skyblock daily rewards with beautiful card animations and instant claim notifications**

[📥 Download on Modrinth](#-installation) • [⚡ Features](#-features) • [💬 Discord](https://discord.gg/tkUqGeDN5J)

</div>

---

## ✨ Features

### 🎯 **Automated Reward System**
- **🔗 Smart Link Detection** - Automatically detects Hypixel reward links in chat
- **⚡ One-Click Claiming** - Claims rewards instantly with a single click
- **🎮 In-Game Integration** - Seamless Minecraft experience

### 🎨 **Beautiful Interactive UI**
- **🃏 Card-Based Reward Selection** - Choose from beautifully animated reward cards
- **🌈 Rarity-Based Colors** - Visual distinction for different reward rarities:
  - 🟫 **Common** - Gray
  - 🔵 **Rare** - Aqua  
  - 🟣 **Epic** - Light Purple
  - 🟡 **Legendary** - Gold
- **🔄 Smooth Flip Animations** - Satisfying card reveal animations
- **📱 Modern Interface** - Clean, responsive design

### ⚙️ **Customizable Configuration**
- **🎭 Animation Settings** - Toggle flip animations and adjust speed
- **💾 Persistent Settings** - Configuration saves automatically

### 🔧 **Technical Features**
- **🌐 HTTP Integration** - Secure communication with Hypixel's reward system
- **🎵 Sound Effects** - Audio feedback for reward claiming
- **📊 Error Handling** - Robust error management and user feedback
- **🔄 Auto-Retry** - Automatic retry on network failures (coming-soon)

---

## 🚀 Installation

### 📱 **Easy Installation (Recommended)**
1. **Install via Modrinth App** or your favorite launcher
2. **Search for "[Hypixel] DailyRewards"** or use this link: [Download on Modrinth](https://modrinth.com/mod/hypixel-dailyrewards#download)
3. **Click Install** - all dependencies will be handled automatically!

### 🛠️ **Manual Installation**

#### Prerequisites
- **Minecraft 1.21.5**
- **Fabric Loader 0.16.14+**
- **Java 21+**

#### Required Dependencies
- [📚 **Fabric API**](https://modrinth.com/mod/fabric-api) `0.127.1+1.21.5`
- [🌐 **Fabric Language Kotlin**](https://modrinth.com/mod/fabric-language-kotlin) `1.13.3+kotlin.2.1.21`

#### Optional Dependencies
- [⚙️ **Cloth Config**](https://modrinth.com/mod/cloth-config) `18.0.145` - For in-game configuration (Required if you want the config GUI)
- [📋 **ModMenu**](https://modrinth.com/mod/modmenu) `9.0.0`

#### Steps
1. **Download** the latest version from [Release](https://github.com/cqseur/DailyRewards/releases)
2. **Install all required dependencies** listed above
3. **Place** all `.jar` files in your `mods` folder
4. **Launch** Minecraft with Fabric
5. **Join Hypixel** and wait for reward links to appear in chat
6. **Choose a reward** to claim it

---

## 🎮 Commands

| Command | Description | Example |
|---------|-------------|---------|
| `/dailyrewards` | Open the daily rewards configuration interface | `/dailyrewards` |


---

## ⚙️ Configuration

Access the configuration menu via `/dailyrewards` or through Mod Menu.

### 🎛️ Available Settings

<details>
<summary><b>🎭 Animation Settings</b></summary>

- **Flip Animation** - Enable/disable card flip animations
- **Flip Speed** - Adjust animation speed (0.1x - 2.0x)

</details>

---

## 🎨 Screenshots

<div align="center">

### 🃏 Interactive Reward Cards
![Reward Cards](https://cdn.modrinth.com/data/AoDOmdGD/images/0c4011a3d0b79ffc245e16528893aa6eca527c84.png)

### 🎨 Rarity-Based UI
![Rarity Colors](https://cdn.modrinth.com/data/cached_images/903480f90ffce1316a54828f189a533db8cde029_0.webp)

</div>

---

## 🖼️ **Gallery**

<div align="center">

| Feature | Preview |
|---------|----------|
| 🃏 **Card Selection** | Beautiful animated cards with rarity colors |
| ⚡ **Quick Claim** | One-click reward claiming from chat |
| 🌈 **Visual Feedback** | Clear success/error indicators |
| 🔊 **Sound Effects** | Satisfying audio feedback |

*View more screenshots on the [Modrinth Gallery](https://modrinth.com/mod/hypixel-dailyrewards/gallery)*

</div>

---

## 🔧 API Integration

### 🌐 Hypixel Rewards API
The mod integrates seamlessly with Hypixel's official rewards system:

- **Secure HTTPS** communication
- **Token-based** authentication
- **Real-time** reward tracking
- **Error recovery** mechanisms

### 📡 Network Features
- **Automatic link parsing** from chat messages
- **Background reward fetching** 
- **Cached reward data** for offline viewing
- **Rate limiting** to respect API limits

---

## 🛠️ Building from Source

### Prerequisites
- **Java 17+**
- **Git**

### Build Steps
```bash
# Clone the repository
git clone https://github.com/Cqseur/DailyRewards.git
cd DailyRewards

# Build the mod
./gradlew clean build

# The built mod will be in build/libs/
```

### Development Setup
```bash
# Run in development environment
./gradlew runClient
```

---

## 🎯 How It Works

### 🔄 Reward Flow
1. **Detection** - Mod monitors chat for Hypixel reward links
2. **Parsing** - Extracts reward tokens from URLs
3. **Fetching** - Retrieves reward data from Hypixel API
4. **Display** - Shows beautiful card-based selection interface
5. **Claiming** - Submits selection and receives rewards

### 🎮 User Experience
- **Zero Configuration** - Works out of the box
- **Non-Intrusive** - Only appears when rewards are available
- **Fast Claims** - Minimal clicks to claim rewards
- **Visual Feedback** - Clear success/error indicators

---

## 📊 Compatibility & Integration

### ✅ **Tested Environments**
| Environment | Status | Notes |
|-------------|--------|-------|
| 🌎 **Hypixel Network** | ✅ **Full Support** | Primary target - all features work |
| 💻 **Other Servers** | ❌ **Not Applicable** | Hypixel-specific features |

### 🔧 **Mod Compatibility**

#### ✅ **Fully Compatible**
- [⚙️ **Cloth Config**](https://modrinth.com/mod/cloth-config) - Configuration menus
- [📋 **ModMenu**](https://modrinth.com/mod/modmenu) - In-game mod management

#### ⚠️ **May Have Issues**
- **Optifine** - Potential rendering conflicts with overlays
- **Sodium + Iris** - Generally works, test thoroughly
- **Canvas/Other Renderers** - Not extensively tested

### 🎮 **Launcher Support**
- ✅ **Modrinth App** (Recommended - Automatic)
- ✅ **CurseForge/Overwolf** (Manual install)
- ✅ **MultiMC/PolyMC/Prism** (Manual install)
- ✅ **ATLauncher** (Manual install)
- ✅ **Vanilla Launcher** (Manual install)

---

## 🔗 Links & Community

### 📱 **Official Platforms**
- **🟢 Modrinth** - [Download & Updates](https://modrinth.com/mod/hypixel-dailyrewards)
- **📊 Modrinth Versions** - [Version History](https://modrinth.com/mod/hypixel-dailyrewards/versions)
- **📝 Changelog** - [Release Notes](https://modrinth.com/mod/hypixel-dailyrewards/changelog)
- **🖼️ Gallery** - [Screenshots](https://modrinth.com/mod/hypixel-dailyrewards/gallery)

### 🚀 **Development & Support**
- **🤖 GitHub Repository** - [Source Code](https://github.com/Cqseur/DailyRewards)
- **🐛 Bug Reports** - [GitHub Issues](https://github.com/Cqseur/DailyRewards/issues)
- **💬 Discord Support** - [Join Community](https://discord.gg/tkUqGeDN5J)
- **📊 Project Stats** - [Modrinth Analytics](https://modrinth.com/mod/hypixel-dailyrewards)
  
---

## 🤝 Contributing

We welcome contributions! Please feel free to submit a Pull Request.

### 📋 Contributing Guidelines
1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### 🐛 Bug Reports
Please use the [GitHub Issues](https://github.com/Cqseur/DailyRewards/issues) tab to report bugs.

Include:
- **Minecraft version**
- **Mod version**
- **Steps to reproduce**
- **Error logs** (if any)

---

## 📜 License

This project is licensed under the **LGPL-3.0-only License** - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Credits & Acknowledgments

### 👥 **Development Team**
- **[Cqseur](https://github.com/Cqseur)** - Lead Developer
- **[Claude 4.0 🤖](data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAdHx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys/RD84QzQ5OjcBCgoKDQwNGg8PGjclHyU3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3N//AABEIAJQAmwMBIgACEQEDEQH/xAAcAAEAAQUBAQAAAAAAAAAAAAAAAQIDBAYHBQj/xAA9EAABAgMFBgQDBgUEAwAAAAABAgMAERIEITFBUQUGEyIyUiMzQmFDcfBigZGhscEUJDRT0QcVcuGDkqL/xAAaAQEAAgMBAAAAAAAAAAAAAAAAAwQCBQYB/8QAKBEAAgEDAwMDBQEAAAAAAAAAAAECAwQREiExBUFxIoHhEyQyYcEU/9oADAMBAAIRAxEAPwDsoJSqtAm6rqRpASSCGuZKus6QAUTJsyeHWcjASIPCklA8yecAJJp4c/A74GSgEuGlsdKtYTTw6wP5ftz+pwMkgF29s9A0gASVKCl3OJ6E6wvCy4B4xxRoIEEEBwzdPQdIXlRSCBaM1e0AByElu9SusdsJJAoSZtHqXpAXz4UkqHme8OUprR5I6k6wAICk0LMmh0q1geYjiikpPIO6BkE1OCbJ6U5iBuID0ionw/aAEzXWRJ7JGogCUkqb5nFdadIc1VJ8/uGAEACVENGTo6yc4AiQCCls1Nq61aRJAIoPkjBfvASKSW7mxctJziJppqMjZ8k5gwBNygA4aUJ6D3QJJNa+V4dKNYGSZF29B8saQkoKpXe8elQwEATeFFaBN49SdIgAJB4XMFdf2fq+AmSUoID/AKlQEiDwZADzPf6vgAAAnhpM2TirSKgt1IpaRUgYHWKRTTUn+nzTEpS8RNpQCPSDAESChQTSE/E7oXrvIop9PdC4pAcuZHSc4k3kF2QWOj3gCJzAdlf/AGodPNTWVYo7Ym+qoy/iO3KIBIJLQm4esHKAEgkUTqCjeufTAD4RMgL+LrAUgEN3tnrOkYlq2lYbKkotD6EtC8AGao8bS5MoxlJ4ismX1SB5QnA98Jz8SVMvh6xrzm+ewgvhu2xPKeQCQI+d8Z9i2/s23yWzbGy9gkEyB/aMVOL4ZLO2rQWZRa9j0py8QJqJ+HpCVFwNdQx7IAmdSL3T1DKUBIA8KRB8yeUZkAlLw5zBvLmkJBXIeQJ9fdEcoTSPIzOc4tv2hhlE7U6ltkdBJxgepNvCLpmvnIoKT0d0Jy8SV5+FpHh2ne3YzKpv2ocRPTSImyb2bGtSqm7a2HzdJQujHXF9yZ21ZLLg8eD250XgVlWKeyAFPhzrB+JpFDLqHEcWyLS5VesgzEVikIpbmWT1E4xkQCVUm50gYOawPPjyU4DvgQmmldzAwOcDfLi3EeXLP6ugBj4hTIgeXrAthfMXaCfTPD84cxVUoeP6RlKIIYJm8ohzMQBJISAtYqbPSjSBBTIOc6ldB7YATVVUQOPkn2iQSklTV7h6xpESINCjN04OaQAKiUo5Vp61d0AAAAQ3e2es5iFxTI+RkrOHUCpHKhJ5k6wmKQ58HJuABkQA7cgeWRnAzKwVgcYdIGEDJIBcFSVdI7YSKVULvdPSvSAAqCipAm+epOQgOWfCvB8yeX1fAAlVCDS6MV6wAnPh8oT1jugBcElKfIzVnFQLwEmQCj0mKZikrAk0MUaxIbdUKm3KEnBM8IAgipPDJuTnmYK5yCbqcJZwhACfMHc9MokchKxeVYg4REIAmmgFAJkrEmIlNIa9IvnnEQgCTzgA3UYSiSalBzNOWUIQAnSouDE5ZRA5AQL68Z5REIAkJkjhXyN884kisBBwRgREQgCVGtQWcU4SiJyVxczdLKEIAkchKxeVYgwApSWgSQczjEQgBKpIavkM84HxCJ3UYSiIQBUb1cXMZZRSpoLNRJmdIQgD/2Q==)** - Author of this beautiful description

### 🎯 **Special Thanks**
- **Hypixel Team** - For the amazing reward system and API
- **Fabric Community** - For the excellent modding framework
- **Kotlin Community** - For the beautiful programming language
- **Modrinth Team** - For the best mod distribution platform

### 🛠️ **Built With**
- [Fabric](https://fabricmc.net/) - Minecraft Modding Framework
- [Kotlin](https://kotlinlang.org/) - Primary Programming Language
- [OkHttp](https://square.github.io/okhttp/) - HTTP Client for reward API
- [Jsoup](https://jsoup.org/) - HTML parsing for link detection
- [Cloth Config](https://modrinth.com/mod/cloth-config) - Configuration system
- [Minecraft Yarn](https://github.com/FabricMC/yarn) - Minecraft Mappings

---

<div align="center">

### 🌟 **Support the Project!**

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/hypixel-dailyrewards?logo=modrinth&style=for-the-badge&color=00AF5C)](https://modrinth.com/mod/hypixel-dailyrewards)
[![GitHub Stars](https://img.shields.io/github/stars/Cqseur/DailyRewards?style=for-the-badge&color=yellow)](https://github.com/Cqseur/DailyRewards/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/Cqseur/DailyRewards?style=for-the-badge&color=blue)](https://github.com/Cqseur/DailyRewards/network/members)
[![GitHub Issues](https://img.shields.io/github/issues/Cqseur/DailyRewards?style=for-the-badge&color=red)](https://github.com/Cqseur/DailyRewards/issues)

**Made with ❤️ for the Hypixel community**

[🟢 Download on Modrinth](https://modrinth.com/mod/hypixel-dailyrewards#download) • [🌟 Star on GitHub](https://github.com/Cqseur/DailyRewards) • [💬 Join Discord](https://discord.gg/tkUqGeDN5J)

---

**📊 Project Statistics:**
- **Version:** `1.21.5+v1.0.1`
- **License:** `LGPL-3.0-only` 
- **Target:** `Minecraft 1.21.5`
- **Framework:** `Fabric`
- **Language:** `Kotlin`

[⬆️ Back to top](#-hypixel-dailyrewards)

</div>
