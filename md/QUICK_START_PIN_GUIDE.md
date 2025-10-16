# Quick Start Guide - PIN Code System

## 🚀 For Server Administrators

### Installation
1. Plugin is already built: `target/MBTHLoginSecurity-1.0.0.jar`
2. Copy to your server's `plugins/` folder
3. Restart server
4. Configure `plugins/MBTHLoginSecurity/config.yml`

### Quick Configuration

**Make PINs Required for Everyone:**
```yaml
pin-code:
  enabled: true
  required: true
```

**Make PINs Optional:**
```yaml
pin-code:
  enabled: true
  required: false
```

**Disable PIN System:**
```yaml
pin-code:
  enabled: false
```

### Admin Commands
- `/resetpin <player>` - Reset a player's PIN
- Requires `mbth.admin` permission or OP

---

## 👤 For Players

### First Time Setup (If Required)
1. Join server
2. `/register <password> <password>` or `/login <password>`
3. You'll be prompted: **"PIN Setup Required"**
4. `/setuppin 1234 1234` (use your own 4-digit PIN)
5. Done! You're in.

### Optional Setup (If Not Required)
1. Login normally: `/login <password>`
2. Optionally add PIN: `/setuppin 1234 1234`
3. Next login, you'll need to verify PIN

### Daily Login (With PIN)
1. Join server
2. `/login <password>`
3. `/verifypin 1234` (enter your PIN)
4. Done!

### Managing Your PIN

**Change PIN:**
```
/changepin <old-pin> <new-pin> <new-pin>
```

**Remove PIN** (if allowed):
```
/removepin <current-pin>
```

---

## 📋 Command Reference

| Command | Description | Example |
|---------|-------------|---------|
| `/setuppin <pin> <confirm>` | Create PIN | `/setuppin 1234 1234` |
| `/verifypin <pin>` | Enter PIN | `/verifypin 1234` |
| `/changepin <old> <new> <confirm>` | Change PIN | `/changepin 1234 5678 5678` |
| `/removepin <pin>` | Remove PIN | `/removepin 1234` |
| `/resetpin <player>` | Admin: Reset PIN | `/resetpin Steve` |

---

## ❓ Common Questions

**Q: I forgot my PIN!**  
A: Ask an admin to use `/resetpin <yourname>`

**Q: How long should my PIN be?**  
A: Default is 4 digits. Check with server admin.

**Q: Can I use letters in my PIN?**  
A: No, only numbers (0-9).

**Q: Do I need a PIN if I already have a password?**  
A: Depends on server settings. It's extra security!

**Q: What happens if I enter wrong PIN?**  
A: You get 3 attempts (default), then kicked.

---

## 🛡️ Security Tips

### For Players
- ✅ Use different PIN than password
- ✅ Don't share your PIN
- ✅ Avoid obvious PINs (1234, 0000)
- ✅ Change PIN regularly

### For Admins
- ✅ Set `required: true` for high security
- ✅ Use 4-6 digit PIN length
- ✅ Monitor reset requests
- ✅ Log admin actions

---

## 🎯 Examples

### Example 1: New Player Registration
```
[Player joins server]
Server: Please register with /register <password> <password>

/register MySecret123 MySecret123
Server: ✓ Successfully registered!
Server: PIN Setup Required - use /setuppin <pin> <confirm-pin>

/setuppin 9876 9876
Server: ✓ PIN code setup successfully!
Server: Welcome to MBTH!
```

### Example 2: Returning Player Login
```
[Player joins server]
Server: Please login with /login <password>

/login MySecret123
Server: ✓ Successfully logged in!
Server: PIN Verification Required - use /verifypin <pin>

/verifypin 9876
Server: ✓ PIN verified successfully!
Server: Welcome back to MBTH!
```

### Example 3: Changing PIN
```
/changepin 9876 4321 4321
Server: ✓ PIN changed successfully!
```

### Example 4: Admin Helping Player
```
[Admin] /resetpin Steve
Server: ✓ PIN reset successfully!
Server: Player: Steve
Server: The player's PIN has been removed.

[Steve can now set up a new PIN]
```

---

## 🔧 Troubleshooting

### "PIN must be exactly 4 digits"
- Check server's PIN length setting
- Use only numbers, no letters or symbols

### "Incorrect PIN"
- Double-check your PIN
- You have limited attempts before kick
- Ask admin for reset if forgotten

### "PIN is required on this server"
- Server requires all players to have PINs
- Cannot remove PIN
- Can change PIN with `/changepin`

### "You must be logged in first"
- Use `/login <password>` before PIN commands
- PIN comes AFTER password authentication

---

## 📞 Support

Need help? Contact server administrators or check full documentation in `PIN_SYSTEM_DOCUMENTATION.md`

---

**Quick Start Guide v1.0**  
**MBTH Login Security Plugin**

