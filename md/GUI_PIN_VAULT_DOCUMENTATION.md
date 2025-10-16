# GUI PIN Vault & Premium Player System Documentation

## 🎮 Overview

The MBTH Login Security plugin now features an advanced **GUI-based PIN Vault** system with a clickable number pad interface, along with **automatic premium/cracked player detection**. This provides a modern, user-friendly authentication experience while ensuring only cracked players need to authenticate.

---

## 🆕 New Features

### 1. **Premium/Cracked Player Detection**
- ✅ Automatically detects premium (paid Minecraft) players
- ✅ Premium players bypass all authentication requirements
- ✅ Only cracked (non-premium) players must login/register
- ✅ Configurable bypass system

### 2. **GUI PIN Vault Interface**
- ✅ Beautiful inventory-based PIN entry system
- ✅ Clickable number pad (0-9)
- ✅ Visual feedback with filled/empty circles (● ○)
- ✅ Clear and Submit buttons
- ✅ Sound effects for button clicks
- ✅ Cannot be closed until PIN is entered

### 3. **Login Method Selection**
- ✅ Players can choose between PIN or Password login
- ✅ Interactive GUI menu for selection
- ✅ Flexible authentication options
- ✅ Configurable feature

---

## 🎯 How It Works

### For Premium Players (Paid Minecraft)

```
Premium Player Joins Server
        ↓
✓ Automatically Detected
        ↓
✓ Bypass All Authentication
        ↓
✓ Instant Access to Server
```

**No login required!** Premium players are automatically authenticated.

### For Cracked Players (Non-Premium)

#### First Time (Registration)
```
Cracked Player Joins Server
        ↓
Prompted to Register
        ↓
/register <password> <password>
        ↓
Account Created
        ↓
Full Access
```

#### Returning Player (With PIN Setup)
```
Cracked Player Joins Server
        ↓
Choose Login Method GUI Opens
        ↓
┌─────────────────┬─────────────────┐
│  Login with PIN │ Login with Pass │
└─────────────────┴─────────────────┘
        ↓                    ↓
    PIN VAULT           /login command
        ↓                    ↓
  Click Numbers         Enter Password
        ↓                    ↓
    Submit PIN           Authenticated
        ↓
  Authenticated
```

---

## 🔐 GUI PIN Vault Interface

### Visual Layout

```
┌─────────────────────────────────────┐
│         ✦ Enter PIN Vault ✦         │
├─────────────────────────────────────┤
│                                     │
│         PIN: ● ● ○ ○                │
│         Entered: 2/4 digits         │
│                                     │
├─────────────────────────────────────┤
│                                     │
│         [1]  [2]  [3]               │
│                                     │
│         [4]  [5]  [6]               │
│                                     │
│         [7]  [8]  [9]               │
│                                     │
│              [0]                    │
│                                     │
│    [CLEAR]        [SUBMIT]          │
│                                     │
└─────────────────────────────────────┘
```

### Features

1. **Number Buttons (0-9)**
   - Green glass panes with numbers
   - Click to enter digit
   - Sound feedback on click
   - Disabled when PIN is full

2. **PIN Display**
   - Shows filled circles (●) for entered digits
   - Shows empty circles (○) for remaining digits
   - Real-time update as you type

3. **Clear Button**
   - Orange glass pane
   - Clears all entered digits
   - Allows you to start over

4. **Submit Button**
   - Green emerald
   - Submits your PIN for verification
   - Only works when all digits entered

5. **Auto-Reopen**
   - Vault reopens if closed without completing
   - Ensures authentication is completed
   - Cannot bypass by closing inventory

---

## ⚙️ Configuration

### config.yml Settings

```yaml
# Premium/Cracked Player Detection
premium-bypass:
  # Enable automatic bypass for premium (paid Minecraft) players
  # Premium players will NOT need to login/register
  # Only cracked (non-premium) players will be required to authenticate
  enabled: true

# Login Method Choice
login-method-choice:
  # Allow players to choose between PIN or Password login
  # Players with both PIN and Password can select their preferred method
  enabled: true

# PIN Code System
pin-code:
  enabled: true
  required: false
  length: 4
  max-attempts: 3
```

### Configuration Options Explained

#### Premium Bypass
- **`enabled: true`** - Premium players auto-authenticated (recommended)
- **`enabled: false`** - All players must authenticate (cracked + premium)

#### Login Method Choice
- **`enabled: true`** - Players choose PIN or Password (recommended)
- **`enabled: false`** - Players use traditional /login command only

#### PIN Code
- **`enabled: true`** - GUI PIN vault available
- **`required: true`** - All cracked players must set up PIN
- **`length: 4`** - Number of digits (4-6 recommended)
- **`max-attempts: 3`** - Failed attempts before kick

---

## 🎮 Player Experience

### Scenario 1: Premium Player Joins

```
[Player joins with premium Minecraft account]

Server: ✔ Premium Account Detected!
Server: Welcome to MBTH! You have been automatically authenticated.

[Player has immediate full access]
```

### Scenario 2: Cracked Player First Time

```
[Player joins with cracked Minecraft]

Server: Please register with /register <password> <password>

Player: /register MyPass123 MyPass123

Server: ✔ Successfully registered!
Server: Your account is now secured.

[Player has full access]
```

### Scenario 3: Cracked Player Returns (With PIN)

```
[Player joins with cracked Minecraft]

[GUI opens: "Choose Login Method"]
- Option 1: Login with PIN (Iron Door icon)
- Option 2: Login with Password (Paper icon)

[Player clicks "Login with PIN"]

[PIN Vault GUI opens]
Player clicks: [1] [2] [3] [4]
Display shows: ● ● ● ●

[Player clicks SUBMIT]

Server: ✔ PIN verified successfully!
Server: Welcome back to MBTH!

[Player has full access]
```

### Scenario 4: Wrong PIN Entered

```
[Player enters wrong PIN in vault]

Server: ✘ Incorrect PIN!
Server: Attempts remaining: 2

[PIN Vault reopens automatically]

[Player can try again]
```

---

## 🔧 Technical Details

### Premium Detection Logic

The plugin detects premium players by checking:
1. **Server Mode**: Is server in online mode?
   - Online mode = All players are premium
   - Offline mode = All players are cracked
2. **Player Authentication**: Has player been authenticated by Mojang?

### GUI Implementation

- **Inventory Type**: 54-slot chest inventory
- **Number Layout**: 3x3 keypad + 0 at bottom
- **Materials Used**:
  - Lime Glass Pane: Number buttons
  - Orange Glass Pane: Clear button
  - Emerald: Submit button
  - Paper: PIN display
  - Gray Glass Pane: Decorative filler

### Security Features

1. **Cannot Close Vault**: Automatically reopens if player tries to close
2. **Failed Attempt Tracking**: Tracks wrong PIN entries per session
3. **Auto-Kick**: Kicks player after max failed attempts
4. **Sound Feedback**: Plays sounds for clicks and errors
5. **Visual Feedback**: Real-time PIN display updates

---

## 📋 Commands

All existing commands still work:

### Player Commands
- `/register <password> <password>` - Register account (cracked only)
- `/login <password>` - Login with password (cracked only)
- `/setuppin <pin> <confirm>` - Set up PIN for GUI vault
- `/changepin <old> <new> <confirm>` - Change PIN
- `/removepin <pin>` - Remove PIN
- `/changepassword <old> <new> <confirm>` - Change password

### Admin Commands
- `/resetpin <player>` - Reset player's PIN
- `/resetpassword <player> <new>` - Reset player's password
- `/freezeaccount <player>` - Freeze account
- `/unfreezeaccount <player>` - Unfreeze account
- `/forcelogout <player>` - Force logout
- `/checkalt <player>` - Check for alt accounts

---

## 🎨 Customization

### Change PIN Vault Colors

Edit the `openPinVault()` method to change materials:

```java
// Number buttons
Material.LIME_STAINED_GLASS_PANE  // Green numbers

// Clear button
Material.ORANGE_STAINED_GLASS_PANE  // Orange clear

// Submit button
Material.EMERALD  // Green submit

// Background
Material.GRAY_STAINED_GLASS_PANE  // Gray filler
```

### Change Keypad Layout

Edit the `getKeypadSlot()` method to reposition numbers:

```java
case 1: return 20;  // Slot position for number 1
case 2: return 22;  // Slot position for number 2
// etc...
```

---

## 🐛 Troubleshooting

### Premium Players Still Asked to Login

**Problem**: Premium players are being asked to authenticate

**Solutions**:
1. Check `config.yml`: Ensure `premium-bypass.enabled: true`
2. Check server mode: Server must be in online mode for premium detection
3. Reload plugin: `/reload` or restart server

### PIN Vault Not Opening

**Problem**: GUI doesn't open when selecting PIN login

**Solutions**:
1. Check `config.yml`: Ensure `pin-code.enabled: true`
2. Ensure player has PIN set up: Use `/setuppin` first
3. Check for inventory conflicts: Close any open inventories

### Vault Closes Immediately

**Problem**: PIN vault closes right after opening

**Solutions**:
1. This is normal if player is already authenticated
2. Check if premium bypass is active
3. Verify player is not already logged in

### Numbers Don't Work

**Problem**: Clicking numbers doesn't enter digits

**Solutions**:
1. Ensure clicking on the colored glass pane, not empty slots
2. Check if PIN is already full (max digits entered)
3. Try clicking CLEAR and starting over

---

## 📊 Comparison: Old vs New System

| Feature | Old System | New System |
|---------|-----------|------------|
| **PIN Entry** | Command `/verifypin 1234` | GUI with clickable numbers |
| **Visual Feedback** | Text messages only | Real-time display with ● ○ |
| **Premium Players** | Must login | Auto-bypass |
| **Login Choice** | Command only | GUI menu selection |
| **User Experience** | Text-based | Interactive GUI |
| **Security** | Command visible in chat | Hidden in GUI |
| **Accessibility** | Requires typing | Click-based |

---

## 🎯 Best Practices

### For Server Admins

1. **Enable Premium Bypass**: Reduces friction for legitimate players
2. **Require PINs**: Set `pin-code.required: true` for high security
3. **Use 4-6 Digit PINs**: Balance security and usability
4. **Enable Login Choice**: Let players choose their preferred method
5. **Monitor Failed Attempts**: Check logs for suspicious activity

### For Players

1. **Use Different PIN**: Don't use same as password
2. **Avoid Obvious PINs**: No 1234, 0000, 1111, etc.
3. **Try PIN Login**: Faster than typing password
4. **Remember Your PIN**: Only 3 attempts before kick
5. **Use CLEAR Button**: Start over if you make a mistake

---

## 🚀 Quick Start Guide

### For New Servers

1. Install plugin in `plugins/` folder
2. Start server (generates config.yml)
3. Configure settings:
   ```yaml
   premium-bypass:
     enabled: true
   login-method-choice:
     enabled: true
   pin-code:
     enabled: true
     required: false
   ```
4. Restart server
5. Test with cracked and premium accounts

### For Existing Servers

1. Update plugin JAR file
2. Backup `config.yml`
3. Restart server (new options added automatically)
4. Notify players about new GUI PIN system
5. Players can set up PINs with `/setuppin`

---

## 📞 Support & FAQ

### Q: Do premium players need to register?
**A:** No! Premium players are automatically authenticated.

### Q: Can I disable the GUI and use commands only?
**A:** Yes, set `login-method-choice.enabled: false`

### Q: How do I force everyone to use PINs?
**A:** Set `pin-code.required: true` in config

### Q: Can players use both PIN and Password?
**A:** Yes! They can choose their preferred method each login

### Q: What if I forget my PIN?
**A:** Ask an admin to use `/resetpin <yourname>`

### Q: Is the PIN visible in chat?
**A:** No! GUI entry keeps your PIN private

### Q: Can I change the number of digits?
**A:** Yes, edit `pin-code.length` in config (1-10)

### Q: Does this work on Bedrock Edition?
**A:** No, this is for Java Edition servers only

---

## 📝 Version History

**v1.0.0** - Initial GUI PIN Vault Release
- Added GUI-based PIN vault with clickable numbers
- Implemented premium/cracked player detection
- Added login method selection GUI
- Enhanced security with visual feedback
- Improved user experience with sounds and animations

---

**Documentation Version:** 1.0  
**Plugin Version:** 1.0.0  
**Minecraft Version:** 1.21  
**Last Updated:** October 16, 2025

