# Unregister Command - Quick Reference

## Command

```
/unregister <player>
```

**Aliases:** `/unreg`, `/deleteaccount`  
**Permission:** `mbth.admin` or OP

---

## What It Does

🗑️ **Completely deletes a player's account** from the login system.

### Data Removed:
- ✅ Password
- ✅ PIN code
- ✅ Login history
- ✅ Session data
- ✅ Frozen status
- ✅ All player data

### What Happens:
1. Player is removed from all tracking
2. Player data deleted from `players.yml`
3. IP tracking updated
4. Player kicked if online
5. Discord notification sent
6. Console log created

---

## Quick Examples

### Unregister a player:
```
/unregister Steve
```

### From console:
```
unregister Alex
```

---

## Common Use Cases

| Scenario | Command |
|----------|---------|
| Password forgotten + PIN forgotten | `/unregister <player>` |
| Account corrupted | `/unregister <player>` |
| Fresh start requested | `/unregister <player>` |
| Clean up old accounts | `/unregister <player>` |

---

## ⚠️ Important

- **Irreversible** - Cannot be undone!
- **Player can re-register** - Not a ban
- **All data lost** - No recovery without backup
- **No confirmation** - Executes immediately

---

## Alternatives

Don't delete the account? Try these instead:

| Need | Command |
|------|---------|
| Reset password only | `/resetpassword <player> <new-pass>` |
| Remove PIN only | `/resetpin <player>` |
| Temporarily lock | `/freezeaccount <player>` |
| Force re-login | `/forcelogout <player>` |

---

## After Unregistration

**Player sees when kicked:**
```
Your account has been unregistered by an administrator.
You can register a new account by rejoining the server.

Use: /register <password> <confirm>
```

**When they rejoin:**
- Treated as new player
- Must `/register` again
- Can use same username
- Fresh account

---

## Discord Notification

```
⚠️ Security Alert
🗑️ PlayerName's account was unregistered by AdminName
```
*(Red embed in security channel)*

---

## Console Log

```
[SECURITY] Account unregistered: PlayerName by AdminName
```

---

## Errors

| Error | Meaning |
|-------|---------|
| `Player not found!` | Invalid player name |
| `Player not registered!` | No account exists |
| `No permission!` | Need `mbth.admin` or OP |

---

## Best Practices

✅ **DO:**
- Notify player first (if possible)
- Document the reason
- Use alternatives when possible
- Backup `players.yml` for important accounts

❌ **DON'T:**
- Use for temporary issues
- Forget it's permanent
- Use instead of password/PIN reset
- Use without verification

---

For complete documentation, see [UNREGISTER_COMMAND_GUIDE.md](UNREGISTER_COMMAND_GUIDE.md)

