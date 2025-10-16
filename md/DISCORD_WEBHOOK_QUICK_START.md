# Discord Webhooks - Quick Start

## 5-Minute Setup Guide

### Step 1: Create Webhook (2 min)

1. **Open Discord** → Your Server → Channel
2. **Right-click channel** → Edit Channel
3. **Integrations** → **Webhooks** → **New Webhook**
4. **Copy Webhook URL**

### Step 2: Configure Plugin (1 min)

Edit `config.yml`:

```yaml
discord:
  enabled: true
  login-webhook: "PASTE_YOUR_URL_HERE"
  registration-webhook: "PASTE_YOUR_URL_HERE"
```

### Step 3: Reload & Test (2 min)

In-game:
```
/mbthlsreload
```

Then test by logging in!

---

## Quick Config Template

Copy-paste this into your `config.yml`:

```yaml
# Discord Integration (Webhooks)
discord:
  enabled: true
  login-webhook: "https://discord.com/api/webhooks/YOUR_ID/YOUR_TOKEN"
  registration-webhook: "https://discord.com/api/webhooks/YOUR_ID/YOUR_TOKEN"
  log-premium-login: true
  log-failed-attempts: true
  username: "MBTH Security"
  avatar-url: "https://i.imgur.com/AfFp7pu.png"
```

---

## What You'll See in Discord

### ✅ Login Success
```
🔓 Login Event
🔓 PlayerName logged in successfully (Password)
```
*Green embed with timestamp*

### 📝 New Registration  
```
📝 Registration Event
🆕 PlayerName registered a new account!
```
*Blue embed with timestamp*

### ⚠️ Failed Login
```
⚠️ Security Alert
❌ PlayerName failed login attempt (1/3)
```
*Red embed with timestamp*

---

## Troubleshooting (30 seconds)

**No notifications?**
1. Check webhook URL starts with `https://discord.com/api/webhooks/`
2. Set `enabled: true` in config
3. Run `/mbthlsreload`

**Still not working?**
- Check console for errors
- Make sure channel exists
- Regenerate webhook

---

## Tips

✅ **Use same webhook for both** (login and registration)  
✅ **Keep URLs private** (don't share publicly)  
✅ **Test with `/login`** to verify it works  

---

That's it! You're done! 🎉

For detailed docs, see [DISCORD_WEBHOOK_GUIDE.md](DISCORD_WEBHOOK_GUIDE.md)

