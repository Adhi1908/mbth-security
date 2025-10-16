# 📖 Configuration Guide - MBTH Login Security

## Complete config.yml Explanation

### 🏷️ Server Branding

```yaml
# Server/Network Name (Customizable branding)
server-name: "MBTH"
```

**What it does:** Customizes the server name shown in all plugin messages.

**Examples:**
- `"MBTH"` (default)
- `"AwesomeNetwork"`
- `"MySurvivalServer"`
- `"The Gaming Hub"`

**Where it appears:**
- Welcome messages
- Login success messages
- Registration messages
- PIN verification messages

---

### 🔒 Security Settings

```yaml
# Maximum number of failed login attempts before kick
max-login-attempts: 3

# Time in seconds before player is kicked for not logging in
login-timeout-seconds: 60
```

**max-login-attempts:**
- Default: `3`
- Range: `1-10` recommended
- Higher = more forgiving, lower = more secure

**login-timeout-seconds:**
- Default: `60` (1 minute)
- Range: `30-300` recommended
- Time before AFK players are kicked

---

### 🕐 Session Management

```yaml
session:
  # Enable session persistence (players stay logged in after rejoining)
  enabled: true
  
  # Session duration in minutes
  duration-minutes: 30
```

**enabled:**
- `true` = Players stay logged in when rejoining
- `false` = Players must login every time

**duration-minutes:**
- Default: `30` minutes
- After this time, players must login again
- Recommended: `15-60` minutes

---

### 🔍 Alt Account Detection

```yaml
alt-detection:
  # Enable automatic alt account detection via IP tracking
  enabled: true
  
  # Notify admins when alt accounts are detected
  notify-admins: true
```

**enabled:**
- `true` = Track IPs and detect multiple accounts
- `false` = Disable alt detection

**notify-admins:**
- `true` = Alert admins when alts are detected
- `false` = Silent detection (check with `/checkalt`)

---

### ⭐ Premium/Cracked Player Detection

```yaml
premium-bypass:
  # Enable automatic bypass for premium (paid Minecraft) players
  # Premium players will NOT need to login/register
  # Only cracked (non-premium) players will be required to authenticate
  enabled: true
```

**What it does:**
- Automatically detects premium (paid Minecraft) players
- Premium players bypass all authentication
- Only cracked/non-premium players need to login

**Recommendations:**
- `true` for cracked servers
- `false` if you want everyone to authenticate

---

### 🎮 Login Method Choice

```yaml
login-method-choice:
  # Allow players to choose between PIN or Password login
  # Players with both PIN and Password can select their preferred method
  enabled: true
```

**enabled:**
- `true` = Show GUI menu with PIN/Password choice
- `false` = Direct login prompts only

---

### 🎮 Discord Integration

```yaml
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

**enabled:**
- `true` = Enable DiscordSRV integration
- `false` = Disable Discord notifications
- **Requires DiscordSRV plugin installed!**

**login-channel:**
- Channel name for login events
- Example: `"login-logs"`
- Leave empty `""` for default console channel
- **No `#` symbol!**

**registration-channel:**
- Channel name for new registrations
- Can be same as login-channel
- Leave empty `""` for default console channel

**log-premium-login:**
- `true` = Log premium player logins to Discord
- `false` = Don't log premium logins

**log-failed-attempts:**
- `true` = Log failed login attempts (security!)
- `false` = Only log successful logins

**Example Setups:**

**Same Channel for Everything:**
```yaml
discord:
  enabled: true
  login-channel: "player-auth"
  registration-channel: "player-auth"
```

**Separate Channels:**
```yaml
discord:
  enabled: true
  login-channel: "logins"
  registration-channel: "new-players"
```

**Default Console Channel:**
```yaml
discord:
  enabled: true
  login-channel: ""
  registration-channel: ""
```

---

### 🔢 PIN Code System

```yaml
pin-code:
  # Enable PIN code system for extra security
  enabled: true
  
  # Require all players to set up a PIN
  required: false
  
  # PIN length (number of digits)
  length: 4
  
  # Maximum PIN verification attempts before kick
  max-attempts: 3
```

**enabled:**
- `true` = Players can use PIN for login
- `false` = Disable PIN system

**required:**
- `true` = All players MUST setup a PIN
- `false` = PIN is optional

**length:**
- Default: `4` digits
- Range: `4-8` recommended
- Longer = more secure

**max-attempts:**
- Default: `3`
- Failed attempts before kick
- Range: `3-5` recommended

---

### 💬 Messages (Customizable)

```yaml
messages:
  prefix: "&6[&eMBTH&6] &7"
  
  login-prompt: "&eWelcome back! Please login with &a/login <password>"
  register-prompt: "&eWelcome! Please register with &a/register <password> <confirm>"
  
  login-success: "&aSuccessfully logged in!"
  register-success: "&aAccount registered successfully!"
  
  already-authenticated: "&cYou are already authenticated!"
  incorrect-password: "&cIncorrect password!"
  too-many-attempts: "&cToo many failed attempts!"
  
  pin-prompt: "&eEnter your PIN with &a/verifypin <pin>"
  pin-setup-prompt: "&eSetup your PIN with &a/setuppin <pin> <confirm>"
  pin-verified: "&aPin verified successfully!"
  incorrect-pin: "&cIncorrect PIN!"
```

**Color Codes:**
- `&0` - Black
- `&1` - Dark Blue
- `&2` - Dark Green
- `&3` - Dark Aqua
- `&4` - Dark Red
- `&5` - Dark Purple
- `&6` - Gold
- `&7` - Gray
- `&8` - Dark Gray
- `&9` - Blue
- `&a` - Green
- `&b` - Aqua
- `&c` - Red
- `&d` - Light Purple
- `&e` - Yellow
- `&f` - White

**Format Codes:**
- `&l` - Bold
- `&m` - Strikethrough
- `&n` - Underline
- `&o` - Italic
- `&r` - Reset

---

## 📋 Complete Example Configuration

### Recommended for Cracked Servers

```yaml
# Server Branding
server-name: "AwesomeServer"

# Security
max-login-attempts: 3
login-timeout-seconds: 60

# Sessions
session:
  enabled: true
  duration-minutes: 30

# Alt Detection
alt-detection:
  enabled: true
  notify-admins: true

# Premium Bypass (Important for cracked servers!)
premium-bypass:
  enabled: true

# Login Method Choice
login-method-choice:
  enabled: true

# Discord Integration
discord:
  enabled: true
  login-channel: "player-auth"
  registration-channel: "player-auth"
  log-premium-login: true
  log-failed-attempts: true

# PIN System
pin-code:
  enabled: true
  required: false
  length: 4
  max-attempts: 3
```

### Recommended for Premium Servers

```yaml
# Server Branding
server-name: "PremiumServer"

# Security
max-login-attempts: 5
login-timeout-seconds: 90

# Sessions
session:
  enabled: true
  duration-minutes: 60

# Alt Detection
alt-detection:
  enabled: true
  notify-admins: true

# Premium Bypass (Disable for premium-only servers)
premium-bypass:
  enabled: false

# Login Method Choice
login-method-choice:
  enabled: true

# Discord Integration
discord:
  enabled: true
  login-channel: "security-logs"
  registration-channel: "new-members"
  log-premium-login: false
  log-failed-attempts: true

# PIN System
pin-code:
  enabled: true
  required: false
  length: 6
  max-attempts: 5
```

---

## 🔧 How to Reload Configuration

After editing `config.yml`:

1. Save the file
2. Run command: `/mbthlsreload`
3. Configuration is instantly updated!

**No server restart needed!** ✨

---

## 💡 Tips

1. **Start with defaults** and adjust as needed
2. **Test changes** with `/mbthlsreload`
3. **Monitor Discord logs** for security events
4. **Adjust timeouts** based on your player base
5. **Enable alt-detection** for security
6. **Use Discord** for real-time monitoring
7. **Customize messages** to match your server style

---

## 🆘 Common Issues

### YAML Syntax Errors
- Use spaces, not tabs
- Proper indentation is critical
- Check for typos

### Discord Not Working
- Install DiscordSRV first
- Check channel names (no `#`)
- Leave empty for default channel

### Changes Not Applying
- Save config.yml
- Run `/mbthlsreload`
- Check console for errors

---

**Happy configuring!** 🎉

