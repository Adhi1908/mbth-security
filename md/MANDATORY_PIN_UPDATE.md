# 🔒 Mandatory PIN System Update

## ✨ What's New

### 1. **Mandatory PIN Setup After Registration**
Every player MUST setup a PIN after creating their password!

**Flow:**
1. Player joins server
2. Player runs `/register password password`
3. **NEW:** PIN Vault automatically opens
4. Player must enter a 4-digit PIN
5. Player can now play!

### 2. **Skull/Head-Based GUI**
The PIN vault now uses player heads instead of glass panes and emeralds!

**Before:**
- Numbers: Green glass panes
- Clear: Orange glass pane
- Submit: Emerald

**After:**
- Numbers: Player heads (0-9)
- Clear: Player head with yellow text
- Submit: Player head with green text

---

## 🎮 How It Works

### Registration Process

**Step 1: Register**
```
/register mypassword mypassword
```

**Step 2: Automatic PIN Vault Opens**
- PIN vault opens automatically after 1 second
- Player sees: "⚠ PIN REQUIRED"
- Message: "You must setup a PIN to secure your account."
- Message: "Opening PIN Vault..."

**Step 3: Enter PIN**
- Click number heads (0-9) to enter PIN
- Click yellow CLEAR head to reset
- Click green SUBMIT head when done

**Step 4: Complete!**
- Message: "✔ PIN Setup Complete!"
- Message: "Your PIN has been saved successfully."
- Player can now move and play!

---

## 🔧 Configuration

```yaml
# config.yml
pin-code:
  enabled: true
  required: true  # ⚠️ Changed to true!
  length: 4
  max-attempts: 3
```

**required: true** = Mandatory PIN setup after registration

---

## 📦 GUI Details

### PIN Vault Layout

```
+------------------------------------------+
|            [PIN DISPLAY]                 |
|                                          |
|         [1]  [2]  [3]                    |
|         [4]  [5]  [6]                    |
|         [7]  [8]  [9]                    |
|         [CLEAR] [0] [SUBMIT]             |
+------------------------------------------+
```

All items are now **Player Heads**:
- **Number Heads (0-9)**: Green text with lore
- **CLEAR Head**: Yellow text with red lore
- **SUBMIT Head**: Green text with green lore

---

## 🔄 Player Experience

### New Player:
1. ✅ `/register pass pass`
2. ✅ PIN Vault opens automatically
3. ✅ Enter 4-digit PIN (e.g., 1234)
4. ✅ Click SUBMIT
5. ✅ Can move and play!

### Returning Player:
1. ✅ Login with password OR PIN
2. ✅ Start playing immediately

---

## 🎨 GUI Features

### Number Heads
- **Display Name**: Green bold number (0-9)
- **Lore**: 
  - "Click to enter [number]"
  - ""
  - "Number: [number]"

### CLEAR Head
- **Display Name**: Yellow bold "CLEAR"
- **Lore**:
  - "Clear entered PIN"
  - ""
  - "Reset your input"

### SUBMIT Head
- **Display Name**: Green bold "SUBMIT"
- **Lore**:
  - "Submit your PIN"
  - ""
  - "Confirm your input"

---

## 🔐 Security Flow

### Setup Mode (After Registration):
1. Player frozen (cannot move)
2. PIN vault opens
3. Player enters PIN
4. PIN saved to database (hashed)
5. Player unfrozen
6. Success messages

### Login Mode (Returning):
1. Player enters PIN
2. PIN compared with stored hash
3. If correct: authenticated
4. If wrong: tries again (3 max)
5. Too many failures: kicked

---

## 🎯 Benefits

### For Server Owners:
- ✅ **Mandatory security** for all players
- ✅ **Prevents account theft**
- ✅ **Professional GUI** with heads
- ✅ **Automatic enforcement**

### For Players:
- ✅ **Easy to use** visual interface
- ✅ **Fast PIN entry** with heads
- ✅ **One-time setup** per account
- ✅ **Quick login** with PIN

---

## 📊 Technical Details

### Setup Mode Detection:
```java
pinSetupMode.put(uuid, true);  // Track setup vs login
```

### PIN Storage:
```java
dataConfig.set("players." + uuid + ".pin", hashedPin);
dataConfig.set("players." + uuid + ".pin-setup-date", timestamp);
```

### GUI Material:
```java
Material.PLAYER_HEAD  // All buttons now use heads
```

### Click Detection:
```java
if (displayName.matches("[0-9]")) {
    // Number clicked
} else if (displayName.equals("CLEAR")) {
    // Clear clicked
} else if (displayName.equals("SUBMIT")) {
    // Submit clicked
}
```

---

## 🆕 Discord Notifications

New event added:
```
🛡️ **PlayerName** completed PIN setup!
```

Logged to `registration-channel` in Discord.

---

## 🐛 Error Handling

### Player Closes Vault:
- Vault reopens automatically
- Player cannot escape setup
- Must complete PIN to play

### Wrong PIN Length:
- Shows error: "Please enter all 4 digits!"
- Plays error sound
- Vault stays open

### Server Restart During Setup:
- Player must login again
- PIN setup required again
- Previous attempt discarded

---

## 📝 Messages

All messages are customizable in `config.yml`:

```yaml
messages:
  welcome-premium: "&7Welcome to {server}! You have been automatically authenticated."
  welcome-back: "&7Welcome back to {server}!"
  welcome-registered: "&7Your account is now secured."
  login-success: "&a&l✔ Successfully logged in!"
  register-success: "&a&l✔ Successfully registered!"
  premium-detected: "&a&l✔ Premium Account Detected!"
  pin-verified: "&a&l✔ PIN verified successfully!"
  reload-success: "&a&l✔ Configuration Reloaded!"
  reload-details: "&7All settings have been updated from config.yml"
```

Use `{server}` placeholder to insert server name!

---

## 🚀 Quick Start

### Deploy Update:
1. Replace plugin JAR: `target/MBTHLoginSecurity-1.0.0.jar`
2. Restart server
3. Check `config.yml` → `pin-code.required: true`
4. Test with new account!

### Test Process:
1. Join server
2. `/register test test`
3. PIN vault should open
4. Enter 1234
5. Click SUBMIT
6. Should be able to move!

---

## ✅ Complete Feature List

### Implementation Complete:
1. ✅ Mandatory PIN after registration
2. ✅ Automatic PIN vault opening
3. ✅ Player head-based GUI
4. ✅ Number heads (0-9)
5. ✅ CLEAR head (yellow)
6. ✅ SUBMIT head (green)
7. ✅ Setup mode tracking
8. ✅ PIN saving on first entry
9. ✅ Player frozen until PIN setup
10. ✅ Automatic unfreezing after setup
11. ✅ Discord notification
12. ✅ Error handling
13. ✅ Customizable messages
14. ✅ Config reload support

---

**All features tested and working!** 🎉

Ready to deploy! 🚀

