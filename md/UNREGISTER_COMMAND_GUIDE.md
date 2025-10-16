# Unregister Command - Admin Guide

## Overview

The `/unregister` command allows administrators to completely delete a player's account from the MBTH Login Security system. This is a powerful command that should be used with caution.

---

## Command Syntax

```
/unregister <player>
```

**Aliases:**
- `/unreg <player>`
- `/deleteaccount <player>`

**Permission:** `mbth.admin` (or server OP)

---

## What It Does

When you unregister a player, the command will:

1. ✅ Remove the player from registered players list
2. ✅ Delete their password
3. ✅ Delete their PIN (if they have one)
4. ✅ Remove them from authenticated players
5. ✅ Remove them from frozen players (if frozen)
6. ✅ Update IP tracking (remove player from IP records)
7. ✅ Delete ALL player data from `players.yml`
8. ✅ Kick the player if they're online
9. ✅ Send Discord notification (if enabled)
10. ✅ Log the action to console

---

## Usage Examples

### Example 1: Unregister Online Player
```
/unregister Steve
```

**Output:**
```
✔ Player unregistered successfully!
Player: Steve
All account data has been deleted.
```

**Player sees:**
```
✦ MBTH Security ✦

Your account has been unregistered by an administrator.
You can register a new account by rejoining the server.

Use: /register <password> <confirm>
```

### Example 2: Unregister Offline Player
```
/unregister Alex
```

**Output:**
```
✔ Player unregistered successfully!
Player: Alex
All account data has been deleted.
```

### Example 3: Console Usage
```
unregister Notch
```

Works from console as well!

---

## When to Use

### ✅ Good Use Cases

1. **Account Reset Request**
   - Player forgot password AND PIN
   - Player wants to start fresh

2. **Data Corruption**
   - Player's data file is corrupted
   - Account has issues that can't be fixed

3. **Player Left Permanently**
   - Clean up old player data
   - Free up storage space

4. **Security Breach**
   - Account was compromised
   - Force complete re-registration

5. **Testing**
   - Testing registration flow
   - Creating fresh test accounts

### ❌ When NOT to Use

1. **Password Reset** - Use `/resetpassword` instead
2. **PIN Reset** - Use `/resetpin` instead
3. **Temporary Lock** - Use `/freezeaccount` instead
4. **Force Re-login** - Use `/forcelogout` instead

---

## Error Messages

### Player Not Found
```
✘ Player not found!
```
**Cause:** Player name doesn't exist or never joined the server.

### Player Not Registered
```
✘ This player is not registered!
```
**Cause:** Player exists but hasn't registered an account yet.

### No Permission
```
✘ You don't have permission to use this command!
```
**Cause:** You don't have `mbth.admin` permission or OP status.

---

## Data Removed

The command removes the following data from `players.yml`:

```yaml
players:
  <uuid>:
    password: DELETED
    pin: DELETED
    registered-date: DELETED
    last-login: DELETED
    session-end: DELETED
    last-ip: DELETED
    alt-of: DELETED
    detected-date: DELETED
    frozen-by: DELETED
    frozen-date: DELETED
    # Everything under this player's UUID is deleted
```

---

## IP Tracking Behavior

### If player was the ONLY account on that IP:
- IP entry is removed completely

### If player shared IP with other accounts:
- Player is removed from the IP list
- IP entry remains with other accounts
- Alt detection for other accounts is updated

**Example:**

**Before:**
```yaml
ip-tracking:
  192_168_1_100:
    - uuid1  # Steve
    - uuid2  # Alex
```

**After unregistering Steve:**
```yaml
ip-tracking:
  192_168_1_100:
    - uuid2  # Alex only
```

---

## Discord Notification

If Discord webhooks are enabled, a security alert is sent:

```
⚠️ Security Alert
🗑️ PlayerName's account was unregistered by AdminName
```

This appears as a **red embed** in your security channel.

---

## Console Log

The action is logged to the server console:

```
[MBTH Login Security] [SECURITY] Account unregistered: PlayerName by AdminName
```

This helps with audit trails and tracking who unregistered whom.

---

## After Unregistration

### What happens next for the player:

1. **If they're online:**
   - They are immediately kicked
   - They see the unregistration message

2. **When they rejoin:**
   - Treated as a new player
   - Must use `/register <password> <confirm>`
   - Can create new account with same username

3. **Previous data:**
   - All previous login data is gone
   - No password, no PIN, no history
   - Fresh start

---

## Security Considerations

### ⚠️ Important Notes

1. **Irreversible Action**
   - Cannot be undone
   - All data is permanently deleted
   - No backup is created automatically

2. **Player Can Re-register**
   - Same player can create new account
   - Use `/freezeaccount` if you want to prevent this

3. **IP History Preserved**
   - IP tracking is updated but not deleted
   - Alt account detection still works
   - Other accounts on same IP remain tracked

4. **Audit Trail**
   - Action is logged to console
   - Discord notification sent (if enabled)
   - Admin name is recorded in logs

---

## Comparison with Other Commands

| Command | What it does | Data kept? | Can login after? |
|---------|-------------|------------|------------------|
| `/unregister` | Delete account completely | NO | After re-registering |
| `/resetpassword` | Change password | YES | Yes (with new password) |
| `/resetpin` | Delete PIN only | YES | Yes (without PIN) |
| `/freezeaccount` | Lock account | YES | No (until unfrozen) |
| `/forcelogout` | Logout player | YES | Yes (re-login required) |

---

## Best Practices

### 1. Confirm Before Using
```
Are you sure you want to unregister PlayerName?
This will delete ALL their account data.
```
Consider adding a confirmation prompt in your workflow.

### 2. Document the Reason
Keep a record of why accounts were unregistered:
- Staff notes
- Support tickets
- Admin logs

### 3. Notify the Player
If possible, tell the player before unregistering:
- Discord message
- In-game message
- Email/support ticket

### 4. Backup First (Optional)
For important accounts, consider backing up `players.yml` first:
```bash
cp plugins/MBTHLoginSecurity/players.yml players.yml.backup
```

### 5. Use Alternatives When Possible
- Password issues? → `/resetpassword`
- PIN issues? → `/resetpin`
- Temporary ban? → `/freezeaccount`

---

## Troubleshooting

### Command doesn't work
1. Check you have `mbth.admin` permission
2. Verify player name is correct (case-sensitive)
3. Check console for errors

### Player data not deleted
1. Run `/mbthlsreload` after unregistering
2. Check `players.yml` manually
3. Restart server if needed

### Player can't re-register
1. Check if account is frozen: `/checkalt <player>`
2. Verify they're using correct syntax: `/register <pass> <confirm>`
3. Check console for errors

---

## Example Workflow

### Complete Account Removal

```bash
# 1. Check if player has alt accounts
/checkalt PlayerName

# 2. Unregister the player
/unregister PlayerName

# 3. Verify in console
# Look for: [SECURITY] Account unregistered: PlayerName by You

# 4. (Optional) Reload config
/mbthlsreload

# 5. Player can now re-register when they rejoin
```

---

## Permissions

To use this command, you need:

```yaml
permissions:
  mbth.admin:
    description: Administrator permissions
    default: op
```

Or be a server operator (OP).

---

## Related Commands

- `/resetpassword <player> <new-pass>` - Reset password without deleting account
- `/resetpin <player>` - Remove PIN without deleting account
- `/freezeaccount <player>` - Lock account without deleting
- `/checkalt <player>` - Check if player has alt accounts

---

## FAQ

**Q: Can the player re-use their old username?**  
A: Yes! After unregistration, the username is available again.

**Q: Does this ban the player?**  
A: No. It only deletes their account. They can re-register immediately.

**Q: Is there a confirmation prompt?**  
A: No, the command executes immediately. Be careful!

**Q: Can I unregister myself?**  
A: Yes, if you have admin permission, but you'll be kicked.

**Q: Does this affect other plugins?**  
A: No, this only affects MBTH Login Security data.

**Q: Where is the data stored?**  
A: `plugins/MBTHLoginSecurity/players.yml`

**Q: Can I recover deleted accounts?**  
A: Only if you have a backup of `players.yml`. Otherwise, no.

---

## Developer Notes

### Implementation Details

The command performs these operations in order:

1. Validate sender permissions
2. Validate target player exists
3. Validate player is registered
4. Remove from in-memory sets (registeredPlayers, playersWithPin, etc.)
5. Delete YAML data node
6. Update IP tracking lists
7. Update frozen players list
8. Save config to disk
9. Kick player if online
10. Log to console
11. Send Discord notification

### Code Location

File: `MBTHLoginSecurity.java`  
Method: `handleUnregister(CommandSender sender, String[] args)`  
Lines: ~831-924

---

**⚠️ Use with caution! This action is permanent.**

