# 🎨 Title & GUI Customization Guide

## ✨ What's New

### 1. **Fully Customizable Titles**
All titles in the plugin can now be customized!

### 2. **Textured Skull GUI**
PIN vault now uses clean, modern textured player heads instead of simple skulls.

---

## 🎯 Configuration

### Title Customization

```yaml
# config.yml

# Server/Network Name (Customizable branding)
server-name: "MBTH"

# Title Customization
titles:
  # Main title shown on join (supports color codes with &)
  main-title: "&6✦ &e&lMBTH Security &6✦"
  
  # Subtitle shown below main title
  subtitle: "&ePlease login to continue"
  
  # Scoreboard/Action bar title
  scoreboard-title: "&6&lMBTH LOGIN SECURITY"
```

---

## 🎨 Customization Examples

### Example 1: Simple Server Name
```yaml
titles:
  main-title: "&e&lMyServer"
  subtitle: "&7Please authenticate"
  scoreboard-title: "&e&lMYSERVER LOGIN"
```

### Example 2: Professional Style
```yaml
titles:
  main-title: "&6« &b&lAWESOME NETWORK &6»"
  subtitle: "&fPlease login to continue"
  scoreboard-title: "&b&lAWESOME NETWORK &7• &fSECURITY"
```

### Example 3: Gaming Style
```yaml
titles:
  main-title: "&4⚔ &c&lEPIC GAMING &4⚔"
  subtitle: "&6Login required!"
  scoreboard-title: "&c&lEPIC GAMING &4SECURITY"
```

### Example 4: Minimal Clean
```yaml
titles:
  main-title: "&f&lSERVER NAME"
  subtitle: "&7Login to play"
  scoreboard-title: "&f&lLOGIN SYSTEM"
```

### Example 5: Fancy Unicode
```yaml
titles:
  main-title: "&d✧ &5&lMYSTIC SERVER &d✧"
  subtitle: "&7✦ Please authenticate ✦"
  scoreboard-title: "&5&lMYSTIC SERVER &d❖ &7SECURITY"
```

---

## 🎭 Color Codes Reference

```
&0 - Black          &8 - Dark Gray
&1 - Dark Blue      &9 - Blue
&2 - Dark Green     &a - Green
&3 - Dark Aqua      &b - Aqua
&4 - Dark Red       &c - Red
&5 - Dark Purple    &d - Light Purple
&6 - Gold           &e - Yellow
&7 - Gray           &f - White

&l - Bold
&m - Strikethrough
&n - Underline
&o - Italic
&r - Reset
```

### Unicode Symbols
```
✦ ✧ ❖ ⚔ ✪ ✯ ★ ☆ ◆ ◇ ● ○ ▪ ▫ ■ □ 
« » ‹ › 【 】 『 』 「 」 ⟨ ⟩ ◀ ▶
```

---

## 💀 PIN Vault GUI Features

### Textured Skulls
The PIN vault now uses textured player heads:

**Number Buttons (0-9):**
- Clean blue modern number textures
- Green name (&a&l)
- Professional appearance
- Lore with number info

**CLEAR Button:**
- Orange/Red skull texture
- Yellow name (&e&l)
- Red reset indicator
- Clear lore text

**SUBMIT Button:**
- Green checkmark texture
- Green name (&a&l)
- Confirmation indicator
- Submit lore text

**Example Display:**
```
+----------------------------------------------+
|         [PIN DISPLAY - PAPER ITEM]           |
|                                              |
|     [Blue 1] [Blue 2] [Blue 3]               |
|     [Blue 4] [Blue 5] [Blue 6]               |
|     [Blue 7] [Blue 8] [Blue 9]               |
|  [Orange CLEAR] [Blue 0] [Green SUBMIT]      |
+----------------------------------------------+
```

---

## 🔄 Applying Changes

### Method 1: Reload Command
```
/mbthlsreload
```
- Instant reload
- No server restart needed
- All titles update immediately

### Method 2: Server Restart
```
1. Edit config.yml
2. Save changes
3. Restart server
```

---

## 📝 Where Titles Appear

### Main Title (`main-title`)
- **Player login prompt**
- **Player registration prompt**
- **PIN verification prompt**
- **PIN setup prompt**

**Displayed:** Top center of screen (large title)

### Subtitle (`subtitle`)
- Shown below main title
- Context-specific messages
- Login/register instructions

**Displayed:** Below main title (smaller text)

### Scoreboard Title (`scoreboard-title`)
- **Chat messages**
- **Login/register prompts**
- **Action bar**
- **System messages**

**Displayed:** In chat and UI elements

---

## 🎯 Best Practices

### Title Length
- **Main Title:** 20-30 characters max
- **Subtitle:** 30-40 characters max
- **Scoreboard:** 25-35 characters max

### Color Scheme
- Use consistent colors across all titles
- Match your server's theme
- Don't use too many colors (3-4 max)

### Formatting
- Bold (&l) for emphasis
- Symbols for decoration
- Consistent spacing

### Testing
- Test in-game before finalizing
- Check readability on different screens
- Verify color combinations

---

## 🖼️ Visual Examples

### Login Screen
```
╔════════════════════════════╗
║  &6✦ &e&lMBTH Security &6✦  ║
║  &ePlease login to continue ║
╚════════════════════════════╝

━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         &6&lMBTH LOGIN SECURITY

 &7Welcome back! Please authenticate
 
 &e➤ &fUse: &a/login <password>
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### Registration Screen
```
╔════════════════════════════╗
║  &6✦ &e&lMBTH Security &6✦  ║
║   &aPlease register account ║
╚════════════════════════════╝

━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         &6&lMBTH LOGIN SECURITY

 &7Welcome! Please register your account
 
 &e➤ &fUse: &a/register <pass> <pass>
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 🎨 Theme Templates

### Corporate Professional
```yaml
titles:
  main-title: "&f&lCOMPANY NAME &8| &7Security"
  subtitle: "&7Please verify your credentials"
  scoreboard-title: "&f&lCOMPANY &8• &7SECURITY SYSTEM"
```

### Gaming Network
```yaml
titles:
  main-title: "&6★ &e&lGAMING NETWORK &6★"
  subtitle: "&aJoin the adventure!"
  scoreboard-title: "&e&lGAMING NETWORK &6SECURITY"
```

### Roleplay Server
```yaml
titles:
  main-title: "&5&l⚜ ROYAL REALM ⚜"
  subtitle: "&dState your identity"
  scoreboard-title: "&5&lROYAL REALM &d⚜ &7GATE KEEPER"
```

### PvP Server
```yaml
titles:
  main-title: "&c&l⚔ BATTLE ARENA ⚔"
  subtitle: "&7Enter the battlefield"
  scoreboard-title: "&c&lBATTLE ARENA &4SECURITY"
```

### Survival Server
```yaml
titles:
  main-title: "&2&l🌲 SURVIVAL WORLD 🌲"
  subtitle: "&aWelcome, survivor"
  scoreboard-title: "&2&lSURVIVAL &a• &7LOGIN"
```

---

## 🔧 Technical Details

### Title Display
```java
player.sendTitle(
    ChatColor.translateAlternateColorCodes('&', titleMain),
    ChatColor.translateAlternateColorCodes('&', titleSubtitle),
    10, 999999, 10
);
```

### Textured Skulls
```java
PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
PlayerTextures textures = profile.getTextures();
textures.setSkin(new URL(textureURL));
profile.setTextures(textures);
```

### Skull Textures
- Numbers: Clean blue modern digits
- CLEAR: Orange warning skull
- SUBMIT: Green checkmark skull
- High-resolution textures
- Professional appearance

---

## 📋 Quick Reference

| Setting | Default | Purpose |
|---------|---------|---------|
| `main-title` | `&6✦ &e&lMBTH Security &6✦` | Main screen title |
| `subtitle` | `&ePlease login to continue` | Subtitle message |
| `scoreboard-title` | `&6&lMBTH LOGIN SECURITY` | Chat/UI title |
| `server-name` | `MBTH` | Used in messages |

---

## 🎉 Complete Example

```yaml
# COMPLETE CUSTOMIZATION EXAMPLE

server-name: "AwesomeServer"

titles:
  main-title: "&6« &b&lAWESOME SERVER &6»"
  subtitle: "&fWelcome! Please authenticate"
  scoreboard-title: "&b&lAWESOME SERVER &3• &fSECURITY"

messages:
  welcome-premium: "&7Welcome to {server}! Enjoy your stay."
  welcome-back: "&7Welcome back to {server}!"
  welcome-registered: "&7Your account is secured on {server}."
  login-success: "&a&l✔ Successfully authenticated!"
  register-success: "&a&l✔ Account created successfully!"
  premium-detected: "&a&l✔ Premium Account Verified!"
  pin-verified: "&a&l✔ PIN verified!"
  reload-success: "&a&l✔ Configuration reloaded!"
  reload-details: "&7All settings updated successfully"
```

Save, run `/mbthlsreload`, and enjoy your customized security system! 🚀

---

**All titles and GUI elements are now fully customizable!** ✨

