# MBTH Login Security

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/Adhi1908/mbth-security)
[![Minecraft](https://img.shields.io/badge/minecraft-1.20.1+-green.svg)](https://www.minecraft.net/)
[![License](https://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)
[![Spigot](https://img.shields.io/badge/spigot-download-yellow.svg)](https://www.spigotmc.org/resources/mbth-login-security.129264/)
[![Discord](https://img.shields.io/discord/YOUR_SERVER_ID?color=7289da&label=discord&logo=discord&logoColor=white)](https://discord.gg/Q4xe2Uhksj)

**Advanced login security system for Minecraft servers with PIN authentication, premium player detection, Discord webhooks, and comprehensive admin tools.**

---

## 🌟 Features

### 🔐 Core Security
- **Password Authentication** - Secure SHA-256 hashed passwords
- **PIN Code System** - Additional 4-digit PIN for extra security
- **GUI PIN Vault** - Beautiful graphical PIN entry with custom balloon number heads
- **Session Management** - Remember authenticated players (configurable duration)
- **Login Timeout** - Auto-kick players who don't login in time
- **Failed Attempt Protection** - Kick after too many wrong password/PIN attempts

### 👥 Player Management
- **Premium Player Detection** - Auto-authenticate paid Minecraft accounts
- **Login Method Choice** - Players can choose between PIN or password login
- **Alt Account Detection** - Automatic IP-based alt account tracking
- **Account Freezing** - Temporarily lock suspicious accounts
- **Force Logout** - Admin can force players to re-authenticate

### 🎨 Customization
- **Custom Branding** - Personalize server name, titles, and messages
- **Color Coded Messages** - Professional UI with Minecraft color codes
- **Configurable Settings** - Every feature can be enabled/disabled
- **Discord Webhooks** - Rich embed notifications with colors and emojis

### 🛠️ Admin Tools
- **Password Reset** - Reset player passwords as admin
- **PIN Reset** - Remove player PINs
- **Account Unregister** - Completely delete player accounts
- **Freeze/Unfreeze** - Lock and unlock accounts
- **Alt Checker** - View all accounts from same IP
- **Config Reload** - Hot-reload configuration without restart

---

## 📥 Installation

1. **Download** the latest `MBTHLoginSecurity-1.0.0.jar`
2. **Place** the JAR file in your server's `plugins/` folder
3. **Restart** your server
4. **Configure** the plugin in `plugins/MBTHLoginSecurity/config.yml`
5. **Reload** with `/mbthlsreload`

### Requirements
- Minecraft Server (Paper/Spigot) 1.20.1+
- Java 17+
- No dependencies required!

---

## 🚀 Quick Start

### For Players

#### First Time Join
```
/register <password> <confirm-password>
/setuppin <pin> <confirm-pin>
```

#### Login
```
/login <password>
```
or use the **GUI PIN Vault** for quick login!

### For Admins

#### Reset Player Password
```
/resetpassword Steve newpass123
```

#### Check Alt Accounts
```
/checkalt Steve
```

#### Freeze Suspicious Account
```
/freezeaccount Steve
```

---

## 📝 Commands

### Player Commands

| Command | Description | Usage |
|---------|-------------|-------|
| `/register` | Create new account | `/register <pass> <confirm>` |
| `/login` | Login to account | `/login <password>` |
| `/changepassword` | Change password | `/changepassword <old> <new> <confirm>` |
| `/setuppin` | Create PIN code | `/setuppin <pin> <confirm>` |
| `/verifypin` | Verify PIN | `/verifypin <pin>` |
| `/changepin` | Change PIN | `/changepin <old> <new> <confirm>` |

### Admin Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/resetpassword` | Reset player password | `mbth.admin` |
| `/resetpin` | Remove player PIN | `mbth.admin` |
| `/unregister` | Delete player account | `mbth.admin` |
| `/freezeaccount` | Lock player account | `mbth.admin` |
| `/unfreezeaccount` | Unlock player account | `mbth.admin` |
| `/forcelogout` | Force player logout | `mbth.admin` |
| `/checkalt` | Check alt accounts | `mbth.admin` |
| `/mbthlsreload` | Reload config | `mbth.admin` |

---

## ⚙️ Configuration

### Basic Setup

```yaml
# Server Branding
server-name: "MBTH"

# Login Settings
max-login-attempts: 3
login-timeout-seconds: 60

# Session (Stay Logged In)
session:
  enabled: true
  duration-minutes: 30

# PIN Code System
pin-code:
  enabled: true
  required: true  # Force all players to setup PIN
  length: 4
  max-attempts: 3

# Premium Player Bypass
premium-bypass:
  enabled: true  # Auto-login for paid Minecraft accounts

# Alt Detection
alt-detection:
  enabled: true
  notify-admins: true
```

### Discord Webhooks

```yaml
discord:
  enabled: true
  login-webhook: "https://discord.com/api/webhooks/YOUR_ID/YOUR_TOKEN"
  registration-webhook: "https://discord.com/api/webhooks/YOUR_ID/YOUR_TOKEN"
  log-premium-login: true
  log-failed-attempts: true
  username: "MBTH Security"
  avatar-url: "https://i.imgur.com/AfFp7pu.png"
```

**See [DISCORD_WEBHOOK_GUIDE.md](md/DISCORD_WEBHOOK_GUIDE.md) for setup instructions.**

---

## 🎨 Customization

### Custom Messages

All messages support Minecraft color codes (`&a`, `&c`, etc.):

```yaml
messages:
  welcome-premium: "&7Welcome to {server}! You have been automatically authenticated."
  login-success: "&a&l✔ Successfully logged in!"
  register-success: "&a&l✔ Successfully registered!"
  pin-verified: "&a&l✔ PIN verified successfully!"
```

### Custom Titles

```yaml
titles:
  main-title: "&6✦ &e&lMBTH Security &6✦"
  subtitle: "&ePlease login to continue"
  scoreboard-title: "&6&lMBTH LOGIN SECURITY"
```

---

## 🔔 Discord Integration

### Webhook Notifications

The plugin sends beautiful rich embeds to Discord:

#### 🟢 Login Events (Green)
- Player logged in with password
- Player logged in with PIN
- PIN verified successfully
- Premium player auto-login

#### 🔵 Registration Events (Blue)
- New player registered
- PIN setup completed

#### 🔴 Security Alerts (Red)
- Failed login attempts
- Player kicked for too many attempts
- Account unregistered by admin

**No external plugins required!** Uses native HTTP webhooks.

---

## 🛡️ Security Features

### Password Protection
- **SHA-256 Hashing** - Passwords are never stored in plain text
- **Salted Hashing** - Each password has unique hash
- **Attempt Limiting** - Configurable max attempts before kick
- **Session Validation** - Prevents unauthorized access

### PIN Code Security
- **GUI-Based Entry** - Secure graphical PIN pad
- **Number Balloons** - Beautiful custom head textures
- **Attempt Tracking** - Separate attempt counter for PIN
- **Optional/Mandatory** - Can be required or optional
- **Easy Reset** - Admins can reset forgotten PINs

### Alt Account Detection
- **IP Tracking** - Monitors accounts from same IP
- **Main/Alt Labels** - Identifies primary account
- **Admin Notifications** - Alerts when alts join
- **Detailed Reports** - View all accounts per IP

---

## 📊 Premium Player Detection

### How It Works

The plugin automatically detects **premium players** (those who purchased Minecraft):

- ✅ **Auto-Login** - Premium players skip authentication
- ✅ **No Registration** - No password/PIN needed
- ✅ **Instant Access** - Immediate gameplay
- ✅ **Secure** - Uses Minecraft's built-in authentication

### Configuration

```yaml
premium-bypass:
  enabled: true  # Enable auto-login for premium players
```

**Note:** Only works on servers in **online mode**.

---

## 🎮 Login Method Choice

Players with both password and PIN can choose their login method:

### GUI Login Choice Menu

When a player joins, they see a beautiful GUI:

```
┌─────────────────────────────────┐
│     ✦ Choose Login Method ✦     │
├─────────────────────────────────┤
│                                 │
│   🚪 Login with PIN             │
│   Use your secure PIN code      │
│                                 │
│   📄 Login with Password        │
│   Use your account password     │
│                                 │
└─────────────────────────────────┘
```

### Configuration

```yaml
login-method-choice:
  enabled: true  # Show choice menu to players
```

---

## 📁 File Structure

```
plugins/
└── MBTHLoginSecurity/
    ├── config.yml          # Main configuration
    ├── players.yml         # Player data (auto-generated)
    └── MBTHLoginSecurity-1.0.0.jar
```

### Data Storage

Player data is stored in `players.yml`:

```yaml
players:
  <uuid>:
    password: <hashed>
    pin: <hashed>
    registered-date: <timestamp>
    last-login: <timestamp>
    last-ip: <ip>
    session-end: <timestamp>
```

---

## 🎯 Use Cases

### Cracked Server
- Enable all security features
- Require PIN for extra protection
- Monitor alt accounts
- Use password + PIN authentication

### Hybrid Server (Premium + Cracked)
- Enable premium bypass
- Auto-login for paid accounts
- Require auth for cracked players
- Give players login choice

### Premium Only
- Disable most features
- Use only for session management
- Optional PIN for extra security

---

## 🐛 Troubleshooting

### Players Can't Login

**Check:**
1. Are they registered? `/checkalt <player>`
2. Is account frozen? `players.yml` frozen list
3. Correct password? Reset with `/resetpassword`
4. Check console for errors

### Discord Webhooks Not Working

**Check:**
1. `enabled: true` in config
2. Valid webhook URL (starts with `https://discord.com/api/webhooks/`)
3. Channel still exists
4. Run `/mbthlsreload`

### Premium Detection Not Working

**Check:**
1. Server is in **online mode** (`server.properties`)
2. `premium-bypass: enabled: true`
3. Player has genuine Minecraft account
4. Check console logs

### PIN Vault Not Opening

**Check:**
1. `pin-code: enabled: true`
2. Player has registered first
3. No inventory plugin conflicts
4. Check console for errors

---

## 📚 Documentation

Detailed guides available in the `md/` folder:

- **[CONFIG_GUIDE.md](md/CONFIG_GUIDE.md)** - Complete configuration reference
- **[DISCORD_WEBHOOK_GUIDE.md](md/DISCORD_WEBHOOK_GUIDE.md)** - Discord setup instructions
- **[DISCORD_WEBHOOK_QUICK_START.md](md/DISCORD_WEBHOOK_QUICK_START.md)** - 5-minute setup
- **[PIN_SYSTEM_DOCUMENTATION.md](md/PIN_SYSTEM_DOCUMENTATION.md)** - PIN system details
- **[UNREGISTER_COMMAND_GUIDE.md](md/UNREGISTER_COMMAND_GUIDE.md)** - Unregister command reference
- **[NEW_FEATURES_DOCUMENTATION.md](md/NEW_FEATURES_DOCUMENTATION.md)** - All features explained
- **[LOGIN_CHOICE_GUIDE.md](md/LOGIN_CHOICE_GUIDE.md)** - Login method choice

---

## 🔄 Updates & Changelog

### Version 1.0.0
- ✅ Password authentication with SHA-256
- ✅ GUI PIN Vault system
- ✅ Premium player detection
- ✅ Discord webhook integration (replaces DiscordSRV)
- ✅ Alt account detection
- ✅ Session management
- ✅ Login method choice GUI
- ✅ Complete admin toolset
- ✅ Account freeze/unfreeze
- ✅ Unregister command
- ✅ Customizable messages & branding

---

## 🤝 Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 💡 Support

### Need Help?

- **Discord:** [Join our server](https://discord.gg/Q4xe2Uhksj)
- **Issues:** [GitHub Issues](https://github.com/Adhi1908/mbth-security/issues)
- **Wiki:** [Documentation](https://github.com/Adhi1908/mbth-security/wiki)
- **Spigot:** [Download & Reviews](https://www.spigotmc.org/resources/mbth-login-security.129264/)

### Report Bugs

Found a bug? Please report it on our [GitHub Issues](https://github.com/Adhi1908/mbth-security/issues) page with:
- Server version
- Plugin version
- Error logs (from console)
- Steps to reproduce

---

## 🌟 Features Roadmap

### Planned Features
- [ ] 2FA via email/Discord
- [ ] Captcha system
- [ ] Hardware ID binding
- [ ] Account recovery system
- [ ] Multi-language support
- [ ] MySQL/MongoDB support
- [ ] Login history viewer
- [ ] Security statistics dashboard

---

## 👏 Credits

**Developed by:** MBTH Studios  
**Lead Developer:** Adhi1908  
**GitHub:** [github.com/Adhi1908](https://github.com/Adhi1908)  
**Spigot:** [MBTH Login Security](https://www.spigotmc.org/resources/mbth-login-security.129264/)

### Special Thanks
- Minecraft community for feedback
- Contributors and testers
- Discord for webhook API
- Paper/Spigot teams
- All supporters and users

---

## 📊 Statistics

- **Commands:** 15+
- **Permissions:** 1 (`mbth.admin`)
- **Configuration Options:** 30+
- **Features:** 20+
- **Documentation Pages:** 10+

---

## ⚡ Performance

- **Lightweight:** < 100KB JAR size
- **Async Operations:** Discord webhooks, data saving
- **No TPS Impact:** Optimized event handling
- **Fast Authentication:** < 10ms average
- **Memory Efficient:** Minimal RAM usage

---

## 🔒 Security Best Practices

1. **Regular Backups** - Backup `players.yml` regularly
2. **Strong Passwords** - Encourage players to use strong passwords
3. **Monitor Logs** - Check console for suspicious activity
4. **Discord Alerts** - Enable webhook notifications
5. **Alt Detection** - Keep alt detection enabled
6. **Freeze Suspicious** - Use `/freezeaccount` for suspicious behavior
7. **Audit Trail** - Review console logs periodically

---

## 🎨 Screenshots

### Login Screen
![Login Prompt](https://via.placeholder.com/800x400?text=Login+Prompt)

### PIN Vault GUI
![PIN Vault](https://via.placeholder.com/800x400?text=PIN+Vault+GUI)

### Login Method Choice
![Login Choice](https://via.placeholder.com/800x400?text=Login+Choice+Menu)

### Discord Notifications
![Discord Webhook](https://via.placeholder.com/800x400?text=Discord+Notifications)

---

## 📞 Contact

- **Discord:** [MBTH Studios Server](https://discord.gg/Q4xe2Uhksj)
- **GitHub:** [@Adhi1908](https://github.com/Adhi1908)
- **Spigot:** [Plugin Page](https://www.spigotmc.org/resources/mbth-login-security.129264/)
- **Issues:** [Report Bugs](https://github.com/Adhi1908/mbth-security/issues)

---

## ⭐ Show Your Support

If you find this plugin useful, please:
- ⭐ Star this repository
- 🐛 Report bugs
- 💡 Suggest features
- 📢 Share with others
- 💰 Consider [donating](https://mbth.dev/donate)

---

<div align="center">

**Made with ❤️ by MBTH Studios**

**Lead Developer: Adhi1908**

[GitHub](https://github.com/Adhi1908/mbth-security) • [Discord](https://discord.gg/Q4xe2Uhksj) • [Spigot](https://www.spigotmc.org/resources/mbth-login-security.129264/)

</div>

