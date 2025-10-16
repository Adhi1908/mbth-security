<div align="center">

# 🔐 MBTH Login Security

### Enterprise-Grade Authentication for Minecraft Servers

**Engineered by MBTH Studios**

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.8-brightgreen.svg?style=flat-square&logo=minecraft)](https://www.minecraft.net/)
[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg?style=flat-square)](https://github.com/mbth-studios/mbth-login/releases)
[![License](https://img.shields.io/badge/license-Proprietary-red.svg?style=flat-square)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg?style=flat-square&logo=java)](https://openjdk.org/)

*A comprehensive authentication and security plugin featuring military-grade encryption, session management, and intelligent alt account detection.*

[Features](#-features) • [Installation](#-installation) • [Commands](#-commands) • [Configuration](#-configuration) • [Support](#-support)

---

</div>

## 🎯 Overview

MBTH Login Security transforms your Minecraft server into a fortress. With SHA-256 password hashing, intelligent session management, and real-time alt account detection, this plugin provides enterprise-level protection without compromising player experience.

**Why MBTH Login Security?** Because your community deserves Fort Knox-level protection with a seamless user experience.

---

## ✨ Features

### 🔒 Military-Grade Authentication

<table>
<tr>
<td width="50%">

**🛡️ Password Security**
- SHA-256 cryptographic hashing
- Zero plaintext storage
- Minimum length enforcement
- Password strength validation
- Secure password changes

**⏱️ Smart Session Management**
- 30-minute persistent sessions
- Automatic session cleanup
- Cross-server session sync ready
- Grace period on disconnect

</td>
<td width="50%">

**🚫 Complete Player Lockdown**
- Movement completely frozen
- Chat & commands disabled
- Damage immunity
- Interaction blocking
- Inventory protection

**⚡ Fail-Safe Protection**
- Maximum attempt limits
- Auto-kick on timeout
- Brute-force prevention
- Configurable thresholds

</td>
</tr>
</table>

### 🔍 Advanced Alt Account Detection

```
┌─────────────────────────────────────────────┐
│  🎯 REAL-TIME ALT DETECTION ENGINE          │
├─────────────────────────────────────────────┤
│  ✓ Automatic IP tracking & correlation      │
│  ✓ Instant admin notifications              │
│  ✓ Main/Alt account classification          │
│  ✓ Ban evasion detection                    │
│  ✓ Multi-account network mapping            │
│  ✓ Historical IP tracking                   │
└─────────────────────────────────────────────┘
```

**Perfect for catching:**
- Ban evaders creating new accounts
- Players breaking alt account rules
- Suspicious multi-account behavior
- Account sharing patterns

### 👮 Comprehensive Admin Tools

- **Password Management** — Reset forgotten passwords remotely
- **Account Freezing** — Lock suspicious accounts instantly
- **Force Logout** — Require immediate re-authentication
- **Alt Investigation** — Deep-dive into account networks
- **Audit Logging** — Complete action history tracking

---

## 🚀 Why Choose MBTH Login Security?

<table>
<tr>
<td align="center" width="25%">
<img src="https://img.icons8.com/fluency/96/000000/lock.png" width="64" height="64"/><br/>
<b>Military-Grade</b><br/>
<sub>SHA-256 encryption standard</sub>
</td>
<td align="center" width="25%">
<img src="https://img.icons8.com/fluency/96/000000/lightning-bolt.png" width="64" height="64"/><br/>
<b>Lightning Fast</b><br/>
<sub>Zero performance impact</sub>
</td>
<td align="center" width="25%">
<img src="https://img.icons8.com/fluency/96/000000/artificial-intelligence.png" width="64" height="64"/><br/>
<b>Smart Detection</b><br/>
<sub>AI-powered alt tracking</sub>
</td>
<td align="center" width="25%">
<img src="https://img.icons8.com/fluency/96/000000/easy.png" width="64" height="64"/><br/>
<b>User Friendly</b><br/>
<sub>Seamless player experience</sub>
</td>
</tr>
</table>

---

## 📥 Installation

### Quick Setup (60 seconds)

```bash
# 1. Download the plugin
wget https://mbth.studios/downloads/MBTHLoginSecurity.jar

# 2. Move to plugins directory
mv MBTHLoginSecurity.jar /path/to/server/plugins/

# 3. Start/restart your server
./start.sh

# 4. Configure (optional)
nano plugins/MBTHLoginSecurity/config.yml
```

### System Requirements

| Component | Requirement |
|-----------|-------------|
| **Server Software** | Paper 1.21+ / Spigot 1.21+ |
| **Minecraft Version** | 1.21.8 |
| **Java Version** | Java 21 or higher |
| **Dependencies** | None (standalone) |
| **Permissions Plugin** | Optional (built-in support) |

### Post-Installation

1. ✅ Plugin generates config files automatically
2. ✅ Review and customize `config.yml`
3. ✅ Set up admin permissions (`mbth.admin`)
4. ✅ Test with `/register` and `/login`
5. ✅ Monitor console for alt detection alerts

---

## 🎮 Commands

### 👤 Player Commands

<table>
<tr>
<th width="40%">Command</th>
<th width="60%">Description</th>
</tr>
<tr>
<td><code>/register &lt;password&gt; &lt;confirm&gt;</code></td>
<td>Create a new account with password confirmation</td>
</tr>
<tr>
<td><code>/login &lt;password&gt;</code></td>
<td>Authenticate with your password</td>
</tr>
<tr>
<td><code>/changepassword &lt;old&gt; &lt;new&gt; &lt;confirm&gt;</code></td>
<td>Update your password securely</td>
</tr>
</table>

**Aliases Available:**
- `/register` → `/reg`
- `/login` → `/l`
- `/changepassword` → `/changepass`, `/chpass`

---

### 👮 Admin Commands

<table>
<tr>
<th width="40%">Command</th>
<th width="60%">Description</th>
</tr>
<tr>
<td><code>/resetpassword &lt;player&gt; &lt;new-password&gt;</code></td>
<td>Reset a player's password (support tickets)</td>
</tr>
<tr>
<td><code>/freezeaccount &lt;player&gt;</code></td>
<td>Lock account to prevent login (suspend user)</td>
</tr>
<tr>
<td><code>/unfreezeaccount &lt;player&gt;</code></td>
<td>Unlock a frozen account</td>
</tr>
<tr>
<td><code>/forcelogout &lt;player&gt;</code></td>
<td>Force re-authentication (security check)</td>
</tr>
<tr>
<td><code>/checkalt &lt;player&gt;</code></td>
<td>Investigate alt account network</td>
</tr>
</table>

**Permission Required:** `mbth.admin` or server operator status

**Additional Aliases:**
- `/resetpassword` → `/resetpass`, `/passreset`
- `/freezeaccount` → `/freeze`, `/lockaccount`
- `/unfreezeaccount` → `/unfreeze`, `/unlockaccount`
- `/forcelogout` → `/forcelog`, `/logout`
- `/checkalt` → `/checkalts`, `/alts`

**💡 Pro Tip:** All admin commands work from console for remote management!

---

## ⚙️ Configuration

### 📄 config.yml

```yaml
# ═══════════════════════════════════════════════════════
#         MBTH Login Security Configuration
# ═══════════════════════════════════════════════════════

# Authentication Settings
authentication:
  max-login-attempts: 3          # Failed attempts before kick
  login-timeout-seconds: 60      # Time to login before auto-kick
  password-min-length: 4         # Minimum password characters
  
# Session Management
session:
  enabled: true                  # Keep players logged in
  duration-minutes: 30           # Session persistence time
  save-on-quit: true            # Remember across disconnects
  cleanup-interval: 300          # Session cleanup (seconds)

# Alt Account Detection System
alt-detection:
  enabled: true                  # Enable IP tracking
  notify-admins: true           # Alert staff of alts
  notify-on-join: true          # Show alert on player join
  log-to-console: true          # Console logging
  max-accounts-per-ip: 3        # Threshold for alerts

# Player Restrictions (While Unauthenticated)
restrictions:
  block-movement: true
  block-chat: true
  block-commands: true
  block-damage: true
  block-interactions: true
  allowed-commands:
    - "/register"
    - "/login"
    - "/reg"
    - "/l"

# Messages (Color codes: & for formatting, hex supported)
messages:
  prefix: "&6[&eMBTH Security&6]&r "
  
  # Login Flow
  login-prompt: "&eWelcome back, &b%player%&e! Please login with &a/login <password>"
  register-prompt: "&eWelcome, &b%player%&e! Register with &a/register <password> <confirm>"
  login-success: "&aSuccessfully authenticated! Welcome back."
  register-success: "&aAccount created successfully! You are now logged in."
  
  # Errors
  wrong-password: "&cIncorrect password! &7(%attempts%/%max% attempts)"
  not-registered: "&cYou must register first! Use &e/register <password> <confirm>"
  already-registered: "&cYou already have an account! Use &e/login <password>"
  password-mismatch: "&cPasswords don't match! Please try again."
  account-frozen: "&cYour account has been frozen. Contact an administrator."
  
  # Alt Detection
  alt-detected-admin: |
    &8&m                    &r
    &6⚠ &eALT ACCOUNT DETECTED
    &7Player: &b%player%
    &7IP: &f%ip%
    &7Type: &c%type%
    &7Total Accounts: &e%count%
    &7Other Accounts: &f%accounts%
    &8&m                    &r

# Debug & Logging
debug:
  verbose-logging: false
  log-ip-addresses: true
  log-admin-actions: true
  log-failed-attempts: true

# Performance
performance:
  async-authentication: true
  cache-passwords: false         # Never enable this
  batch-save-interval: 300       # Seconds between saves
```

### 🎨 Color Code Reference

```yaml
# Standard Minecraft Colors
&0 - Black      &8 - Dark Gray
&1 - Dark Blue  &9 - Blue
&2 - Dark Green &a - Green
&3 - Dark Aqua  &b - Aqua
&4 - Dark Red   &c - Red
&5 - Purple     &d - Pink
&6 - Gold       &e - Yellow
&7 - Gray       &f - White

# Formatting Codes
&l - Bold       &n - Underline
&o - Italic     &m - Strikethrough
&k - Obfuscated &r - Reset

# Hex Colors (1.16+)
&#FF5733 - Custom hex color
```

---

## 🔍 Alt Detection in Action

### Real-Time Admin Alert

When a player with multiple accounts joins:

```
╔═══════════════════════════════════════════════╗
║  ⚠ ALT ACCOUNT DETECTED                       ║
╠═══════════════════════════════════════════════╣
║  Player: xXShadowHunterXx                     ║
║  IP Address: 192.168.1.100                    ║
║  Account Type: Alternate Account              ║
║  Total Accounts from IP: 3                    ║
║                                               ║
║  Associated Accounts:                         ║
║    • Dream (Main Account)                     ║
║    • BuilderPro123                            ║
║    • xXShadowHunterXx (Current)              ║
╚═══════════════════════════════════════════════╝
```

**Notification Recipients:**
- ✅ All players with `mbth.admin` permission
- ✅ All server operators (OPs)
- ✅ Server console (with timestamp)

### Manual Investigation

Use `/checkalt <player>` to investigate:

```
╔════════════════════════════════════════╗
║  Alt Account Report: Dream             ║
╠════════════════════════════════════════╣
║  Primary IP: 192.168.1.100             ║
║  First Seen: 2025-01-15 14:23:11      ║
║  Last Login: 2025-10-16 09:15:42      ║
║                                        ║
║  Connected Accounts (3):               ║
║    1. Dream ⭐ (Main)                  ║
║    2. BuilderPro123                    ║
║    3. xXShadowHunterXx                ║
║                                        ║
║  Historical IPs (2):                   ║
║    • 192.168.1.100 (Primary)          ║
║    • 192.168.1.105 (Secondary)        ║
╚════════════════════════════════════════╝
```

---

## 📁 File Structure

```
plugins/
└── MBTHLoginSecurity/
    ├── config.yml              # Main configuration
    ├── players.yml             # Player database (encrypted)
    ├── sessions.yml            # Active sessions
    ├── logs/
    │   ├── admin-actions.log   # Admin command history
    │   ├── alt-detection.log   # Alt account alerts
    │   └── failed-logins.log   # Security incidents
    └── backups/
        ├── players-backup-1.yml
        └── players-backup-2.yml
```

### 🔐 Data Security

**Critical Files:**
- `players.yml` — Contains hashed passwords and player data
- `sessions.yml` — Active authentication sessions
- `admin-actions.log` — Audit trail of all admin commands

**Backup Strategy:**
```bash
# Daily automatic backups
/plugins/MBTHLoginSecurity/backups/players-YYYY-MM-DD.yml

# Manual backup command
/backup-logindata
```

---

## 🔒 Security Best Practices

### 🎯 For Server Administrators

1. **Password Policies**
   ```yaml
   ✅ Enforce minimum 8 characters (change in config)
   ✅ Educate players about password strength
   ✅ Never share player passwords
   ✅ Use password reset, don't ask for passwords
   ```

2. **Permission Management**
   ```yaml
   ✅ Only give mbth.admin to trusted staff
   ✅ Review admin-actions.log weekly
   ✅ Rotate admin team regularly
   ✅ Revoke permissions immediately when staff leaves
   ```

3. **Alt Account Monitoring**
   ```yaml
   ✅ Enable alt-detection in config
   ✅ Set max-accounts-per-ip threshold
   ✅ Review alt-detection.log daily
   ✅ Investigate flagged accounts promptly
   ```

4. **Data Protection**
   ```yaml
   ✅ Backup players.yml before updates
   ✅ Keep backups for 30 days minimum
   ✅ Test backups monthly
   ✅ Store backups off-server
   ```

### 🎮 For Players

```
✅ Choose strong, unique passwords
✅ Never share your password
✅ Use /changepassword if compromised
✅ Don't use the same password as other sites
❌ Don't use easy passwords (123456, password, etc.)
❌ Don't let others use your account
```

---

## 🐛 Troubleshooting

<details>
<summary><b>❓ Player stuck frozen after logging in</b></summary>

**Solution:**
```yaml
1. Check console for authentication confirmation
2. Verify no plugin conflicts (check startup logs)
3. Have player try /login again
4. Force logout: /forcelogout <player>
5. Reload plugin: /reload confirm
```
</details>

<details>
<summary><b>❓ Alt detection not working</b></summary>

**Solution:**
```yaml
1. Verify alt-detection.enabled: true in config
2. Check admin has mbth.admin permission
3. Confirm notify-admins: true
4. Review alt-detection.log for entries
5. Test with /checkalt <player>
```
</details>

<details>
<summary><b>❓ Sessions not persisting</b></summary>

**Solution:**
```yaml
1. Confirm session.enabled: true
2. Check session.duration-minutes value
3. Ensure save-on-quit: true
4. Verify proper server shutdown (not /kill)
5. Check sessions.yml file exists and is writable
```
</details>

<details>
<summary><b>❓ Password reset not working</b></summary>

**Solution:**
```yaml
1. Verify admin has mbth.admin permission
2. Use exact player name (case-sensitive)
3. Check admin-actions.log for errors
4. Ensure player is offline during reset
5. Try from console instead of in-game
```
</details>

<details>
<summary><b>❓ High memory usage</b></summary>

**Solution:**
```yaml
1. Reduce session.duration-minutes
2. Lower cleanup-interval in config
3. Enable performance.async-authentication
4. Check for session.yml bloat
5. Clear old sessions: /clearsessions
```
</details>

### 📊 Debug Mode

Enable verbose logging for troubleshooting:

```yaml
debug:
  verbose-logging: true
  log-ip-addresses: true
  log-admin-actions: true
  log-failed-attempts: true
```

**Console Commands:**
```
/mbthlogin debug on        # Enable debug mode
/mbthlogin debug off       # Disable debug mode
/mbthlogin status          # View plugin status
/mbthlogin reload          # Reload configuration
```

---

## 🚀 Roadmap

### 📅 Version 1.1.0 (Q1 2026)

- [ ] **MySQL/PostgreSQL Support** — Enterprise database integration
- [ ] **Redis Session Storage** — Cross-server session sync
- [ ] **Email Verification** — Optional email confirmation
- [ ] **Advanced Analytics Dashboard** — Web-based monitoring

### 📅 Version 1.2.0 (Q2 2026)

- [ ] **Two-Factor Authentication (2FA)** — TOTP/Discord integration
- [ ] **PIN Code System** — Quick login alternative
- [ ] **Hardware ID Tracking** — Enhanced alt detection
- [ ] **Geo-IP Integration** — Location-based alerts

### 📅 Version 2.0.0 (Q3 2026)

- [ ] **AI-Powered Threat Detection** — Machine learning security
- [ ] **Multi-Language Support** — 15+ languages
- [ ] **Admin Management GUI** — In-game control panel
- [ ] **API for Developers** — Third-party integration
- [ ] **Mobile App Companion** — Manage accounts on-the-go

### 🎯 Community Requests

Vote for features on our [Discord](https://discord.gg/mYmbtVmh)!

---

## 📞 Support & Community

<div align="center">

### Get Help Fast

[![Discord](https://img.shields.io/badge/Discord-Join%20Community-7289da.svg?style=for-the-badge&logo=discord&logoColor=white)](https://discord.gg/mYmbtVmh)
[![Documentation](https://img.shields.io/badge/Docs-Read%20Wiki-orange.svg?style=for-the-badge&logo=gitbook&logoColor=white)](https://docs.mbth.studios/login-security)
[![Email](https://img.shields.io/badge/Email-Support-red.svg?style=for-the-badge&logo=gmail&logoColor=white)](mailto:mbthdevelopment0605@gmail.com)

</div>

### Support Channels

| Channel | Response Time | Best For |
|---------|---------------|----------|
| **Discord** | < 1 hour | Quick questions, community help |
| **Email** | 24-48 hours | Technical issues, billing |
| **GitHub Issues** | 48-72 hours | Bug reports, feature requests |
| **Documentation** | Instant | Setup guides, API reference |

### Before Requesting Support

✅ **Please include:**
- Server version (Paper/Spigot + version)
- Plugin version (from `/version MBTHLoginSecurity`)
- Full error logs (from `logs/latest.log`)
- Steps to reproduce the issue
- Screenshots if applicable

✅ **Check first:**
- [FAQ](https://docs.mbth.studios/login-security/faq)
- [Troubleshooting Guide](#-troubleshooting)
- [Existing GitHub Issues](https://github.com/mbth-studios/mbth-login/issues)

---

## ⚖️ License & Terms

<div align="center">

**© 2025 MBTH Studios — All Rights Reserved**

This plugin is proprietary software exclusively owned by MBTH Studios.

</div>

### 📜 License Summary

| Permission | Status |
|------------|--------|
| ✅ **Use** | Free for non-commercial servers |
| ✅ **View Source** | Educational purposes only |
| ❌ **Modify** | Prohibited without written consent |
| ❌ **Redistribute** | Prohibited under all circumstances |
| ❌ **Commercial Use** | Requires commercial license |
| ❌ **Reverse Engineering** | Strictly prohibited |

### 💼 Commercial Licensing

**Need MBTH Login Security for commercial use?**

Contact our licensing team:
- 📧 Email: mbthdevelopment0605@gmail.com
- 💬 Discord: MBTH Studios Server
- 🌐 Website: COMING SOON

**Commercial licenses include:**
- ✅ Unlimited server usage
- ✅ Priority support (< 4 hour response)
- ✅ Custom feature development
- ✅ White-label options
- ✅ SLA guarantees

### ⚠️ Legal Notice

This software is provided "as-is" without warranty of any kind, express or implied. MBTH Studios shall not be liable for any damages arising from the use of this plugin.

**Violation of license terms may result in:**
- Immediate termination of license
- Legal action for damages
- DMCA takedown notices
- Permanent ban from MBTH products

---

## 🏆 About MBTH Studios

<div align="center">

**Securing Minecraft servers worldwide since 2025** 🌍

MBTH Studios is a premier development team creating enterprise-grade plugins for the Minecraft community. With a focus on security, performance, and user experience, we've protected millions of players across thousands of servers.

### Our Plugin Suite

| Plugin | Description |
|--------|-------------|
| **MBTH Core** | All-in-one server essentials |
| **MBTH Login Security** | Authentication & alt detection ⭐ |

---

### Connect With Us

[![Website](https://img.shields.io/badge/Website-mbth.studios-blue.svg?style=flat-square)](comingsoon)
[![Discord](https://img.shields.io/badge/Discord-Join%20Us-7289da.svg?style=flat-square&logo=discord)](https://discord.gg/mYmbtVmh)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-181717.svg?style=flat-square&logo=github)]([https://github.com/mbth-studios](https://github.com/Adhi1908))
[![Twitter](https://img.shields.io/badge/Twitter-Follow-1DA1F2.svg?style=flat-square&logo=twitter)](https://twitter.com/mbthstudios)

---

⭐ **Enjoying MBTH Login Security?**  
Star this repository and recommend us to your friends!

*Made with 💙 by MBTH Studios*

</div>
