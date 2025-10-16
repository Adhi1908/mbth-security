# PIN Code System Documentation

## Overview

The PIN Code System adds an extra layer of security to your MBTH Login Security plugin. After players successfully login with their password, they can optionally (or must, if required) set up a numeric PIN code for additional protection.

## Features

### 1. **Two-Factor Authentication**
   - Players first login with their password
   - Then verify with their numeric PIN code
   - Provides enhanced account security

### 2. **Configurable PIN Length**
   - Default: 4 digits
   - Customizable in config.yml (1-10 digits recommended)
   - Only numeric characters allowed

### 3. **Optional or Required**
   - Can be made optional (players choose to set up)
   - Can be made required (all players must set up)
   - Configurable via `pin-code.required` setting

### 4. **Failed Attempt Protection**
   - Tracks failed PIN verification attempts
   - Kicks player after max attempts (default: 3)
   - Prevents brute-force attacks

## Player Commands

### `/setuppin <pin> <confirm-pin>`
**Aliases:** `/setpin`, `/createpin`
- **Description:** Set up a new PIN code
- **Requirements:** Must be logged in first
- **Example:** `/setuppin 1234 1234`
- **Notes:** 
  - PIN must match confirmation
  - PIN must be exactly the configured length (default 4 digits)
  - Only numeric digits allowed

### `/verifypin <pin>`
**Aliases:** `/pin`, `/enterpin`
- **Description:** Verify your PIN code after login
- **Requirements:** Must be logged in, have a PIN setup
- **Example:** `/verifypin 1234`
- **Notes:**
  - Required after each login if PIN is set up
  - Limited attempts before kick

### `/changepin <old-pin> <new-pin> <confirm-new>`
**Aliases:** `/modifypin`
- **Description:** Change your existing PIN
- **Requirements:** Must be fully authenticated
- **Example:** `/changepin 1234 5678 5678`
- **Notes:**
  - Must provide correct old PIN
  - New PIN must meet length requirements

### `/removepin <current-pin>`
**Aliases:** `/deletepin`
- **Description:** Remove your PIN code
- **Requirements:** Must be fully authenticated
- **Example:** `/removepin 1234`
- **Notes:**
  - Cannot remove PIN if server requires it
  - Must provide correct current PIN
  - Account will no longer require PIN verification

## Admin Commands

### `/resetpin <player>`
**Aliases:** `/pinreset`
- **Permission:** `mbth.admin`
- **Description:** Reset a player's PIN code
- **Example:** `/resetpin Steve`
- **Notes:**
  - Removes the player's PIN
  - Player will need to set up a new PIN
  - Action is logged for security
  - Can be executed from console

## Configuration

Add these settings to your `config.yml`:

```yaml
# PIN Code System
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

### Configuration Options Explained

- **`enabled`**: Master switch for PIN system (true/false)
- **`required`**: If true, all players MUST set up a PIN after first login
- **`length`**: Number of digits required for PIN (recommended: 4-6)
- **`max-attempts`**: Failed attempts before player is kicked (recommended: 3)

## How It Works

### Login Flow (PIN Enabled, Not Required)

1. Player joins server
2. Player uses `/register` or `/login`
3. **If player has PIN:** Prompted to verify PIN
4. **If player doesn't have PIN:** Fully authenticated
5. Player can optionally set up PIN with `/setuppin`

### Login Flow (PIN Enabled, Required)

1. Player joins server
2. Player uses `/register` or `/login`
3. **If player has PIN:** Prompted to verify PIN
4. **If player doesn't have PIN:** Prompted to set up PIN
5. Player MUST use `/setuppin` before accessing server
6. After PIN setup, player is fully authenticated

### Security During PIN Verification

While waiting for PIN verification, players are restricted:
- ✓ Can use `/verifypin` or `/setuppin`
- ✗ Cannot move
- ✗ Cannot chat
- ✗ Cannot use other commands
- ✗ Cannot take damage
- ✗ Cannot interact with world
- ✗ Cannot open inventories

## Data Storage

PIN codes are stored in `plugins/MBTHLoginSecurity/players.yml`:

```yaml
players:
  <uuid>:
    password: <hashed-password>
    pin: <hashed-pin>
    pin-setup-date: 1697472000000
    pin-changed-date: 1697472100000
    pin-reset-by: "Admin"
    pin-reset-date: 1697472200000
```

### Security Notes
- PINs are hashed using SHA-256 (same as passwords)
- Never stored in plain text
- Cannot be recovered, only reset
- All PIN actions are timestamped

## Use Cases

### 1. High-Security Server
```yaml
pin-code:
  enabled: true
  required: true
  length: 6
  max-attempts: 3
```
All players must use 6-digit PINs for maximum security.

### 2. Optional Extra Security
```yaml
pin-code:
  enabled: true
  required: false
  length: 4
  max-attempts: 3
```
Players can choose to add PIN protection to their accounts.

### 3. Disabled PIN System
```yaml
pin-code:
  enabled: false
```
Traditional password-only authentication.

## Troubleshooting

### Player Forgot PIN
Admin solution: `/resetpin <player>`
- Removes player's PIN
- Player can set up new PIN
- If required=true, they must set up new PIN on next login

### Player Can't Enter PIN
- Check if PIN length matches config
- Ensure only numeric digits are used
- Verify attempts haven't been exceeded

### PIN Not Working After Login
- Ensure `pin-code.enabled: true` in config
- Check that player has set up PIN
- Verify player is logged in first

## Best Practices

1. **For Players:**
   - Use a unique PIN, different from password
   - Don't share your PIN with anyone
   - Change PIN regularly using `/changepin`
   - Don't use obvious PINs (1234, 0000, etc.)

2. **For Admins:**
   - Set reasonable `max-attempts` (3-5)
   - Use 4-6 digit PIN length
   - Monitor PIN reset requests
   - Enable required=true for high-security servers
   - Educate players about PIN security

3. **For Developers:**
   - PINs are hashed, never store plain text
   - All admin actions are logged
   - Failed attempts are tracked per session
   - Sessions clear PIN state on disconnect

## Permissions

- **`mbth.admin`**: Access to all admin commands including `/resetpin`
- Default players have access to all player PIN commands

## Integration with Existing Features

### Session Management
- PIN verification is required each session
- Even if password session is valid, PIN must be re-verified
- Session settings in config affect password, not PIN

### Alt Detection
- PIN system works alongside alt detection
- Each account needs separate PIN
- Admin can check alts and reset PINs independently

### Account Freezing
- Frozen accounts cannot set up or verify PINs
- PIN data is preserved when account is frozen/unfrozen

## Future Enhancements (Not Yet Implemented)

Potential features for future versions:
- PIN recovery questions
- Time-based PIN changes (force change every X days)
- Different PIN lengths per player
- PIN hints (non-obvious)
- 2FA app integration (TOTP)

## Support

For issues or questions:
- Check configuration is correct
- Verify Java version compatibility
- Review console logs for errors
- Contact MBTH Development team

---

**Version:** 1.0.0  
**Compatible With:** MBTH Login Security v1.0.0+  
**Minecraft Version:** 1.21  
**API:** Paper/Spigot

