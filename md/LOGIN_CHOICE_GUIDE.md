# 🎮 Login Method Selection - Complete Guide

## ✨ How Players Choose PIN or Password

### 🎯 **EVERY TIME a cracked player joins the server, they see this menu:**

```
┌─────────────────────────────────────────────────┐
│         ✦ Choose Login Method ✦                 │
├─────────────────────────────────────────────────┤
│                                                 │
│   🚪 [Iron Door]                                │
│   Login with PIN                                │
│   Use your secure PIN code                      │
│   to login quickly                              │
│   ✔ Click to select                             │
│                                                 │
│   📄 [Paper]                                    │
│   Login with Password                           │
│   Use your account password                     │
│   to login                                      │
│   ✔ Click to select                             │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 📋 **When Does This Menu Appear?**

✅ **ALWAYS shows for cracked players who are registered**
- Doesn't matter if they have PIN or not
- Doesn't matter which method they used last time
- Shows EVERY login

❌ **Does NOT show for:**
- Premium players (auto-authenticated)
- Players who haven't registered yet
- When `login-method-choice: false` in config

---

## 🎮 **Player Experience - Step by Step**

### **Scenario 1: Player with PIN Setup**

```
1. Player "Steve" joins server (cracked)
   ↓
2. GUI opens automatically: "Choose Login Method"
   ↓
3. Steve sees TWO options:
   
   Left Side (Slot 11):
   🚪 Iron Door
   "Login with PIN"
   "Use your secure PIN code to login quickly"
   "✔ Click to select"
   
   Right Side (Slot 15):
   📄 Paper
   "Login with Password"
   "Use your account password to login"
   "✔ Click to select"
   ↓
4. Steve clicks the Iron Door (PIN option)
   ↓
5. PIN Vault opens with number pad
   ↓
6. Steve clicks: [1] [2] [3] [4]
   Display shows: ● ● ● ●
   ↓
7. Steve clicks SUBMIT
   ↓
8. ✅ Authenticated! Welcome to server!
```

### **Scenario 2: Player WITHOUT PIN Setup**

```
1. Player "Alex" joins server (cracked)
   ↓
2. GUI opens automatically: "Choose Login Method"
   ↓
3. Alex sees TWO options:
   
   Left Side (Slot 11):
   🚪 Iron Door
   "Setup PIN (Optional)"
   "Create a PIN for faster login next time"
   "Click to setup"
   "(You can skip this)"
   
   Right Side (Slot 15):
   📄 Paper
   "Login with Password"
   "Use your account password to login"
   "✔ Click to select"
   ↓
4a. If Alex clicks Iron Door (Setup PIN):
    ↓
    GUI closes
    ↓
    Chat shows:
    "✦ PIN SETUP"
    "You chose to setup a PIN for faster login."
    "Use: /setuppin <pin> <confirm>"
    "Example: /setuppin 1234 1234"
    "Or use /login <password> to login with password instead."
    ↓
    Alex can:
    - Type /setuppin 1234 1234 (sets up PIN for next time)
    - Type /login MyPassword (logs in now with password)
    
4b. If Alex clicks Paper (Password):
    ↓
    GUI closes
    ↓
    Chat shows:
    "Please login with /login <password>"
    ↓
    Alex types: /login MyPassword
    ↓
    ✅ Authenticated! Welcome to server!
```

### **Scenario 3: Premium Player**

```
1. Player "Mike" joins server (premium Minecraft)
   ↓
2. System detects: Premium player!
   ↓
3. NO GUI shown
   ↓
4. ✅ Instant authentication!
   ↓
5. Chat shows:
   "✔ Premium Account Detected!"
   "Welcome to MBTH! You have been automatically authenticated."
```

---

## 🎨 **Visual Layout in Minecraft**

The GUI is a **27-slot inventory** (3 rows × 9 columns):

```
Slot Layout:
┌───┬───┬───┬───┬───┬───┬───┬───┬───┐
│ 0 │ 1 │ 2 │ 3 │ 4 │ 5 │ 6 │ 7 │ 8 │  Row 1: Yellow glass (decorative)
├───┼───┼───┼───┼───┼───┼───┼───┼───┤
│ 9 │10 │11 │12 │13 │14 │15 │16 │17 │  Row 2: PIN option (11), Password (15)
├───┼───┼───┼───┼───┼───┼───┼───┼───┤
│18 │19 │20 │21 │22 │23 │24 │25 │26 │  Row 3: Yellow glass (decorative)
└───┴───┴───┴───┴───┴───┴───┴───┴───┘

Slot 11: 🚪 Iron Door (PIN option)
Slot 15: 📄 Paper (Password option)
All others: Yellow glass panes (decorative)
```

---

## ⚙️ **Configuration Control**

### Enable Login Choice (Recommended)
```yaml
login-method-choice:
  enabled: true
```
**Result:** Players see the GUI menu every time they join

### Disable Login Choice
```yaml
login-method-choice:
  enabled: false
```
**Result:** Players go straight to `/login` prompt (no GUI)

---

## 🔄 **Complete Flow Diagram**

```
                    Player Joins Server
                            ↓
                    ┌───────────────┐
                    │ Premium Check │
                    └───────┬───────┘
                            ↓
                ┌───────────┴───────────┐
                │                       │
            Premium?                Cracked?
                │                       │
                ↓                       ↓
        Auto-Authenticate        Registered?
                │                       │
                ↓                       ↓
            Welcome!              ┌─────┴─────┐
                                  │           │
                                Yes          No
                                  │           │
                                  ↓           ↓
                        login-method-choice?  /register
                                  │
                        ┌─────────┴─────────┐
                        │                   │
                      Yes                  No
                        │                   │
                        ↓                   ↓
                  [CHOICE GUI]        /login prompt
                        │
            ┌───────────┴───────────┐
            │                       │
        Click PIN              Click Password
            │                       │
            ↓                       ↓
      Has PIN?                /login prompt
            │                       │
        ┌───┴───┐                   ↓
        │       │              Type password
      Yes      No                   │
        │       │                   ↓
        ↓       ↓              Authenticated ✅
    PIN Vault  Setup prompt
        │           │
        ↓           ↓
    Click nums  /setuppin or /login
        │           │
        ↓           ↓
    Authenticated ✅  Authenticated ✅
```

---

## 💡 **Key Features**

### ✅ **Always Shows Choice**
- Menu appears EVERY login for cracked players
- No matter if they have PIN or not
- Consistent user experience

### ✅ **Smart Detection**
- Knows if player has PIN setup
- Changes button text accordingly
- Guides players appropriately

### ✅ **Flexible Options**
- Players can choose different method each time
- Can setup PIN later
- Can always use password as fallback

### ✅ **Premium Bypass**
- Premium players skip everything
- Instant access
- No authentication needed

---

## 🎯 **Comparison: With vs Without Choice**

### With `login-method-choice: true` (Recommended)

```
Cracked Player Joins
        ↓
[GUI Menu Opens]
        ↓
Player Clicks Choice
        ↓
Appropriate Action
```

**Benefits:**
- ✅ Visual, user-friendly
- ✅ No commands to remember
- ✅ Can choose each time
- ✅ Modern interface

### With `login-method-choice: false`

```
Cracked Player Joins
        ↓
Chat: "Please login with /login <password>"
        ↓
Player Types Command
        ↓
Authenticated
```

**Benefits:**
- ✅ Faster for experienced players
- ✅ Traditional approach
- ✅ No GUI needed

---

## 📊 **What Players See**

### Player WITH PIN:
```
┌─────────────────────────────┐
│  Choose Login Method        │
├─────────────────────────────┤
│  🚪 Login with PIN          │
│  ✔ Click to select          │
│                             │
│  📄 Login with Password     │
│  ✔ Click to select          │
└─────────────────────────────┘
```

### Player WITHOUT PIN:
```
┌─────────────────────────────┐
│  Choose Login Method        │
├─────────────────────────────┤
│  🚪 Setup PIN (Optional)    │
│  Click to setup             │
│  (You can skip this)        │
│                             │
│  📄 Login with Password     │
│  ✔ Click to select          │
└─────────────────────────────┘
```

---

## 🎓 **Teaching Players**

### Announce to Your Server:

```
📢 NEW LOGIN SYSTEM!

When you join the server, you'll see a menu:

🚪 PIN Option:
- Fast login with 4 numbers
- Click numbers in a vault
- No typing needed!

📄 Password Option:
- Traditional /login command
- Use your password
- Works like before

Choose your preferred method each time you join!

Premium players: You're auto-logged in! 👑
```

---

## 🔧 **Troubleshooting**

### Q: Menu doesn't appear?
**A:** Check config.yml:
```yaml
login-method-choice:
  enabled: true  # Must be true
```

### Q: Can't click the items?
**A:** Make sure you're clicking:
- The Iron Door (left)
- The Paper (right)
- Not the yellow glass

### Q: Premium player sees menu?
**A:** Check:
```yaml
premium-bypass:
  enabled: true  # Must be true
```

### Q: Want to skip menu?
**A:** Set in config:
```yaml
login-method-choice:
  enabled: false
```

---

## 🎉 **Summary**

### How Selection Works:

1. **Automatic**: Menu opens when cracked player joins
2. **Visual**: No commands needed to choose
3. **Click to Select**: Click Iron Door (PIN) or Paper (Password)
4. **Every Time**: Shows on every login
5. **Smart**: Adapts based on whether player has PIN
6. **Optional**: Can be disabled in config

### What Players Get:

- 🎮 **Choice**: Pick their preferred method
- 👆 **Easy**: Just click an item
- 🔄 **Flexible**: Can change each time
- 🎨 **Modern**: Beautiful GUI interface
- ⚡ **Fast**: Quick selection process

---

**The menu is automatic, visual, and gives players full control over how they login!** 🎮

**JAR Location:** `target/MBTHLoginSecurity-1.0.0.jar`  
**Status:** ✅ Ready to Use

