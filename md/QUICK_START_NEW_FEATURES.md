# 🚀 Quick Start - New Features

## Feature 1: Custom Server Name

### Step 1: Edit Config
```yaml
server-name: "YourServerName"
```

### Step 2: Reload
```
/mbthlsreload
```

### Done! ✅
All messages now show your custom server name!

---

## Feature 2: Discord Integration

### Step 1: Install DiscordSRV
Download and install DiscordSRV plugin from SpigotMC

### Step 2: Create Discord Channels
Create channels in your Discord server:
- `#login-logs`
- `#security-logs` (or any name you want)

### Step 3: Edit Config
```yaml
discord:
  enabled: true
  login-channel: "login-logs"
  registration-channel: "security-logs"
  log-premium-login: true
  log-failed-attempts: true
```

### Step 4: Reload
```
/mbthlsreload
```

### Done! ✅
Login/registration events now appear in Discord!

---

## Feature 3: Reload Command

### Usage
```
/mbthlsreload
```

### Aliases
- `/mbthlsr`
- `/reloadmbthls`

### What it does
- Reloads config.yml
- Updates all settings
- No server restart needed!

---

## Example: Everything Together

```yaml
# config.yml
server-name: "AwesomeNetwork"

discord:
  enabled: true
  login-channel: "auth-logs"
  registration-channel: "auth-logs"
  log-premium-login: true
  log-failed-attempts: true
```

Run `/mbthlsreload` and you're done! 🎉

---

## Commands Reference

| Command | Aliases | Permission | Description |
|---------|---------|------------|-------------|
| `/mbthlsreload` | `/mbthlsr`, `/reloadmbthls` | `mbth.admin` | Reload plugin configuration |

---

## Config Reference

```yaml
# Server Branding
server-name: "MBTH"

# Discord Integration
discord:
  enabled: false
  login-channel: ""
  registration-channel: ""
  log-premium-login: true
  log-failed-attempts: true
```

---

## Discord Event Types

| Event | Icon | Example Message |
|-------|------|-----------------|
| Registration | :new: | **Player123** registered a new account! |
| Login (Password) | :unlock: | **Player123** logged in successfully (Password)! |
| Login (PIN) | :unlock: | **Player123** logged in successfully (PIN)! |
| PIN Verify | :unlock: | **Player123** verified PIN successfully! |
| Premium Login | :star: | **Player123** logged in (Premium Account) |
| Failed Login | :x: | **Player123** failed login attempt (1/3) |
| Kicked | :warning: | **Player123** was kicked for too many failed login attempts! |

---

## Troubleshooting

### Discord not working?
1. Check if DiscordSRV is installed
2. Verify channel names match (no `#` symbol in config!)
3. Leave channel empty `""` to use default console channel

### Reload command not working?
1. Restart server once to register command
2. Make sure you're an operator or have `mbth.admin` permission

### Server name not changing?
1. Save config.yml
2. Run `/mbthlsreload`
3. Check YAML syntax

---

**That's it! Enjoy the new features!** 🎊

