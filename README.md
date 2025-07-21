# 🎁 [Hypixel] DailyRewards

<div align="center">

![Modrinth Downloads](https://img.shields.io/modrinth/dt/hypixel-dailyrewards?logo=modrinth&label=downloads&style=for-the-badge&color=00AF5C)
![Modrinth Game Versions](https://img.shields.io/modrinth/game-versions/hypixel-dailyrewards?logo=modrinth&label=game%20versions&style=for-the-badge)
![Modrinth Version](https://img.shields.io/modrinth/v/hypixel-dailyrewards?logo=modrinth&label=version&style=for-the-badge&color=00AF5C)
![GitHub Issues](https://img.shields.io/github/issues/Cqseur/DailyRewards?style=for-the-badge&color=red)
![License](https://img.shields.io/badge/License-LGPL--3.0-blue?style=for-the-badge)

**🎁 Automate Hypixel Skyblock daily rewards with beautiful card animations and instant claim notifications**

[📥 Download on Modrinth](#download) • [⚡ Features](#features) • [💬 Discord](https://discord.gg/tkUqGeDN5J)

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
1. **Download** the latest version from [Modrinth](https://modrinth.com/mod/hypixel-dailyrewards/versions)
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
- **[Claude 4.0 🤖](data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAdHx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys/RD84QzQ5OjcBCgoKDQwNGg8PGjclHyU3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3N//AABEIAJQAmwMBIgACEQEDEQH/xAAcAAEAAQUBAQAAAAAAAAAAAAAAAQIDBAYHBQj/xAA9EAABAgMFBgQDBgUEAwAAAAABAgMAERIEITFBUQUGEyIyUiMzQmFDcfBigZGhscEUJDRT0QcVcuGDkqL/xAAaAQEAAgMBAAAAAAAAAAAAAAAAAwQCBQYB/8QAKBEAAgEDAwMDBQEAAAAAAAAAAAECAwQREiExBUFxIoHhEyQyYcEU/9oADAMBAAIRAxEAPwDsoJSqtAm6rqRpASSCGuZKus6QAUTJsyeHWcjASIPCklA8yecAJJp4c/A74GSgEuGlsdKtYTTw6wP5ftz+pwMkgF29s9A0gASVKCl3OJ6E6wvCy4B4xxRoIEEEBwzdPQdIXlRSCBaM1e0AByElu9SusdsJJAoSZtHqXpAXz4UkqHme8OUprR5I6k6wAICk0LMmh0q1geYjiikpPIO6BkE1OCbJ6U5iBuID0ionw/aAEzXWRJ7JGogCUkqb5nFdadIc1VJ8/uGAEACVENGTo6yc4AiQCCls1Nq61aRJAIoPkjBfvASKSW7mxctJziJppqMjZ8k5gwBNygA4aUJ6D3QJJNa+V4dKNYGSZF29B8saQkoKpXe8elQwEATeFFaBN49SdIgAJB4XMFdf2fq+AmSUoID/AKlQEiDwZADzPf6vgAAAnhpM2TirSKgt1IpaRUgYHWKRTTUn+nzTEpS8RNpQCPSDAESChQTSE/E7oXrvIop9PdC4pAcuZHSc4k3kF2QWOj3gCJzAdlf/AGodPNTWVYo7Ym+qoy/iO3KIBIJLQm4esHKAEgkUTqCjeufTAD4RMgL+LrAUgEN3tnrOkYlq2lYbKkotD6EtC8AGao8bS5MoxlJ4ismX1SB5QnA98Jz8SVMvh6xrzm+ewgvhu2xPKeQCQI+d8Z9i2/s23yWzbGy9gkEyB/aMVOL4ZLO2rQWZRa9j0py8QJqJ+HpCVFwNdQx7IAmdSL3T1DKUBIA8KRB8yeUZkAlLw5zBvLmkJBXIeQJ9fdEcoTSPIzOc4tv2hhlE7U6ltkdBJxgepNvCLpmvnIoKT0d0Jy8SV5+FpHh2ne3YzKpv2ocRPTSImyb2bGtSqm7a2HzdJQujHXF9yZ21ZLLg8eD250XgVlWKeyAFPhzrB+JpFDLqHEcWyLS5VesgzEVikIpbmWT1E4xkQCVUm50gYOawPPjyU4DvgQmmldzAwOcDfLi3EeXLP6ugBj4hTIgeXrAthfMXaCfTPD84cxVUoeP6RlKIIYJm8ohzMQBJISAtYqbPSjSBBTIOc6ldB7YAlPiIFTh6kdsByTS2agrqPbACRq4fxv7kJzJCDQpPWo5wupon4X9yPM23aCllFmTy1z5vsDGPJPCyZ04a5KJjW622m3OGy7LTJJGM5A+5OQ/WPMf3UsSlFe0n3rWceHUUNg53A3j5zjYrBZxY7CCpMnXQFKnkMh93+dY8DeTbdl2W1xLU7TUZJSBMqOgEV5RWNUzZW0p6tFDbxyzT95d0LEUqd2WVNOAeUpc0q+U8D+UaDxXrI6Swt1tSTIpN0j7iN0tO+jDij/KvhJOMxeI1Xb1os1stYtFmmCseICmUiMD9aRUlpzmJ01D/AEafp102jad1d/LdYwEWh0FM70qwl+3zF0dO2dvJsy3tJcS+izKAmtDqgmr5HOPnNU6gcFJvBEbFuoq123abVkFpU2yQVOFAHKkDETwyiaFdrbk1tz0inNufB1zau9LaFcHZ4nXcnlmVK+ynMxif7WbS0p7bDrpWsEBoLIpHucZxXZEbO2Qypxm9yXM+6qapfPIewlGpbd37s4UpuxIL5F1U5J/HOMpS7zZXt7Zt6aEfL7/Bqm8VmbsG1rTZ2wpSEKFJUq+RAP7x46ljqqKFC8G++L207c5b7W5aXpBS5cowAF0YKoqJb5R0cpS+moyZuu7G89qs9onsx9bTwE12N1VSHB9g4/cfxjrm7u3rLtyxl+zgNluXHs5xBM7/AJGX5R80BSmnEuNqpWg1A6GOibvbdOzdqWLaqDJl9FTyBgRgsfcZGLtKfY5fqFos6kdoMgmtQqaOCNIkzR5nOVdB7YA/FRzLWOjKUBJEw3zBXWe2LRoxI1cMqm8bwvQRBcaSaXG61jFUsYmQCeGPKN5XpE1uI5W26kjAyxgCJKqPDlx/UdYCUjwegeZPOAFR4YNKhi53QB4gmBRTiO6AIMqBIfy+mf1OPDtZ/idu8E+WkNtgfZJBI/CYj3RhXIgf2v8AqNYtjvD28+Z0VFsplkVAAfrEVV4SLVrHLl4Nit6pCOJb+2pdq3itDaiaLOEtpHzAUf1H4R2GyOm07GsL61Eqds6FEnEkpEcb3yaKN5bfMHmWlQ/9ExWun6TedCilWkn2RrqxFlQjJWIsqEVEdOywRGVs632nZrpdsqglRxCkzBiwRFOcZEUoprDPR2ltu37SSEWp7wxfw0CST89Y80kxJiiPfJ4korESDFtUVmKDHqK82etuuum3OoyUiZH3/wDcbBtWRaZ/5kflP9o13dtP88tWjcvzH+I2DaZ8JkauT/8Akxah+BoLt5uDrm59pVbN2tnOoM3+AEqUfs3fsI9gEEHg3D4k/r5xr+4CCd0dnovQShSqsJgqMbBc4DIUUY/bi0uDQz2kxdKaf6fMZziQH5eCUhv0ziJzBdlJI+FrEhpSxUHSgH0i6UemJEkkBDhkyOg6wM1SLwkodHuYEpSmpwTZPQnSCuUgO8y1dB0gBzVVkePknL6lGq70tFNvS4g0qfaKCe1YwP6fhG1SM+HPx++PK3jsn8Zs5zgp8azniEy6pYy+6I6sdUGWbSoqdVN8GNulaxbt3mpdbK1tFPbImQ/AiOff6jWQt7aQ+J0utAfeCf8AIjZNzLamy7etNgUaWrcnjNacQYj5kfpD/UnZ5d2e3aQL7O5f8lXfrKKsvXS/Zvbb7bqGO0v78nKnExYUIznURirTfFRHTNmOoRQRF1QihQjIjbKDFBisxSRGRHJlsxTKKjcYvWVkvPttgdSgD8s4ziinWmluz3ti2RLLKV31LQmc/lP94y9rLCLO3yzVzKEsdBF5hF4SkYxnbu2D/eN67K0U1WazHiunKlF/5qkPxi2o7YOblU1Tc2dV2LY/4LYtisT1yGWEJKh3AX/nOM080uKKSPL9/q6BKQmtYmyelOn1fBQpkHDUVeX9mJzVt53F5XUqQfHSn2iCllRJdXJeYiTVVQfON6V+0QpxlJIdQVLGJGcASCUkuIFSlYo7YeXNKDWFYntgJ1eHe/6wcICVJ4N6PiTgCJCkNA8n9yJUK+RZpSnBWSoctN39NrnA0yHGPhegwBznezZj+z7cm02MKbUlzjWcy6Tmn5Ri7zb8J2hs1Nls9jKXHm5PqcPKk4EJGJPz9o6LtSwo2lZV2a03Om9tQyOUcj3k2M7Y33QtulaFc6R+o9ooV4yp5ceGdP0urQutMa35R4PCJCkiUYrqb4ujkJEULviomdHKOGYixFtUXXbotTmIzRFJ4RQRFJitRlFMiYzSK8p4LShzJHvHtbHs3iKdIvAkmPLQib7STgoxtNlaQyyAo0oSJqMT04movq+2EXXXRZbOpwkBR5U/5jo3+nmwlbJ2SbZaUqTbbXJam1YhA6U/mSfcxqm5Ww/982iNo25EtnWZUkJODqxgn3AxP4ax1UzmK5B/0xZgu5oq08LQvcAlJ4iRNZxb0gOSYTzhWP2YCdR4fn+qeAgLgrg3jByeX1fEhWEpDhzmg4uaRPEWjlQyVpGCpG+KZppNP9P6tZxUnjgDgyLfpnAEAFRLaTSpPUvWAkuZRyBF6h3QICkhCzJpPSvWBmsgu8hT0DugBMEF2Xh/24E0ALWKkquSnthMlXEl4w+HATSSpAqcV1p7YAEFJCFXqVgvtjytu7Gb2o1QmQtbYJQ6cFDtPt+keqAEgoQZtq6ldsRIFIbJ8EYL1jxpNYZlCcoNSi8M4rtrYzjD7iSjhrRcUqyOny0P0NeWFJJSsEEXEHKO87a2OxtZrxZNvJEmnZdQ0Oojlm8m7z9keUhxlSHQJjRQ9tR+ka+tQcN0df0/qsa60VOTUXRMRYllGS4lSFFChIjKMdRkR8xEMTZ1X6SQySb8IupaCUkyi+sobE1ql7CMFbr1odLbCZk5do1PvFjZGolOU/BcsdL20EFZCW0frG6bA2Lat5LZw2wpqwMqHHeAw9h9o/lj88Pc3c9/a7s0zas6T4loIxOaUan9M9D2bZths2y7I1Z7CylsNiQaH6nMk4zixTg+5p724jqxHkrsdlZ2dZGbPZ2gmzoSEttj0D/PvF4gpk2TUtWDmkEkoJU2Klq6h2wSEpSW0Gpo9S9InNUJVK4QMnM16wElzo5aeod0JJKQ2pUmhgvWCprlxOUp6B3QAqmni0ybGLesSG3FipDhQk4J0gZlXEIk6MEaxSpDSjU45Qs4pnhAEmmmpweCehIygZiXF5lny5ZQBok5Koq+H2wA4d06wr1dsAOaoIN9o7svqUEzKiGpBwdZOBhLFqf/AJYdfJOgJ9fdAAFJBLdzQ6wcTEXUVEfy+Sc5xJNfPKmn0d0CZHiymTdwtIAKkkJ416T0e0Y20LAztBn+GtqEuOEcq+35HKMnoJPWV5dkJU+FOZV8TSGMnqbTyjl+9O5jrNa0Ditp+KgcyPn9Sjn9vsFps5Ph1jIpj6QkVeHVTT8TWPH2luzsraZUt2z8BwYqbMq/uwivO3T3Rt7bq9SnHTU3RwKz7OtFpM3ptpJvHqP3R0bdTcAlDdo2uhVnspM0sDzHf+Wg/P5RvOyd3dl7KIfs1m8YXAuGpX3afcI9Ych4kqyr0dsZQpKPJBc9QlV2jsi2xZ2rGy3Z2mkNhAAZS2AEpHyi4AaqQfHzVkRCVAoBrCvX2wlPwp4fF1iY1wEySGrljzPeE001IADHqTCVckzoo9XfCdQ4kqafh6wAMggKWCWPSkYwVNMuNfPy5ZQnSS7Kon4ekOgGRrrz7IAGdYSZfxGSsohSmASHUkr9RETKXhTnP4mkSHKOUNVy9WsABMKKmxN49Y0iBIAhm9B6zpAAqJQg0uDqXrAc0y3yJT1jugBJITT8DNRxgZEBLtzY6DrCYoDgHgj4cDJICnBWk9I0gAZlQLkg6OgawE6qgPHOKcpRJBBCVczh6V6RABqoB8bNzUQBIunwr1HzAcojlCSEmbPqOYgAVXI5VJ6z3REwUlaUyaHUjX6ugCVUkBKzJn0q1+r4KmSOLcR5cs4GQAWoTbPSjSBmmQcNaldB7YATVVUQOPkn2iQSklTV7h6xpESINCjN04OaQAKiUo5Vp61d0AAAAQ3e2es5iFxTI+RkrOHUCpHKhJ5k6wmKQ58HJuABkQA7cgeWRnAzKwVgcYdIGEDJIBcFSVdI7YSKVULvdPSvSAAqCipAm+epOQgOWfCvB8yeX1fAAlVCDS6MV6wAnPh8oT1jugBcElKfIzVnFQLwEmQCj0mKZikrAk0MUaxIbdUKm3KEnBM8IAgipPDJuTnmYK5yCbqcJZwhACfMHc9MokchKxeVYg4REIAmmgFAJkrEmIlNIa9IvnnEQgCTzgA3UYSiSalBzNOWUIQAnSouDE5ZRA5AQL68Z5REIAkJkjhXyN884kisBBwRgREQgCVGtQWcU4SiJyVxczdLKEIAkchKxeVYgwApSWgSQczjEQgBKpIavkM84HxCJ3UYSiIQBUb1cXMZZRSpoLNRJmdIQgD/2Q==)** - Author of this beautiful description

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

![Modrinth Downloads](https://img.shields.io/modrinth/dt/hypixel-dailyrewards?logo=modrinth&style=for-the-badge&color=00AF5C)
![GitHub Stars](https://img.shields.io/github/stars/Cqseur/DailyRewards?style=for-the-badge&color=yellow)
![GitHub Forks](https://img.shields.io/github/forks/Cqseur/DailyRewards?style=for-the-badge&color=blue)
![GitHub Issues](https://img.shields.io/github/issues/Cqseur/DailyRewards?style=for-the-badge&color=red)

**Made with ❤️ for the Hypixel community**

[🟢 Download on Modrinth](https://modrinth.com/mod/hypixel-dailyrewards#download) • [🌟 Star on GitHub](https://github.com/Cqseur/DailyRewards) • [💬 Join Discord](https://discord.gg/tkUqGeDN5J)

---

**📊 Project Statistics:**
- **Version:** `1.21.5+v1.0.1`
- **License:** `LGPL-3.0-only` 
- **Target:** `Minecraft 1.21.5`
- **Framework:** `Fabric`
- **Language:** `Kotlin`

[⬆️ Back to top](#🎁-hypixel-dailyrewards)

</div>
