# 🎉 New Features - MBTH Login Security v1.0.0

## ✨ Three Major New Features

### 1. 🏷️ Customizable Server Branding

**What is it?**
You can now customize the server name that appears in all plugin messages!

**Configuration:**
```yaml
# config.yml
server-name: "MBTH"
```

**Examples:**
- Change `"MBTH"` to `"YourServer"`
- Change to `"MyNetwork"`
- Change to anything you want!

**Before:**
```
Welcome to MBTH! You have been automatically authenticated.
Welcome back to MBTH!
```

**After (with `server-name: "AwesomeNetwork"`):**
```
Welcome to AwesomeNetwork! You have been automatically authenticated.
Welcome back to AwesomeNetwork!
```

**Where it appears:**
- Premium player welcome message
- Login success messages
- Registration success messages
- PIN verification messages
- All welcome/authentication messages

---

### 2. 🎮 Discord Integration (DiscordSRV)

**What is it?**
Full integration with DiscordSRV to send login/registration notifications to Discord channels!

**Requirements:**
- **DiscordSRV plugin installed** on your server
- Discord bot properly configured

**Configuration:**
```yaml
# config.yml
discord:
  # Enable DiscordSRV integration for login/registration logs
  enabled: false
  
  # Discord channel name for login notifications
  # Leave empty to use the default console channel
  # Example: "login-logs" or "security-logs"
  login-channel: ""
  
  # Discord channel name for registration notifications
  registration-channel: ""
  
  # Log premium player auto-login to Discord
  log-premium-login: true
  
  # Log failed login attempts to Discord
  log-failed-attempts: true
```

**Setup Instructions:**

1. **Install DiscordSRV**
   - Download from: https://www.spigotmc.org/resources/discordsrv.18494/
   - Place in your `plugins/` folder
   - Configure the Discord bot

2. **Enable Integration**
   ```yaml
   discord:
     enabled: true
   ```

3. **Create Discord Channels**
   - Create channels in your Discord server (e.g., `#login-logs`, `#security-logs`)
   - Make sure DiscordSRV knows about these channels

4. **Set Channel Names**
   ```yaml
   discord:
     enabled: true
     login-channel: "login-logs"
     registration-channel: "security-logs"
   ```

5. **Customize Options**
   ```yaml
   discord:
     log-premium-login: true      # Log premium players
     log-failed-attempts: true    # Log failed login attempts
   ```

**Discord Notifications:**

| Event | Icon | Message | Channel |
|-------|------|---------|---------|
| **Registration** | :new: | `**PlayerName** registered a new account!` | registration-channel |
| **Password Login** | :unlock: | `**PlayerName** logged in successfully (Password)!` | login-channel |
| **PIN Login** | :unlock: | `**PlayerName** logged in successfully (PIN)!` | login-channel |
| **PIN Verify** | :unlock: | `**PlayerName** verified PIN successfully!` | login-channel |
| **Premium Login** | :star: | `**PlayerName** logged in (Premium Account)` | login-channel |
| **Failed Login** | :x: | `**PlayerName** failed login attempt (1/3)` | login-channel |
| **Too Many Attempts** | :warning: | `**PlayerName** was kicked for too many failed login attempts!` | login-channel |

**Channel Options:**

- **Specific Channel:** Set a channel name like `"login-logs"`
- **Default Channel:** Leave empty `""` to use DiscordSRV's console channel
- **Same Channel:** Use the same channel name for both login and registration
- **Different Channels:** Use different channel names to separate logs

**Example Configuration:**
```yaml
discord:
  enabled: true
  login-channel: "login-logs"        # All login events go here
  registration-channel: "new-users"  # New registrations go here
  log-premium-login: true
  log-failed-attempts: true
```

---

### 3. 🔄 Configuration Reload Command

**What is it?**
Reload the plugin's configuration without restarting the server!

**Command:**
```
/mbthlsreload
```

**Aliases:**
- `/mbthlsr`
- `/reloadmbthls`

**Permission:**
- `mbth.admin`
- Or be an operator

**Usage:**

1. Edit `config.yml` while the server is running
2. Save your changes
3. Run `/mbthlsreload`
4. All settings are instantly updated!

**What gets reloaded:**
- ✅ Server name
- ✅ Max login attempts
- ✅ Login timeout
- ✅ Session settings
- ✅ Alt detection settings
- ✅ Premium bypass settings
- ✅ Login method choice settings
- ✅ PIN code settings
- ✅ Discord integration settings
- ✅ All configuration values

**Output Example:**
```
✔ Configuration Reloaded!
All settings have been updated from config.yml
Server Name: AwesomeNetwork
Discord Integration: Enabled
Premium Bypass: Enabled
Login Method Choice: Enabled
```

**What does NOT get reloaded:**
- ❌ Player data (already registered/logged in players)
- ❌ Active sessions
- ❌ Current authentication states
- ❌ Player data file (players.yml)

**Console Log:**
```
[MBTHLoginSecurity] Configuration reloaded by PlayerName
```

---

## 🚀 Quick Start Guide

### Example 1: Custom Branding Only

```yaml
# config.yml
server-name: "MyAwesomeServer"
discord:
  enabled: false
```

Run `/mbthlsreload` → All messages now say "MyAwesomeServer"!

### Example 2: Discord Integration

```yaml
# config.yml
server-name: "MBTH"
discord:
  enabled: true
  login-channel: "login-logs"
  registration-channel: "security"
  log-premium-login: true
  log-failed-attempts: true
```

Run `/mbthlsreload` → Discord notifications are now active!

### Example 3: Full Setup

```yaml
# config.yml
server-name: "AwesomeNetwork"
discord:
  enabled: true
  login-channel: "player-auth"
  registration-channel: "player-auth"
  log-premium-login: true
  log-failed-attempts: true
```

Run `/mbthlsreload` → Custom branding + Discord in one channel!

---

## 🔧 Troubleshooting

### Discord Integration Not Working

**Problem:** Discord messages not appearing

**Solutions:**
1. Check if DiscordSRV is installed and working
   - Run `/discordsrv reload`
   - Check console for DiscordSRV errors

2. Verify channel names match exactly
   - Discord channel: `#login-logs`
   - Config: `login-channel: "login-logs"`
   - No `#` symbol in config!

3. Check DiscordSRV channel linking
   - Ensure DiscordSRV knows about your custom channels
   - Check DiscordSRV's config.yml

4. Check console for warnings
   - Look for: `Discord channel 'xxx' not found!`
   - Create the channel or fix the name

5. Leave channel empty to use default
   ```yaml
   login-channel: ""  # Uses DiscordSRV console channel
   ```

### Reload Command Not Working

**Problem:** `/mbthlsreload` says "Unknown command"

**Solution:** 
- Restart the server once to register the command
- Commands are only registered on plugin enable

**Problem:** "You don't have permission"

**Solution:**
- Make yourself an operator: `/op YourName`
- Or give permission: `mbth.admin`

### Server Name Not Changing

**Problem:** Messages still show old name

**Solution:**
- Save config.yml after editing
- Run `/mbthlsreload`
- Check for typos in config
- Verify YAML syntax is correct

---

## 📋 Configuration Examples

### Minimal Setup
```yaml
server-name: "MyServer"
discord:
  enabled: false
```

### Discord Only (Default Console Channel)
```yaml
server-name: "MBTH"
discord:
  enabled: true
  login-channel: ""
  registration-channel: ""
```

### Full Security Logging
```yaml
server-name: "SecureServer"
discord:
  enabled: true
  login-channel: "security-logs"
  registration-channel: "security-logs"
  log-premium-login: true
  log-failed-attempts: true
```

### Separate Channels
```yaml
server-name: "NetworkName"
discord:
  enabled: true
  login-channel: "login-events"
  registration-channel: "new-players"
  log-premium-login: false
  log-failed-attempts: true
```

---

## 🎯 Benefits

### Customizable Branding
- ✅ Professional appearance
- ✅ Match your server's identity
- ✅ Consistent messaging
- ✅ Easy to change anytime

### Discord Integration
- ✅ Real-time security monitoring
- ✅ Track player authentications
- ✅ Detect suspicious activity
- ✅ Log all security events
- ✅ Keep staff informed
- ✅ Historical records in Discord

### Reload Command
- ✅ No server restart needed
- ✅ Instant configuration updates
- ✅ Test settings quickly
- ✅ Zero downtime
- ✅ Live configuration changes

---

## 💡 Tips & Best Practices

1. **Server Branding**
   - Use your actual server/network name
   - Keep it short and recognizable
   - Avoid special characters that might cause issues

2. **Discord Channels**
   - Create dedicated channels for security logs
   - Use different channels for different event types
   - Set appropriate Discord permissions
   - Pin important security notifications

3. **Failed Attempts Logging**
   - Enable `log-failed-attempts: true` for security
   - Monitor for brute-force attempts
   - Set up Discord alerts/notifications
   - Review logs regularly

4. **Reload Command**
   - Use it for testing configurations
   - No need to restart for small changes
   - Check console for any errors after reload
   - Document your configuration changes

---

## 🆘 Support

If you need help:
1. Check console for error messages
2. Verify YAML syntax in config.yml
3. Ensure DiscordSRV is properly configured
4. Check file permissions
5. Test with minimal configuration first

---

**Enjoy the new features!** 🎊

