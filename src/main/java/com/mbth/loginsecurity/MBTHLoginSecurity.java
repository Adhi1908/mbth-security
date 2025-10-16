package com.mbth.loginsecurity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MBTHLoginSecurity extends JavaPlugin implements Listener {
    
    private final Map<UUID, Boolean> authenticatedPlayers = new HashMap<>();
    private final Map<UUID, Location> loginLocations = new HashMap<>();
    private final Map<UUID, Integer> loginAttempts = new HashMap<>();
    private final Set<UUID> registeredPlayers = new HashSet<>();
    private final Set<UUID> frozenPlayers = new HashSet<>();
    private final Map<String, List<UUID>> ipToAccounts = new HashMap<>();
    
    private File dataFile;
    private FileConfiguration dataConfig;
    
    private int maxAttempts;
    private int loginTimeout;
    private boolean sessionEnabled;
    private int sessionDuration;
    private boolean altDetectionEnabled;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfiguration();
        
        dataFile = new File(getDataFolder(), "players.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        loadRegisteredPlayers();
        
        Bukkit.getPluginManager().registerEvents(this, this);
        
        getLogger().info("MBTH Login Security has been enabled!");
    }
    
    @Override
    public void onDisable() {
        savePlayerData();
        getLogger().info("MBTH Login Security has been disabled!");
    }
    
    private void loadConfiguration() {
        FileConfiguration config = getConfig();
        maxAttempts = config.getInt("max-login-attempts", 3);
        loginTimeout = config.getInt("login-timeout-seconds", 60);
        sessionEnabled = config.getBoolean("session.enabled", true);
        sessionDuration = config.getInt("session.duration-minutes", 30);
        altDetectionEnabled = config.getBoolean("alt-detection.enabled", true);
    }
    
    private void loadRegisteredPlayers() {
        if (dataConfig.contains("players")) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                registeredPlayers.add(UUID.fromString(uuidString));
            }
        }
        
        if (dataConfig.contains("ip-tracking")) {
            for (String ip : dataConfig.getConfigurationSection("ip-tracking").getKeys(false)) {
                List<String> uuidStrings = dataConfig.getStringList("ip-tracking." + ip);
                List<UUID> uuids = new ArrayList<>();
                for (String uuidStr : uuidStrings) {
                    uuids.add(UUID.fromString(uuidStr));
                }
                ipToAccounts.put(ip, uuids);
            }
        }
        
        if (dataConfig.contains("frozen-players")) {
            List<String> frozenList = dataConfig.getStringList("frozen-players");
            for (String uuidStr : frozenList) {
                frozenPlayers.add(UUID.fromString(uuidStr));
            }
        }
    }
    
    private void savePlayerData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String ip = player.getAddress().getAddress().getHostAddress();
        
        loginLocations.put(uuid, player.getLocation().clone());
        loginAttempts.put(uuid, 0);
        
        if (frozenPlayers.contains(uuid)) {
            player.kickPlayer(ChatColor.translateAlternateColorCodes('&',
                "&c&l✘ ACCOUNT FROZEN\n\n" +
                "&7Your account has been frozen by an administrator.\n" +
                "&7Please contact server staff for assistance."));
            return;
        }
        
        if (altDetectionEnabled) {
            detectAndNotifyAlts(player, ip, uuid);
        }
        
        trackIPAddress(ip, uuid);
        
        if (sessionEnabled && isSessionValid(uuid)) {
            authenticatedPlayers.put(uuid, true);
            sendMessage(player, "&a&lWelcome back! &7Session restored.");
            return;
        }
        
        authenticatedPlayers.put(uuid, false);
        
        player.setWalkSpeed(0);
        player.setFlySpeed(0);
        
        if (isRegistered(uuid)) {
            sendLoginPrompt(player);
        } else {
            sendRegisterPrompt(player);
        }
        
        startLoginTimeout(player);
    }
    
    private void detectAndNotifyAlts(Player player, String ip, UUID uuid) {
        List<UUID> accountsOnIP = ipToAccounts.get(ip);
        
        if (accountsOnIP != null && accountsOnIP.size() > 0) {
            List<UUID> otherAccounts = new ArrayList<>();
            for (UUID otherUUID : accountsOnIP) {
                if (!otherUUID.equals(uuid)) {
                    otherAccounts.add(otherUUID);
                }
            }
            
            if (!otherAccounts.isEmpty()) {
                UUID mainAccount = accountsOnIP.get(0);
                boolean isMain = uuid.equals(mainAccount);
                
                String notification = ChatColor.translateAlternateColorCodes('&',
                    "\n&6&l⚠ ALT ACCOUNT DETECTED\n" +
                    "&7Player: &e" + player.getName() + "\n" +
                    "&7IP Address: &e" + ip + "\n" +
                    "&7Account Type: &e" + (isMain ? "Main Account" : "Alt Account") + "\n" +
                    "&7Total Accounts: &e" + (otherAccounts.size() + 1) + "\n" +
                    "&7Other Accounts:");
                
                for (UUID otherUUID : otherAccounts) {
                    String otherName = Bukkit.getOfflinePlayer(otherUUID).getName();
                    if (otherName != null) {
                        notification += "\n  &8• &7" + otherName;
                    }
                }
                notification += "\n";
                
                getLogger().warning("ALT ACCOUNT DETECTED: " + player.getName() + " (IP: " + ip + ")");
                
                for (Player admin : Bukkit.getOnlinePlayers()) {
                    if (admin.hasPermission("mbth.admin") || admin.isOp()) {
                        admin.sendMessage(notification);
                    }
                }
                
                Bukkit.getConsoleSender().sendMessage(notification);
                
                dataConfig.set("players." + uuid.toString() + ".alt-of", mainAccount.toString());
                dataConfig.set("players." + uuid.toString() + ".detected-date", System.currentTimeMillis());
                savePlayerData();
            }
        }
    }
    
    private void trackIPAddress(String ip, UUID uuid) {
        dataConfig.set("players." + uuid.toString() + ".last-ip", ip);
        
        List<UUID> accounts = ipToAccounts.getOrDefault(ip, new ArrayList<>());
        if (!accounts.contains(uuid)) {
            accounts.add(uuid);
            ipToAccounts.put(ip, accounts);
            
            List<String> uuidStrings = new ArrayList<>();
            for (UUID accountUUID : accounts) {
                uuidStrings.add(accountUUID.toString());
            }
            dataConfig.set("ip-tracking." + ip.replace(".", "_"), uuidStrings);
            savePlayerData();
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        
        if (sessionEnabled && authenticatedPlayers.getOrDefault(uuid, false)) {
            saveSession(uuid);
        }
        
        authenticatedPlayers.remove(uuid);
        loginLocations.remove(uuid);
        loginAttempts.remove(uuid);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!isAuthenticated(event.getPlayer())) {
            Location from = event.getFrom();
            Location to = event.getTo();
            
            if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!isAuthenticated(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String cmd = event.getMessage().toLowerCase();
        
        if (!isAuthenticated(player)) {
            if (!cmd.startsWith("/login") && !cmd.startsWith("/register")) {
                event.setCancelled(true);
                sendMessage(player, "&cPlease login or register first!");
            }
        }
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!isAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (!isAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isAuthenticated(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && !command.getName().equalsIgnoreCase("resetpassword") 
            && !command.getName().equalsIgnoreCase("freezeaccount") 
            && !command.getName().equalsIgnoreCase("unfreezeaccount")
            && !command.getName().equalsIgnoreCase("forcelogout")
            && !command.getName().equalsIgnoreCase("checkalt")) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("register")) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            
            if (isAuthenticated(player)) {
                sendMessage(player, "&cYou are already authenticated!");
                return true;
            }
            
            if (isRegistered(uuid)) {
                sendMessage(player, "&cYou are already registered! Use /login <password>");
                return true;
            }
            
            if (args.length < 2) {
                sendMessage(player, "&cUsage: /register <password> <confirm-password>");
                return true;
            }
            
            String password = args[0];
            String confirm = args[1];
            
            if (password.length() < 4) {
                sendMessage(player, "&cPassword must be at least 4 characters long!");
                return true;
            }
            
            if (!password.equals(confirm)) {
                sendMessage(player, "&cPasswords do not match!");
                return true;
            }
            
            String hashedPassword = hashPassword(password);
            dataConfig.set("players." + uuid.toString() + ".password", hashedPassword);
            dataConfig.set("players." + uuid.toString() + ".registered-date", System.currentTimeMillis());
            savePlayerData();
            
            registeredPlayers.add(uuid);
            authenticatedPlayers.put(uuid, true);
            
            enablePlayer(player);
            sendMessage(player, "&a&l✔ Successfully registered!");
            sendMessage(player, "&7Your account is now secured.");
            
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("login")) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            
            if (isAuthenticated(player)) {
                sendMessage(player, "&cYou are already authenticated!");
                return true;
            }
            
            if (!isRegistered(uuid)) {
                sendMessage(player, "&cYou are not registered! Use /register <password> <confirm>");
                return true;
            }
            
            if (args.length < 1) {
                sendMessage(player, "&cUsage: /login <password>");
                return true;
            }
            
            String password = args[0];
            String storedHash = dataConfig.getString("players." + uuid.toString() + ".password");
            
            if (hashPassword(password).equals(storedHash)) {
                authenticatedPlayers.put(uuid, true);
                loginAttempts.put(uuid, 0);
                
                enablePlayer(player);
                
                dataConfig.set("players." + uuid.toString() + ".last-login", System.currentTimeMillis());
                savePlayerData();
                
                sendMessage(player, "&a&l✔ Successfully logged in!");
                sendMessage(player, "&7Welcome back to MBTH!");
                
            } else {
                int attempts = loginAttempts.getOrDefault(uuid, 0) + 1;
                loginAttempts.put(uuid, attempts);
                
                if (attempts >= maxAttempts) {
                    player.kickPlayer(ChatColor.RED + "Too many failed login attempts!");
                } else {
                    sendMessage(player, "&c&l✘ Incorrect password!");
                    sendMessage(player, "&7Attempts remaining: &c" + (maxAttempts - attempts));
                }
            }
            
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("changepassword")) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            
            if (!isAuthenticated(player)) {
                sendMessage(player, "&cYou must be logged in to change your password!");
                return true;
            }
            
            if (args.length < 3) {
                sendMessage(player, "&cUsage: /changepassword <old-password> <new-password> <confirm-new>");
                return true;
            }
            
            String oldPassword = args[0];
            String newPassword = args[1];
            String confirmNew = args[2];
            
            String storedHash = dataConfig.getString("players." + uuid.toString() + ".password");
            
            if (!hashPassword(oldPassword).equals(storedHash)) {
                sendMessage(player, "&cIncorrect old password!");
                return true;
            }
            
            if (newPassword.length() < 4) {
                sendMessage(player, "&cNew password must be at least 4 characters long!");
                return true;
            }
            
            if (!newPassword.equals(confirmNew)) {
                sendMessage(player, "&cNew passwords do not match!");
                return true;
            }
            
            dataConfig.set("players." + uuid.toString() + ".password", hashPassword(newPassword));
            savePlayerData();
            
            sendMessage(player, "&a&l✔ Password changed successfully!");
            
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("resetpassword")) {
            return handleResetPassword(sender, args);
        }
        
        if (command.getName().equalsIgnoreCase("freezeaccount")) {
            return handleFreezeAccount(sender, args);
        }
        
        if (command.getName().equalsIgnoreCase("unfreezeaccount")) {
            return handleUnfreezeAccount(sender, args);
        }
        
        if (command.getName().equalsIgnoreCase("forcelogout")) {
            return handleForceLogout(sender, args);
        }
        
        if (command.getName().equalsIgnoreCase("checkalt")) {
            return handleCheckAlt(sender, args);
        }
        
        return false;
    }
    
    private boolean handleResetPassword(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player admin = (Player) sender;
            if (!admin.hasPermission("mbth.admin") && !admin.isOp()) {
                sendMessageToSender(sender, "&c&l✘ You don't have permission to use this command!");
                return true;
            }
        }
        
        if (args.length < 2) {
            sendMessageToSender(sender, "&cUsage: /resetpassword <player> <new-password>");
            return true;
        }
        
        String targetName = args[0];
        String newPassword = args[1];
        
        if (newPassword.length() < 4) {
            sendMessageToSender(sender, "&cPassword must be at least 4 characters long!");
            return true;
        }
        
        Player targetPlayer = Bukkit.getPlayer(targetName);
        UUID targetUUID = null;
        
        if (targetPlayer != null) {
            targetUUID = targetPlayer.getUniqueId();
        } else {
            targetUUID = getUUIDByName(targetName);
        }
        
        if (targetUUID == null) {
            sendMessageToSender(sender, "&c&l✘ Player not found or not registered!");
            sendMessageToSender(sender, "&7Make sure the player has registered before.");
            return true;
        }
        
        dataConfig.set("players." + targetUUID.toString() + ".password", hashPassword(newPassword));
        dataConfig.set("players." + targetUUID.toString() + ".password-reset-by", sender.getName());
        dataConfig.set("players." + targetUUID.toString() + ".password-reset-date", System.currentTimeMillis());
        savePlayerData();
        
        sendMessageToSender(sender, "&a&l✔ Password reset successfully!");
        sendMessageToSender(sender, "&7Player: &e" + targetName);
        sendMessageToSender(sender, "&7New password: &e" + newPassword);
        sendMessageToSender(sender, "&8(Make sure to share this with the player securely)");
        
        if (targetPlayer != null && targetPlayer.isOnline()) {
            authenticatedPlayers.put(targetUUID, false);
            targetPlayer.kickPlayer(ChatColor.translateAlternateColorCodes('&',
                "&6&l✦ MBTH Security ✦\n\n" +
                "&7Your password has been reset by an administrator.\n" +
                "&7New password: &e" + newPassword + "\n\n" +
                "&7Please rejoin and use &a/login " + newPassword + "\n" +
                "&7Then change your password with &a/changepassword"));
        }
        
        getLogger().info("[SECURITY] Password reset for " + targetName + " by " + sender.getName());
        
        return true;
    }
    
    private boolean handleFreezeAccount(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player admin = (Player) sender;
            if (!admin.hasPermission("mbth.admin") && !admin.isOp()) {
                sendMessageToSender(sender, "&c&l✘ You don't have permission to use this command!");
                return true;
            }
        }
        
        if (args.length < 1) {
            sendMessageToSender(sender, "&cUsage: /freezeaccount <player>");
            return true;
        }
        
        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);
        UUID targetUUID = null;
        
        if (targetPlayer != null) {
            targetUUID = targetPlayer.getUniqueId();
        } else {
            targetUUID = getUUIDByName(targetName);
        }
        
        if (targetUUID == null) {
            sendMessageToSender(sender, "&c&l✘ Player not found!");
            return true;
        }
        
        if (frozenPlayers.contains(targetUUID)) {
            sendMessageToSender(sender, "&c&l✘ This account is already frozen!");
            return true;
        }
        
        frozenPlayers.add(targetUUID);
        
        List<String> frozenList = new ArrayList<>();
        for (UUID uuid : frozenPlayers) {
            frozenList.add(uuid.toString());
        }
        dataConfig.set("frozen-players", frozenList);
        dataConfig.set("players." + targetUUID.toString() + ".frozen-by", sender.getName());
        dataConfig.set("players." + targetUUID.toString() + ".frozen-date", System.currentTimeMillis());
        savePlayerData();
        
        sendMessageToSender(sender, "&a&l✔ Account frozen successfully!");
        sendMessageToSender(sender, "&7Player: &e" + targetName);
        
        if (targetPlayer != null && targetPlayer.isOnline()) {
            targetPlayer.kickPlayer(ChatColor.translateAlternateColorCodes('&',
                "&c&l✘ ACCOUNT FROZEN\n\n" +
                "&7Your account has been frozen by an administrator.\n" +
                "&7Please contact server staff for assistance."));
        }
        
        getLogger().info("[SECURITY] Account frozen: " + targetName + " by " + sender.getName());
        
        return true;
    }
    
    private boolean handleUnfreezeAccount(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player admin = (Player) sender;
            if (!admin.hasPermission("mbth.admin") && !admin.isOp()) {
                sendMessageToSender(sender, "&c&l✘ You don't have permission to use this command!");
                return true;
            }
        }
        
        if (args.length < 1) {
            sendMessageToSender(sender, "&cUsage: /unfreezeaccount <player>");
            return true;
        }
        
        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);
        UUID targetUUID = null;
        
        if (targetPlayer != null) {
            targetUUID = targetPlayer.getUniqueId();
        } else {
            targetUUID = getUUIDByName(targetName);
        }
        
        if (targetUUID == null) {
            sendMessageToSender(sender, "&c&l✘ Player not found!");
            return true;
        }
        
        if (!frozenPlayers.contains(targetUUID)) {
            sendMessageToSender(sender, "&c&l✘ This account is not frozen!");
            return true;
        }
        
        frozenPlayers.remove(targetUUID);
        
        List<String> frozenList = new ArrayList<>();
        for (UUID uuid : frozenPlayers) {
            frozenList.add(uuid.toString());
        }
        dataConfig.set("frozen-players", frozenList);
        dataConfig.set("players." + targetUUID.toString() + ".unfrozen-by", sender.getName());
        dataConfig.set("players." + targetUUID.toString() + ".unfrozen-date", System.currentTimeMillis());
        savePlayerData();
        
        sendMessageToSender(sender, "&a&l✔ Account unfrozen successfully!");
        sendMessageToSender(sender, "&7Player: &e" + targetName);
        
        getLogger().info("[SECURITY] Account unfrozen: " + targetName + " by " + sender.getName());
        
        return true;
    }
    
    private boolean handleForceLogout(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player admin = (Player) sender;
            if (!admin.hasPermission("mbth.admin") && !admin.isOp()) {
                sendMessageToSender(sender, "&c&l✘ You don't have permission to use this command!");
                return true;
            }
        }
        
        if (args.length < 1) {
            sendMessageToSender(sender, "&cUsage: /forcelogout <player>");
            return true;
        }
        
        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);
        
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            sendMessageToSender(sender, "&c&l✘ Player is not online!");
            return true;
        }
        
        UUID targetUUID = targetPlayer.getUniqueId();
        
        authenticatedPlayers.put(targetUUID, false);
        
        targetPlayer.setWalkSpeed(0);
        targetPlayer.setFlySpeed(0);
        
        if (isRegistered(targetUUID)) {
            sendLoginPrompt(targetPlayer);
        }
        
        sendMessageToSender(sender, "&a&l✔ Player forced to logout!");
        sendMessageToSender(sender, "&7Player: &e" + targetName);
        
        sendMessage(targetPlayer, "&c&l✘ You have been logged out by an administrator!");
        
        getLogger().info("[SECURITY] Force logout: " + targetName + " by " + sender.getName());
        
        return true;
    }
    
    private boolean handleCheckAlt(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player admin = (Player) sender;
            if (!admin.hasPermission("mbth.admin") && !admin.isOp()) {
                sendMessageToSender(sender, "&c&l✘ You don't have permission to use this command!");
                return true;
            }
        }
        
        if (args.length < 1) {
            sendMessageToSender(sender, "&cUsage: /checkalt <player>");
            return true;
        }
        
        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);
        UUID targetUUID = null;
        String ip = null;
        
        if (targetPlayer != null && targetPlayer.isOnline()) {
            targetUUID = targetPlayer.getUniqueId();
            ip = targetPlayer.getAddress().getAddress().getHostAddress();
        } else {
            targetUUID = getUUIDByName(targetName);
            if (targetUUID != null) {
                ip = dataConfig.getString("players." + targetUUID.toString() + ".last-ip");
            }
        }
        
        if (targetUUID == null || ip == null) {
            sendMessageToSender(sender, "&c&l✘ Player not found or no IP data available!");
            return true;
        }
        
        List<UUID> accountsOnIP = ipToAccounts.get(ip);
        
        if (accountsOnIP == null || accountsOnIP.size() <= 1) {
            sendMessageToSender(sender, "&a&l✔ No alt accounts found!");
            sendMessageToSender(sender, "&7Player: &e" + targetName);
            sendMessageToSender(sender, "&7IP: &e" + ip);
            return true;
        }
        
        sendMessageToSender(sender, "&6&l⚠ ALT ACCOUNTS FOUND");
        sendMessageToSender(sender, "&7Player: &e" + targetName);
        sendMessageToSender(sender, "&7IP Address: &e" + ip);
        sendMessageToSender(sender, "&7Total Accounts: &e" + accountsOnIP.size());
        sendMessageToSender(sender, "&7Accounts on this IP:");
        
        for (UUID uuid : accountsOnIP) {
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            if (name != null) {
                boolean isOnline = Bukkit.getPlayer(uuid) != null;
                String status = isOnline ? "&a[ONLINE]" : "&7[OFFLINE]";
                boolean isMain = uuid.equals(accountsOnIP.get(0));
                String type = isMain ? "&e[MAIN]" : "&c[ALT]";
                
                sendMessageToSender(sender, "  " + status + " " + type + " &f" + name);
            }
        }
        
        return true;
    }
    
    private void sendLoginPrompt(Player player) {
        player.sendTitle(
            ChatColor.GOLD + "✦ MBTH Security ✦",
            ChatColor.YELLOW + "Please login to continue",
            10, 999999, 10
        );
        
        sendMessage(player, "");
        sendMessage(player, "&6&m                                                  ");
        sendMessage(player, "");
        sendMessage(player, "&6&l          MBTH LOGIN SECURITY");
        sendMessage(player, "");
        sendMessage(player, "&7Welcome back! Please authenticate to continue.");
        sendMessage(player, "");
        sendMessage(player, "&e➤ &fUse: &a/login <password>");
        sendMessage(player, "");
        sendMessage(player, "&6&m                                                  ");
        sendMessage(player, "");
    }
    
    private void sendRegisterPrompt(Player player) {
        player.sendTitle(
            ChatColor.GOLD + "✦ MBTH Security ✦",
            ChatColor.GREEN + "Please register an account",
            10, 999999, 10
        );
        
        sendMessage(player, "");
        sendMessage(player, "&6&m                                                  ");
        sendMessage(player, "");
        sendMessage(player, "&6&l          MBTH LOGIN SECURITY");
        sendMessage(player, "");
        sendMessage(player, "&7Welcome! Please register to secure your account.");
        sendMessage(player, "");
        sendMessage(player, "&e➤ &fUse: &a/register <password> <confirm-password>");
        sendMessage(player, "&7Password must be at least 4 characters long.");
        sendMessage(player, "");
        sendMessage(player, "&6&m                                                  ");
        sendMessage(player, "");
    }
    
    private void startLoginTimeout(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isAuthenticated(player)) {
                    player.kickPlayer(ChatColor.RED + "Login timeout - You took too long to authenticate!");
                }
            }
        }.runTaskLater(this, loginTimeout * 20L);
    }
    
    private void enablePlayer(Player player) {
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        player.resetTitle();
    }
    
    private boolean isAuthenticated(Player player) {
        return authenticatedPlayers.getOrDefault(player.getUniqueId(), false);
    }
    
    private boolean isRegistered(UUID uuid) {
        return registeredPlayers.contains(uuid);
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void saveSession(UUID uuid) {
        dataConfig.set("players." + uuid.toString() + ".session-end", 
            System.currentTimeMillis() + (sessionDuration * 60 * 1000));
        savePlayerData();
    }
    
    private boolean isSessionValid(UUID uuid) {
        if (!dataConfig.contains("players." + uuid.toString() + ".session-end")) {
            return false;
        }
        
        long sessionEnd = dataConfig.getLong("players." + uuid.toString() + ".session-end");
        return System.currentTimeMillis() < sessionEnd;
    }
    
    private UUID getUUIDByName(String playerName) {
        if (dataConfig.contains("players")) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
                
                if (player != null && player.getName().equalsIgnoreCase(playerName)) {
                    return uuid;
                }
                
                if (Bukkit.getOfflinePlayer(uuid).getName() != null && 
                    Bukkit.getOfflinePlayer(uuid).getName().equalsIgnoreCase(playerName)) {
                    return uuid;
                }
            }
        }
        return null;
    }
    
    private void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    
    private void sendMessageToSender(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}