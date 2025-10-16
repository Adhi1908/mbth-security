# Discord Webhook Integration Guide

## Overview

MBTH Login Security now uses **Discord Webhooks** instead of DiscordSRV for notifications. This approach is:
- ✅ **More optimized** - No external plugin dependencies
- ✅ **Lightweight** - Direct HTTP requests to Discord
- ✅ **Faster** - No middleware processing
- ✅ **More reliable** - Direct API integration
- ✅ **Better formatted** - Rich embeds with colors and emojis

---

## Setup Instructions

### 1. Create Discord Webhooks

#### For Login Channel:
1. Go to your Discord server
2. Right-click on the channel where you want login notifications
3. Click **Edit Channel** → **Integrations** → **Webhooks**
4. Click **New Webhook**
5. Name it something like "MBTH Security - Logins"
6. Click **Copy Webhook URL**
7. Save this URL for later

#### For Registration Channel:
1. Repeat the same steps for your registration channel
2. You can use the same channel or a different one
3. Copy the webhook URL

### 2. Configure the Plugin

Open `config.yml` and find the Discord section:

```yaml
# Discord Integration (Webhooks)
discord:
  # Enable Discord webhook notifications
  enabled: true
  
  # Discord webhook URL for login notifications
  login-webhook: "https://discord.com/api/webhooks/YOUR_WEBHOOK_ID/YOUR_WEBHOOK_TOKEN"
  
  # Discord webhook URL for registration notifications
  registration-webhook: "https://discord.com/api/webhooks/YOUR_WEBHOOK_ID/YOUR_WEBHOOK_TOKEN"
  
  # Log premium player auto-login to Discord
  log-premium-login: true
  
  # Log failed login attempts to Discord
  log-failed-attempts: true
  
  # Webhook customization
  username: "MBTH Security"
  avatar-url: "https://i.imgur.com/AfFp7pu.png"
```

### 3. Paste Your Webhook URLs

Replace the placeholder URLs with your actual webhook URLs:

```yaml
discord:
  enabled: true
  login-webhook: "https://discord.com/api/webhooks/123456789012345678/abcdefghijklmnopqrstuvwxyz1234567890"
  registration-webhook: "https://discord.com/api/webhooks/987654321098765432/zyxwvutsrqponmlkjihgfedcba0987654321"
```

> **Note:** You can use the same webhook URL for both channels if you want all notifications in one place.

### 4. Reload the Plugin

In-game, run:
```
/mbthlsreload
```

You should see: "Discord webhook integration enabled!"

---

## Notification Types

### 🔓 Login Events (Green Embeds)
- Successful password login
- Successful PIN login
- PIN verification
- Premium player auto-login

**Example:**
```
🔓 Login Event
🔓 PlayerName logged in successfully (Password)
```

### 📝 Registration Events (Blue Embeds)
- New account registration
- PIN setup completion

**Example:**
```
📝 Registration Event
🆕 PlayerName registered a new account!
```

### ⚠️ Security Alerts (Red Embeds)
- Failed login attempts
- Too many failed attempts (kicked)

**Example:**
```
⚠️ Security Alert
❌ PlayerName failed login attempt (2/3)
```

---

## Customization

### Change Bot Name and Avatar

```yaml
discord:
  username: "My Custom Security Bot"
  avatar-url: "https://example.com/my-avatar.png"
```

### Disable Specific Notifications

```yaml
discord:
  # Don't log premium logins
  log-premium-login: false
  
  # Don't log failed attempts
  log-failed-attempts: false
```

### Use One Webhook for Everything

```yaml
discord:
  login-webhook: "https://discord.com/api/webhooks/YOUR_WEBHOOK"
  registration-webhook: "https://discord.com/api/webhooks/YOUR_WEBHOOK"  # Same URL
```

---

## Embed Colors

The plugin automatically uses different colors for different event types:

- **Green (#2ECC71)** - Login Events
- **Blue (#5865F2)** - Registration Events
- **Red (#E74C3C)** - Security Alerts

These colors are hardcoded to match Discord's theme but provide clear visual separation.

---

## Troubleshooting

### No Notifications Appearing

1. **Check webhook URL is correct**
   - Must start with `https://discord.com/api/webhooks/`
   - Must contain both webhook ID and token

2. **Check Discord integration is enabled**
   ```yaml
   discord:
     enabled: true  # Make sure this is true
   ```

3. **Check console for errors**
   - Look for "Discord webhook returned code XXX"
   - Code 404 = Invalid webhook URL
   - Code 401 = Invalid token

4. **Reload the config**
   ```
   /mbthlsreload
   ```

### Webhook URL Not Working

Make sure your webhook URL looks like this:
```
https://discord.com/api/webhooks/1234567890/abcdefghijklmnop-QRSTUVWXYZ
```

**Not like this:**
- ❌ `discord.com/webhooks/...` (missing https://)
- ❌ `https://discord.gg/...` (that's an invite link)
- ❌ `https://discord.com/channels/...` (that's a channel link)

### Bot Name/Avatar Not Showing

Some servers may have restrictions on webhook customization. Check your Discord server settings:
- Server Settings → Integrations → Manage Webhooks
- Ensure webhooks are allowed

---

## Migration from DiscordSRV

If you were previously using DiscordSRV:

1. **No plugin dependency needed** - You can remove DiscordSRV if you only used it for this plugin
2. **Update config.yml** - Replace channel names with webhook URLs
3. **Reload** - Run `/mbthlsreload`

### Old Config (DiscordSRV):
```yaml
discord:
  enabled: true
  login-channel: "login-logs"
  registration-channel: "registration-logs"
```

### New Config (Webhooks):
```yaml
discord:
  enabled: true
  login-webhook: "https://discord.com/api/webhooks/..."
  registration-webhook: "https://discord.com/api/webhooks/..."
```

---

## Advantages Over DiscordSRV

| Feature | DiscordSRV | Webhooks |
|---------|-----------|----------|
| Dependencies | Requires DiscordSRV plugin | None |
| Performance | Medium (plugin overhead) | High (direct API) |
| Setup | Install plugin + configure | Just add URLs |
| Reliability | Depends on plugin updates | Direct Discord API |
| Customization | Limited by DiscordSRV | Full control |
| Embeds | Basic text | Rich embeds with colors |
| Async | Depends on DiscordSRV | Always async |

---

## Security Notes

- **Keep webhook URLs private** - Anyone with the URL can send messages
- **Don't share config.yml** publicly with webhook URLs in it
- **Regenerate webhooks** if they become compromised:
  - Discord → Channel Settings → Webhooks → Delete old webhook → Create new one

---

## Examples

### Minimal Configuration
```yaml
discord:
  enabled: true
  login-webhook: "YOUR_WEBHOOK_URL"
  registration-webhook: "YOUR_WEBHOOK_URL"
```

### Full Configuration
```yaml
discord:
  enabled: true
  login-webhook: "https://discord.com/api/webhooks/123/abc"
  registration-webhook: "https://discord.com/api/webhooks/456/def"
  log-premium-login: true
  log-failed-attempts: true
  username: "MBTH Security Bot"
  avatar-url: "https://i.imgur.com/AfFp7pu.png"
```

### Disable Discord
```yaml
discord:
  enabled: false
```

---

## Support

If you encounter issues with Discord webhooks:

1. Check the server console for error messages
2. Verify your webhook URLs are correct
3. Test the webhook manually using a tool like [Webhook.site](https://webhook.site)
4. Make sure the Discord channel still exists and hasn't been deleted

---

**Happy logging! 🎉**

