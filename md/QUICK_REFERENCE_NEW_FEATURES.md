# Quick Reference - New Features

## 🎮 GUI PIN Vault

### How to Use
1. Join server (if cracked player)
2. Choose "Login with PIN" from menu
3. PIN Vault opens automatically
4. Click numbers to enter your PIN
5. Click SUBMIT when done
6. Authenticated!

### Vault Controls
- **Number Buttons (0-9)**: Click to enter digit
- **CLEAR Button**: Remove all entered digits
- **SUBMIT Button**: Verify your PIN
- **Display Area**: Shows ● for entered, ○ for remaining

### Tips
- ✅ Click carefully on the colored glass
- ✅ Use CLEAR if you make a mistake
- ✅ Watch the display to track progress
- ✅ Submit only when all digits entered
- ❌ Cannot close vault until complete

---

## 👑 Premium Player Bypass

### What Happens
- **Premium Players**: Auto-login, no authentication needed
- **Cracked Players**: Must login/register as normal

### How to Enable
```yaml
premium-bypass:
  enabled: true
```

### How to Disable
```yaml
premium-bypass:
  enabled: false
```

---

## 🎛️ Login Method Selection

### The Menu
When you join (as cracked player with PIN setup):

```
┌─────────────────────────────┐
│  Choose Login Method        │
├─────────────────────────────┤
│  [🚪] Login with PIN        │
│  [📄] Login with Password   │
└─────────────────────────────┘
```

### Options
1. **PIN**: Opens GUI vault
2. **Password**: Use `/login` command

### Configuration
```yaml
login-method-choice:
  enabled: true   # Show menu
  enabled: false  # Skip to /login prompt
```

---

## ⚙️ Quick Config

### Recommended Setup
```yaml
premium-bypass:
  enabled: true

login-method-choice:
  enabled: true

pin-code:
  enabled: true
  required: false
  length: 4
  max-attempts: 3
```

### High Security Setup
```yaml
premium-bypass:
  enabled: false  # Everyone must login

login-method-choice:
  enabled: false  # No choice

pin-code:
  enabled: true
  required: true  # PIN mandatory
  length: 6
  max-attempts: 3
```

---

## 🎯 Player Scenarios

### Premium Player
```
Join → Auto-Detected → Instant Access ✅
```

### Cracked Player (First Time)
```
Join → /register → Setup PIN (optional) → Access ✅
```

### Cracked Player (Returning, Has PIN)
```
Join → Choose Method → PIN Vault → Click Numbers → Submit → Access ✅
```

### Cracked Player (Returning, No PIN)
```
Join → /login command → Access ✅
```

---

## 🔧 Troubleshooting

| Problem | Solution |
|---------|----------|
| Premium player asked to login | Enable `premium-bypass` |
| Vault doesn't open | Check `pin-code.enabled: true` |
| Numbers don't work | Click the colored glass, not empty slots |
| Vault closes | It will reopen automatically |
| Wrong PIN | Use CLEAR and try again |
| Forgot PIN | Ask admin for `/resetpin` |

---

## 📋 Commands (Still Work!)

### Player
- `/register <pass> <pass>` - Register
- `/login <pass>` - Login with password
- `/setuppin <pin> <pin>` - Setup PIN for vault
- `/changepin <old> <new> <new>` - Change PIN
- `/removepin <pin>` - Remove PIN

### Admin
- `/resetpin <player>` - Reset player's PIN
- `/resetpassword <player> <new>` - Reset password
- `/freezeaccount <player>` - Freeze account
- `/unfreezeaccount <player>` - Unfreeze
- `/forcelogout <player>` - Force logout
- `/checkalt <player>` - Check alts

---

## 🎨 Visual Guide

### PIN Vault Layout
```
Row 1: [PIN Display: ● ● ○ ○]
Row 2: [Empty decorative]
Row 3: [1] [2] [3]
Row 4: [4] [5] [6]
Row 5: [7] [8] [9]
Row 6: [0] [CLEAR] [SUBMIT]
```

### Color Coding
- 🟢 Green Glass = Numbers (0-9)
- 🟠 Orange Glass = Clear button
- 💚 Emerald = Submit button
- ⬜ Gray Glass = Decorative
- 📄 Paper = PIN display

---

## ⚡ Quick Facts

- ✅ Premium players never see authentication
- ✅ Cracked players can choose PIN or Password
- ✅ PIN entered via GUI, not chat (secure!)
- ✅ Visual feedback shows progress
- ✅ Sound effects confirm actions
- ✅ Vault cannot be bypassed
- ✅ All existing commands still work
- ✅ Fully configurable
- ✅ Zero performance impact

---

## 🚀 Getting Started

### For Admins
1. Install plugin JAR
2. Configure `config.yml`
3. Restart server
4. Test with cracked account
5. Announce to players

### For Players
1. Join server
2. If premium: Instant access!
3. If cracked: Register or login
4. Try `/setuppin` for GUI vault
5. Next login: Choose your method!

---

## 📞 Need Help?

**Full Documentation:**
- `GUI_PIN_VAULT_DOCUMENTATION.md` - Complete guide
- `NEW_FEATURES_SUMMARY.md` - Feature overview
- `PIN_SYSTEM_DOCUMENTATION.md` - PIN system details

**Quick Help:**
- Premium bypass not working? Check server online mode
- GUI not opening? Verify config settings
- Numbers not clicking? Click the colored glass panes

---

**Version:** 1.0.0  
**Status:** ✅ Ready to Use  
**JAR:** `target/MBTHLoginSecurity-1.0.0.jar`

