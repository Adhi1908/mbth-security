package com.mbth.loginsecurity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import java.net.URL;
import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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
    
    // Session Hijacking Prevention
    private final Map<String, UUID> activePlayerNames = new HashMap<>(); // Username -> UUID mapping
    
    // PIN Code System
    private final Map<UUID, Boolean> pinVerified = new HashMap<>();
    private final Map<UUID, Integer> pinAttempts = new HashMap<>();
    private final Set<UUID> playersWithPin = new HashSet<>();
    
    // GUI PIN System
    private final Map<UUID, String> pinInput = new HashMap<>();
    private final Map<UUID, Inventory> pinInventories = new HashMap<>();
    private final Map<UUID, Boolean> pinSetupMode = new HashMap<>(); // Track if vault is in setup mode
    private final Map<UUID, String> loginMethod = new HashMap<>();
    private final Set<UUID> loginChoiceMade = new HashSet<>();
    
    // Boss Bar for auth prompts (always visible!)
    private final Map<UUID, BossBar> authBossBars = new HashMap<>();
    
    private File dataFile;
    private FileConfiguration dataConfig;
    
    private int maxAttempts;
    private int loginTimeout;
    private boolean sessionEnabled;
    private int sessionDuration;
    private boolean altDetectionEnabled;
    
    // PIN Code Configuration
    private boolean pinEnabled;
    private boolean pinRequired;
    private int pinLength;
    private int maxPinAttempts;
    
    // Login Method Configuration
    private boolean allowLoginMethodChoice;
    
    // Server Branding
    private String serverName;
    private String titleMain;
    private String titleSubtitle;
    private String titleScoreboard;
    
    // Customizable Messages
    private String msgWelcomeBack;
    private String msgWelcomeRegistered;
    private String msgLoginSuccess;
    private String msgRegisterSuccess;
    private String msgPinVerified;
    private String msgReloadSuccess;
    private String msgReloadDetails;
    
    // Discord Integration (Webhooks)
    private boolean discordEnabled;
    private String discordLoginWebhook;
    private String discordRegistrationWebhook;
    private boolean logFailedAttempts;
    private String discordUsername;
    private String discordAvatarUrl;
    
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
        
        // Clean up active username tracking
        activePlayerNames.clear();
        getLogger().info("Cleared active username tracking (" + activePlayerNames.size() + " entries)");
        
        // Clean up all boss bars
        for (BossBar bar : authBossBars.values()) {
            bar.removeAll();
            bar.setVisible(false);
        }
        authBossBars.clear();
        
        getLogger().info("MBTH Login Security has been disabled!");
    }
    
    private void loadConfiguration() {
        FileConfiguration config = getConfig();
        maxAttempts = config.getInt("max-login-attempts", 3);
        loginTimeout = config.getInt("login-timeout-seconds", 60);
        sessionEnabled = config.getBoolean("session.enabled", true);
        sessionDuration = config.getInt("session.duration-minutes", 30);
        altDetectionEnabled = config.getBoolean("alt-detection.enabled", true);
        
        // Server Branding
        serverName = config.getString("server-name", "MBTH");
        titleMain = config.getString("titles.main-title", "&6✦ &e&lMBTH Security &6✦");
        titleSubtitle = config.getString("titles.subtitle", "&ePlease login to continue");
        titleScoreboard = config.getString("titles.scoreboard-title", "&6&lMBTH LOGIN SECURITY");
        
        // Customizable Messages
        msgWelcomeBack = config.getString("messages.welcome-back", "&7Welcome back to {server}!");
        msgWelcomeRegistered = config.getString("messages.welcome-registered", "&7Your account is now secured.");
        msgLoginSuccess = config.getString("messages.login-success", "&a&l✔ Successfully logged in!");
        msgRegisterSuccess = config.getString("messages.register-success", "&a&l✔ Successfully registered!");
        msgPinVerified = config.getString("messages.pin-verified", "&a&l✔ PIN verified successfully!");
        msgReloadSuccess = config.getString("messages.reload-success", "&a&l✔ Configuration Reloaded!");
        msgReloadDetails = config.getString("messages.reload-details", "&7All settings have been updated from config.yml");
        
        // PIN Code Configuration
        pinEnabled = config.getBoolean("pin-code.enabled", true);
        pinRequired = config.getBoolean("pin-code.required", false);
        pinLength = config.getInt("pin-code.length", 4);
        maxPinAttempts = config.getInt("pin-code.max-attempts", 3);
        
        // Login Method Configuration
        allowLoginMethodChoice = config.getBoolean("login-method-choice.enabled", true);
        
        // Discord Integration (Webhooks)
        discordEnabled = config.getBoolean("discord.enabled", false);
        discordLoginWebhook = config.getString("discord.login-webhook", "");
        discordRegistrationWebhook = config.getString("discord.registration-webhook", "");
        logFailedAttempts = config.getBoolean("discord.log-failed-attempts", true);
        discordUsername = config.getString("discord.username", "MBTH Security");
        discordAvatarUrl = config.getString("discord.avatar-url", "https://i.imgur.com/AfFp7pu.png");
        
        // Validate webhook URLs if Discord is enabled
        if (discordEnabled) {
            if (discordLoginWebhook.isEmpty() && discordRegistrationWebhook.isEmpty()) {
                getLogger().warning("Discord integration enabled but no webhook URLs configured!");
            discordEnabled = false;
            } else {
                getLogger().info("Discord webhook integration enabled!");
            }
        }
    }
    
    private void loadRegisteredPlayers() {
        if (dataConfig.contains("players")) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                registeredPlayers.add(UUID.fromString(uuidString));
                
                // Load PIN code status
                if (dataConfig.contains("players." + uuidString + ".pin")) {
                    playersWithPin.add(UUID.fromString(uuidString));
                }
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
    
    // ===== CRITICAL: SESSION HIJACKING PREVENTION =====
    // This event fires BEFORE PlayerJoinEvent and BEFORE Minecraft kicks the original player
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        String username = event.getName();
        UUID incomingUUID = event.getUniqueId();
        String incomingIP = event.getAddress().getHostAddress();
        
        getLogger().info("[PRE-LOGIN CHECK] " + username + " attempting to join (UUID: " + incomingUUID + ", IP: " + incomingIP + ")");
        
        // Check if this username is already online
        UUID existingUUID = activePlayerNames.get(username);
        
        if (existingUUID != null) {
            // Someone with this username is already online
            Player existingPlayer = Bukkit.getPlayer(existingUUID);
            
            if (existingPlayer != null && existingPlayer.isOnline()) {
                String existingIP = existingPlayer.getAddress().getAddress().getHostAddress();
                
                // CRITICAL: Block the new connection to prevent session hijacking
                getLogger().severe("========================================");
                getLogger().severe("⚠️  SESSION HIJACKING ATTEMPT BLOCKED!");
                getLogger().severe("========================================");
                getLogger().severe("Username: " + username);
                getLogger().severe("Existing UUID: " + existingUUID);
                getLogger().severe("Existing IP: " + existingIP);
                getLogger().severe("Attacker UUID: " + incomingUUID);
                getLogger().severe("Attacker IP: " + incomingIP);
                getLogger().severe("========================================");
                
                // Alert the existing player in-game
                existingPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "\n&c&l⚠️  SECURITY ALERT ⚠️"));
                existingPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&7Someone tried to join with your username!"));
                existingPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&7Attacker IP: &c" + incomingIP));
                existingPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&7The connection was &a✔ BLOCKED &7by security."));
                existingPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&7If you're on a cracked server, someone may know your username."));
                existingPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&7Consider using &e/changepassword &7for extra security.\n"));
                
                // Alert all online admins
                for (Player admin : Bukkit.getOnlinePlayers()) {
                    if (admin.hasPermission("mbth.admin") || admin.isOp()) {
                        admin.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "\n&c&l⚠️  SESSION HIJACKING BLOCKED!"));
                        admin.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&7Username: &e" + username));
                        admin.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&7Existing IP: &a" + existingIP + " &7(Protected)"));
                        admin.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&7Attacker IP: &c" + incomingIP + " &7(Blocked)\n"));
                    }
                }
                
                // Discord webhook notification
                sendSecurityNotification(
                    "🚨 **SESSION HIJACKING BLOCKED!**\n\n" +
                    "**Username:** `" + username + "`\n" +
                    "**Existing Player:**\n" +
                    "  • UUID: `" + existingUUID + "`\n" +
                    "  • IP: `" + existingIP + "`\n\n" +
                    "**Attacker Blocked:**\n" +
                    "  • UUID: `" + incomingUUID + "`\n" +
                    "  • IP: `" + incomingIP + "`\n\n" +
                    "✅ **Connection was blocked before the original player could be kicked.**"
                );
                
                // BLOCK the new connection with a detailed message
                event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    ChatColor.translateAlternateColorCodes('&',
                        "&c&l⚠️  CONNECTION BLOCKED ⚠️\n\n" +
                        "&7A player with the username &e" + username + " &7is already online.\n\n" +
                        "&cThis server is protected against session hijacking.\n\n" +
                        "&7If this is your account:\n" +
                        "&7  1. Wait for the other session to disconnect\n" +
                        "&7  2. If you were just playing, you may have been disconnected\n" +
                        "&7  3. Contact server staff if this persists\n\n" +
                        "&7If this is NOT your account:\n" +
                        "&c  • You cannot join with a username that's already in use\n" +
                        "&c  • This attempt has been logged and reported to staff\n\n" +
                        "&8Your IP: " + incomingIP)
                );
                
                return; // Block the connection
            } else {
                // The UUID is in our map but player is not online anymore
                // This can happen if there was a dirty disconnect
                // Clean up the stale entry
                getLogger().info("[PRE-LOGIN] Cleaning up stale username entry for " + username);
                activePlayerNames.remove(username);
            }
        }
        
        // Allow the connection - we'll track it in PlayerJoinEvent
        getLogger().info("[PRE-LOGIN] ✔ Allowing connection for " + username);
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
        String username = player.getName();
        String ip = player.getAddress().getAddress().getHostAddress();
        
        getLogger().info("[JOIN] Player " + username + " joined successfully (UUID: " + uuid + ")");
        
        // Register this player's username in our active tracking map
        // This prevents future duplicate username connections (session hijacking)
        activePlayerNames.put(username, uuid);
        getLogger().info("[SESSION] Registered active username: " + username + " -> " + uuid);
        
        // All players must authenticate (no premium bypass)
        getLogger().info("[AUTH] " + username + " requires authentication");
        continueJoinProcess(player, uuid, ip);
    }
    
    // Continue the join process (separated for async calls)
    private void continueJoinProcess(Player player, UUID uuid, String ip) {
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
            pinVerified.put(uuid, true);
            removeAuthBossBar(uuid); // Remove boss bar on auth
            sendMessage(player, "&a&lWelcome back! &7Session restored.");
            return;
        }
        
        authenticatedPlayers.put(uuid, false);
        
        player.setWalkSpeed(0);
        player.setFlySpeed(0);
        
        if (isRegistered(uuid)) {
            if (allowLoginMethodChoice) {
                // Always show login method choice if enabled
                sendLoginMethodChoice(player);
            } else {
                sendLoginPrompt(player);
            }
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
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String username = player.getName();
        
        if (sessionEnabled && authenticatedPlayers.getOrDefault(uuid, false)) {
            saveSession(uuid);
        }
        
        // CRITICAL: Remove username from active tracking to allow reconnection
        activePlayerNames.remove(username);
        getLogger().info("[SESSION] Unregistered username on quit: " + username);
        
        // Clean up boss bar
        removeAuthBossBar(uuid);
        
        authenticatedPlayers.remove(uuid);
        loginLocations.remove(uuid);
        loginAttempts.remove(uuid);
        pinVerified.remove(uuid);
        pinAttempts.remove(uuid);
        pinInput.remove(uuid);
        pinInventories.remove(uuid);
        pinSetupMode.remove(uuid);
        loginMethod.remove(uuid);
        loginChoiceMade.remove(uuid);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isFullyAuthenticated(player)) {
            Location from = event.getFrom();
            Location to = event.getTo();
            
            if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!isFullyAuthenticated(event.getPlayer())) {
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
        } else if (!isPinVerified(player)) {
            if (!cmd.startsWith("/verifypin") && !cmd.startsWith("/setuppin")) {
                event.setCancelled(true);
                sendMessage(player, "&cPlease verify your PIN first!");
            }
        }
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!isFullyAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            UUID uuid = player.getUniqueId();
            
            // Handle PIN vault GUI clicks
            if (pinInventories.containsKey(uuid) && event.getInventory().equals(pinInventories.get(uuid))) {
                event.setCancelled(true);
                handlePinVaultClick(player, event);
                return;
            }
            
            // Handle login method choice GUI clicks
            if (event.getView().getTitle().contains("Choose Login Method")) {
                event.setCancelled(true);
                handleLoginMethodClick(player, event);
                return;
            }
            
            if (!isFullyAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            UUID uuid = player.getUniqueId();
            
            // Check if it's the login method choice menu being closed
            String title = event.getView().getTitle();
            if (title.contains("Choose Login Method")) {
                // Only reopen if they didn't make a choice AND they're not authenticated
                if (!loginChoiceMade.contains(uuid) && !isAuthenticated(player) && !isFullyAuthenticated(player)) {
                    // Reopen login method choice if they close it without selecting
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!isAuthenticated(player) && player.isOnline() && !loginChoiceMade.contains(uuid)) {
                                sendLoginMethodChoice(player);
                            }
                        }
                    }.runTaskLater(this, 10L); // Increased delay to prevent instant reopen
                }
                return;
            }
            
            // Reopen PIN vault if player tries to close it without completing
            if (pinInventories.containsKey(uuid) && !isFullyAuthenticated(player)) {
                Inventory vault = pinInventories.get(uuid);
                if (vault != null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!isFullyAuthenticated(player) && player.isOnline() && pinInventories.containsKey(uuid)) {
                                Inventory currentVault = pinInventories.get(uuid);
                                if (currentVault != null) {
                                    player.openInventory(currentVault);
                                }
                            }
                        }
                    }.runTaskLater(this, 10L); // Increased delay
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isFullyAuthenticated(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && !command.getName().equalsIgnoreCase("resetpassword") 
            && !command.getName().equalsIgnoreCase("freezeaccount") 
            && !command.getName().equalsIgnoreCase("unfreezeaccount")
            && !command.getName().equalsIgnoreCase("forcelogout")
            && !command.getName().equalsIgnoreCase("checkalt")
            && !command.getName().equalsIgnoreCase("resetpin")
            && !command.getName().equalsIgnoreCase("unregister")
            && !command.getName().equalsIgnoreCase("mbthlsreload")) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("register")) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            
            getLogger().info("=== REGISTER COMMAND START for " + player.getName() + " ===");
            getLogger().info("Current state - Auth: " + isAuthenticated(player) + " Registered: " + isRegistered(uuid));
            
            if (isAuthenticated(player)) {
                sendMessage(player, "&cYou are already authenticated!");
                getLogger().info("Register blocked: already authenticated");
                return true;
            }
            
            if (isRegistered(uuid)) {
                sendMessage(player, "&cYou are already registered! Use /login <password>");
                getLogger().info("Register blocked: already registered");
                return true;
            }
            
            if (args.length < 2) {
                sendMessage(player, "&cUsage: /register <password> <confirm-password>");
                getLogger().info("Register blocked: not enough args");
                return true;
            }
            
            String password = args[0];
            String confirm = args[1];
            
            if (password.length() < 4) {
                sendMessage(player, "&cPassword must be at least 4 characters long!");
                getLogger().info("Register blocked: password too short");
                return true;
            }
            
            if (!password.equals(confirm)) {
                sendMessage(player, "&cPasswords do not match!");
                getLogger().info("Register blocked: passwords don't match");
                return true;
            }
            
            getLogger().info("Proceeding with registration...");
            
            String hashedPassword = hashPassword(password);
            dataConfig.set("players." + uuid.toString() + ".password", hashedPassword);
            dataConfig.set("players." + uuid.toString() + ".registered-date", System.currentTimeMillis());
            savePlayerData();
            
            getLogger().info("Data saved. Setting flags...");
            
            registeredPlayers.add(uuid);
            authenticatedPlayers.put(uuid, true);
            pinVerified.put(uuid, false); // PIN not verified yet - must set up PIN
            removeAuthBossBar(uuid); // Remove boss bar on successful registration
            
            getLogger().info("Flags set.");
            
            // Mandatory PIN setup
            if (pinEnabled && pinRequired) {
                player.setWalkSpeed(0);
                player.setFlySpeed(0);
                
                player.sendMessage(formatMessage(msgRegisterSuccess));
                player.sendMessage(formatMessage(msgWelcomeRegistered));
                sendMessage(player, "");
                sendMessage(player, "&6&l⚠ PIN REQUIRED");
                sendMessage(player, "&7You must setup a PIN to secure your account.");
                sendMessage(player, "&7Opening PIN Vault...");
                sendMessage(player, "");
                
                // Open PIN vault after a short delay
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isOnline() && !hasPin(uuid)) {
                            openPinVault(player, true); // true = setup mode
                        }
                    }
                }.runTaskLater(this, 20L); // 1 second delay
            } else {
                pinVerified.put(uuid, true);
                enablePlayer(player);
                
                player.sendMessage(formatMessage(msgRegisterSuccess));
                player.sendMessage(formatMessage(msgWelcomeRegistered));
            }
            
            getLogger().info("=== REGISTER COMPLETE ===");
            getLogger().info("  authenticatedPlayers.get: " + authenticatedPlayers.get(uuid));
            getLogger().info("  pinVerified.get: " + pinVerified.get(uuid));
            getLogger().info("  PIN Required: " + pinRequired);
            
            // Discord notification
            sendRegistrationNotification("🆕 **" + player.getName() + "** registered a new account!");
            
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
                loginAttempts.put(uuid, 0);
                
                // Password login = Full authentication, no PIN needed
                authenticatedPlayers.put(uuid, true);
                pinVerified.put(uuid, true);
                removeAuthBossBar(uuid); // Remove boss bar on auth
                
                enablePlayer(player);
                
                dataConfig.set("players." + uuid.toString() + ".last-login", System.currentTimeMillis());
                savePlayerData();
                
                // Debug info (console only)
                getLogger().info("Player " + player.getName() + " logged in successfully with PASSWORD");
                getLogger().info("  authenticatedPlayers: " + authenticatedPlayers.getOrDefault(uuid, false));
                getLogger().info("  pinVerified: " + pinVerified.getOrDefault(uuid, false));
                getLogger().info("  isAuthenticated: " + isAuthenticated(player));
                getLogger().info("  isPinVerified: " + isPinVerified(player));
                getLogger().info("  isFullyAuthenticated: " + isFullyAuthenticated(player));
                
                player.sendMessage(formatMessage(msgLoginSuccess));
                player.sendMessage(formatMessage(msgWelcomeBack));
                
                // Discord notification
                sendLoginNotification("🔓 **" + player.getName() + "** logged in successfully (Password)");
            } else {
                int attempts = loginAttempts.getOrDefault(uuid, 0) + 1;
                loginAttempts.put(uuid, attempts);
                
                if (attempts >= maxAttempts) {
                    player.kickPlayer(ChatColor.RED + "Too many failed login attempts!");
                    // Discord notification for failed attempts
                    if (logFailedAttempts) {
                        sendSecurityNotification("⚠️ **" + player.getName() + "** was kicked for too many failed login attempts!");
                    }
                } else {
                    sendMessage(player, "&c&l✘ Incorrect password!");
                    sendMessage(player, "&7Attempts remaining: &c" + (maxAttempts - attempts));
                    // Discord notification for failed attempts
                    if (logFailedAttempts) {
                        sendSecurityNotification("❌ **" + player.getName() + "** failed login attempt (" + attempts + "/" + maxAttempts + ")");
                    }
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
        
        if (command.getName().equalsIgnoreCase("setuppin")) {
            return handleSetupPin(sender, args);
        }
        
        if (command.getName().equalsIgnoreCase("verifypin")) {
            return handleVerifyPin(sender, args);
        }
        
        if (command.getName().equalsIgnoreCase("changepin")) {
            return handleChangePin(sender, args);
        }
        
        if (command.getName().equalsIgnoreCase("resetpin")) {
            return handleResetPin(sender, args);
        }
        
        if (command.getName().equalsIgnoreCase("resetpassword")) {
            return handleResetPassword(sender, args);
        }
        
        if (command.getName().equalsIgnoreCase("unregister")) {
            return handleUnregister(sender, args);
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
        
        if (command.getName().equalsIgnoreCase("mbthlsreload")) {
            return handleReload(sender);
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
    
    private boolean handleUnregister(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player admin = (Player) sender;
            if (!admin.hasPermission("mbth.admin") && !admin.isOp()) {
                sendMessageToSender(sender, "&c&l✘ You don't have permission to use this command!");
                return true;
            }
        }
        
        if (args.length < 1) {
            sendMessageToSender(sender, "&cUsage: /unregister <player>");
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
        
        if (!isRegistered(targetUUID)) {
            sendMessageToSender(sender, "&c&l✘ This player is not registered!");
            return true;
        }
        
        // Remove from all tracking systems
        registeredPlayers.remove(targetUUID);
        playersWithPin.remove(targetUUID);
        authenticatedPlayers.remove(targetUUID);
        pinVerified.remove(targetUUID);
        frozenPlayers.remove(targetUUID);
        
        // Remove player data from config
        dataConfig.set("players." + targetUUID.toString(), null);
        
        // Update frozen players list if they were frozen
        List<String> frozenList = new ArrayList<>();
        for (UUID uuid : frozenPlayers) {
            frozenList.add(uuid.toString());
        }
        dataConfig.set("frozen-players", frozenList);
        
        // Remove from IP tracking (keep the IP record but remove this player from it)
        if (dataConfig.contains("ip-tracking")) {
            for (String ip : dataConfig.getConfigurationSection("ip-tracking").getKeys(false)) {
                List<String> uuidStrings = dataConfig.getStringList("ip-tracking." + ip);
                uuidStrings.remove(targetUUID.toString());
                
                if (uuidStrings.isEmpty()) {
                    // Remove IP entry if no more accounts
                    dataConfig.set("ip-tracking." + ip, null);
                    ipToAccounts.remove(ip.replace("_", "."));
                } else {
                    // Update the list
                    dataConfig.set("ip-tracking." + ip, uuidStrings);
                    List<UUID> updatedUUIDs = new ArrayList<>();
                    for (String uuidStr : uuidStrings) {
                        updatedUUIDs.add(UUID.fromString(uuidStr));
                    }
                    ipToAccounts.put(ip.replace("_", "."), updatedUUIDs);
                }
            }
        }
        
        savePlayerData();
        
        sendMessageToSender(sender, "&a&l✔ Player unregistered successfully!");
        sendMessageToSender(sender, "&7Player: &e" + targetName);
        sendMessageToSender(sender, "&7All account data has been deleted.");
        
        if (targetPlayer != null && targetPlayer.isOnline()) {
            targetPlayer.kickPlayer(ChatColor.translateAlternateColorCodes('&',
                "&6&l✦ MBTH Security ✦\n\n" +
                "&7Your account has been unregistered by an administrator.\n" +
                "&7You can register a new account by rejoining the server.\n\n" +
                "&7Use: &a/register <password> <confirm>"));
        }
        
        getLogger().info("[SECURITY] Account unregistered: " + targetName + " by " + sender.getName());
        
        // Discord notification
        sendSecurityNotification("🗑️ **" + targetName + "'s** account was unregistered by " + sender.getName());
        
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
    
    private boolean handleSetupPin(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        
        if (!isAuthenticated(player)) {
            sendMessage(player, "&cYou must be logged in first!");
            return true;
        }
        
        if (!pinEnabled) {
            sendMessage(player, "&cPIN code system is currently disabled!");
            return true;
        }
        
        if (hasPin(uuid) && isPinVerified(player)) {
            sendMessage(player, "&cYou already have a PIN! Use /changepin to modify it.");
            return true;
        }
        
        if (args.length < 2) {
            sendMessage(player, "&cUsage: /setuppin <pin> <confirm-pin>");
            sendMessage(player, "&7PIN must be " + pinLength + " digits.");
            return true;
        }
        
        String pin = args[0];
        String confirm = args[1];
        
        if (!isValidPin(pin)) {
            sendMessage(player, "&cPIN must be exactly " + pinLength + " digits!");
            return true;
        }
        
        if (!pin.equals(confirm)) {
            sendMessage(player, "&cPINs do not match!");
            return true;
        }
        
        String hashedPin = hashPassword(pin);
        dataConfig.set("players." + uuid.toString() + ".pin", hashedPin);
        dataConfig.set("players." + uuid.toString() + ".pin-setup-date", System.currentTimeMillis());
        savePlayerData();
        
        playersWithPin.add(uuid);
        pinVerified.put(uuid, true);
        
        enablePlayer(player);
        
        sendMessage(player, "&a&l✔ PIN code setup successfully!");
        sendMessage(player, "&7Your account now has an extra layer of security.");
        sendMessage(player, "&7You will be asked for this PIN after logging in.");
        
        return true;
    }
    
    private boolean handleVerifyPin(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        
        if (!isAuthenticated(player)) {
            sendMessage(player, "&cYou must be logged in first!");
            return true;
        }
        
        if (isPinVerified(player)) {
            sendMessage(player, "&cYou are already verified!");
            return true;
        }
        
        if (!hasPin(uuid)) {
            sendMessage(player, "&cYou don't have a PIN setup!");
            return true;
        }
        
        if (args.length < 1) {
            sendMessage(player, "&cUsage: /verifypin <pin>");
            return true;
        }
        
        String pin = args[0];
        String storedHash = dataConfig.getString("players." + uuid.toString() + ".pin");
        
        if (hashPassword(pin).equals(storedHash)) {
            pinVerified.put(uuid, true);
            pinAttempts.put(uuid, 0);
            
            enablePlayer(player);
            
            dataConfig.set("players." + uuid.toString() + ".last-login", System.currentTimeMillis());
            savePlayerData();
            
            player.sendMessage(formatMessage(msgPinVerified));
            player.sendMessage(formatMessage(msgWelcomeBack));
            
            // Discord notification
            sendLoginNotification("🔓 **" + player.getName() + "** verified PIN successfully");
            
        } else {
            int attempts = pinAttempts.getOrDefault(uuid, 0) + 1;
            pinAttempts.put(uuid, attempts);
            
            if (attempts >= maxPinAttempts) {
                player.kickPlayer(ChatColor.RED + "Too many failed PIN attempts!");
            } else {
                sendMessage(player, "&c&l✘ Incorrect PIN!");
                sendMessage(player, "&7Attempts remaining: &c" + (maxPinAttempts - attempts));
            }
        }
        
        return true;
    }
    
    private boolean handleChangePin(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        
        if (!isFullyAuthenticated(player)) {
            sendMessage(player, "&cYou must be fully authenticated to change your PIN!");
            return true;
        }
        
        if (!hasPin(uuid)) {
            sendMessage(player, "&cYou don't have a PIN setup! Use /setuppin first.");
            return true;
        }
        
        if (args.length < 3) {
            sendMessage(player, "&cUsage: /changepin <old-pin> <new-pin> <confirm-new>");
            return true;
        }
        
        String oldPin = args[0];
        String newPin = args[1];
        String confirmNew = args[2];
        
        String storedHash = dataConfig.getString("players." + uuid.toString() + ".pin");
        
        if (!hashPassword(oldPin).equals(storedHash)) {
            sendMessage(player, "&cIncorrect old PIN!");
            return true;
        }
        
        if (!isValidPin(newPin)) {
            sendMessage(player, "&cNew PIN must be exactly " + pinLength + " digits!");
            return true;
        }
        
        if (!newPin.equals(confirmNew)) {
            sendMessage(player, "&cNew PINs do not match!");
            return true;
        }
        
        dataConfig.set("players." + uuid.toString() + ".pin", hashPassword(newPin));
        dataConfig.set("players." + uuid.toString() + ".pin-changed-date", System.currentTimeMillis());
        savePlayerData();
        
        sendMessage(player, "&a&l✔ PIN changed successfully!");
        
        return true;
    }
    
    private boolean handleResetPin(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player admin = (Player) sender;
            if (!admin.hasPermission("mbth.admin") && !admin.isOp()) {
                sendMessageToSender(sender, "&c&l✘ You don't have permission to use this command!");
                return true;
            }
        }
        
        if (args.length < 1) {
            sendMessageToSender(sender, "&cUsage: /resetpin <player>");
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
        
        if (!hasPin(targetUUID)) {
            sendMessageToSender(sender, "&c&l✘ This player doesn't have a PIN setup!");
            return true;
        }
        
        dataConfig.set("players." + targetUUID.toString() + ".pin", null);
        dataConfig.set("players." + targetUUID.toString() + ".pin-reset-by", sender.getName());
        dataConfig.set("players." + targetUUID.toString() + ".pin-reset-date", System.currentTimeMillis());
        savePlayerData();
        
        playersWithPin.remove(targetUUID);
        
        sendMessageToSender(sender, "&a&l✔ PIN reset successfully!");
        sendMessageToSender(sender, "&7Player: &e" + targetName);
        sendMessageToSender(sender, "&7The player's PIN has been removed.");
        
        if (targetPlayer != null && targetPlayer.isOnline()) {
            sendMessage(targetPlayer, "&6&l⚠ Your PIN has been reset by an administrator!");
            sendMessage(targetPlayer, "&7Use /setuppin to create a new PIN.");
        }
        
        getLogger().info("[SECURITY] PIN reset for " + targetName + " by " + sender.getName());
        
        return true;
    }
    
    private void sendLoginPrompt(Player player) {
        UUID uuid = player.getUniqueId();
        
        player.sendTitle(
            ChatColor.translateAlternateColorCodes('&', titleMain),
            ChatColor.translateAlternateColorCodes('&', titleSubtitle),
            10, 999999, 10
        );
        
        // Create RAINBOW BOSS BAR - Always visible at top of screen!
        BossBar bossBar = Bukkit.createBossBar(
            ChatColor.translateAlternateColorCodes('&', "&e⚠ &fPlease login: &a/login <password>"),
            BarColor.RED,
            BarStyle.SOLID
        );
        bossBar.setProgress(1.0);
        bossBar.setVisible(true);
        bossBar.addPlayer(player);
        authBossBars.put(uuid, bossBar);
        
        // Start rainbow color animation
        startRainbowBossBar(uuid, bossBar);
        
        // Delay messages slightly to ensure they appear after player is fully loaded
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (!player.isOnline()) return;
        
        sendMessage(player, "");
        sendMessage(player, "&6&m                                                  ");
        sendMessage(player, "");
        sendMessage(player, titleScoreboard);
        sendMessage(player, "");
        sendMessage(player, "&7Welcome back! Please authenticate to continue.");
        sendMessage(player, "");
        sendMessage(player, "&e➤ &fUse: &a/login <password>");
        sendMessage(player, "");
        sendMessage(player, "&6&m                                                  ");
        sendMessage(player, "");
        }, 10L); // 0.5 second delay
        
        // Send repeated action bar messages so player always sees it
        sendRepeatingActionBar(player, "&e⚠ Please login: /login <password>");
    }
    
    private void sendRegisterPrompt(Player player) {
        UUID uuid = player.getUniqueId();
        
        player.sendTitle(
            ChatColor.translateAlternateColorCodes('&', titleMain),
            ChatColor.translateAlternateColorCodes('&', "&aPlease register an account"),
            10, 999999, 10
        );
        
        // Create RAINBOW BOSS BAR - Always visible at top of screen!
        BossBar bossBar = Bukkit.createBossBar(
            ChatColor.translateAlternateColorCodes('&', "&e⚠ &fPlease register: &a/register <password> <confirm>"),
            BarColor.PINK,
            BarStyle.SOLID
        );
        bossBar.setProgress(1.0);
        bossBar.setVisible(true);
        bossBar.addPlayer(player);
        authBossBars.put(uuid, bossBar);
        
        // Start rainbow color animation
        startRainbowBossBar(uuid, bossBar);
        
        // Delay messages slightly to ensure they appear after player is fully loaded
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (!player.isOnline()) return;
        
        sendMessage(player, "");
        sendMessage(player, "&6&m                                                  ");
        sendMessage(player, "");
        sendMessage(player, titleScoreboard);
        sendMessage(player, "");
        sendMessage(player, "&7Welcome! Please register to secure your account.");
        sendMessage(player, "");
        sendMessage(player, "&e➤ &fUse: &a/register <password> <confirm-password>");
        sendMessage(player, "&7Password must be at least 4 characters long.");
        sendMessage(player, "");
        sendMessage(player, "&6&m                                                  ");
        sendMessage(player, "");
        }, 10L); // 0.5 second delay
        
        // Send repeated action bar messages so player always sees it
        sendRepeatingActionBar(player, "&e⚠ Please register: /register <password> <confirm>");
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
    
    // PIN Code Helper Methods
    private boolean hasPin(UUID uuid) {
        return playersWithPin.contains(uuid);
    }
    
    private boolean isPinVerified(Player player) {
        return pinVerified.getOrDefault(player.getUniqueId(), true);
    }
    
    private boolean isFullyAuthenticated(Player player) {
        return isAuthenticated(player) && isPinVerified(player);
    }
    
    private boolean isValidPin(String pin) {
        if (pin.length() != pinLength) {
            return false;
        }
        for (char c : pin.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    
    private void sendPinPrompt(Player player) {
        player.sendTitle(
            ChatColor.translateAlternateColorCodes('&', titleMain),
            ChatColor.translateAlternateColorCodes('&', "&ePlease enter your PIN"),
            10, 999999, 10
        );
        
        sendMessage(player, "");
        sendMessage(player, "&6&m                                                  ");
        sendMessage(player, "");
        sendMessage(player, "&6&l          PIN VERIFICATION REQUIRED");
        sendMessage(player, "");
        sendMessage(player, "&7Please enter your " + pinLength + "-digit PIN to continue.");
        sendMessage(player, "");
        sendMessage(player, "&e➤ &fUse: &a/verifypin <pin>");
        sendMessage(player, "");
        sendMessage(player, "&6&m                                                  ");
        sendMessage(player, "");
    }
    
    private void sendPinSetupPrompt(Player player) {
        player.sendTitle(
            ChatColor.translateAlternateColorCodes('&', titleMain),
            ChatColor.translateAlternateColorCodes('&', "&aCreate your security PIN"),
            10, 999999, 10
        );
        
        sendMessage(player, "");
        sendMessage(player, "&6&m                                                  ");
        sendMessage(player, "");
        sendMessage(player, "&6&l          PIN SETUP REQUIRED");
        sendMessage(player, "");
        sendMessage(player, "&7This server requires a PIN for additional security.");
        sendMessage(player, "&7Please create a " + pinLength + "-digit PIN code.");
        sendMessage(player, "");
        sendMessage(player, "&e➤ &fUse: &a/setuppin <pin> <confirm-pin>");
        sendMessage(player, "&7Example: &a/setuppin 1234 1234");
        sendMessage(player, "");
        sendMessage(player, "&6&m                                                  ");
        sendMessage(player, "");
    }
    
    // Login Method Choice GUI
    private void sendLoginMethodChoice(Player player) {
        UUID uuid = player.getUniqueId();
        boolean hasPin = hasPin(uuid);
        
        // Clear the choice flag when opening menu
        loginChoiceMade.remove(uuid);
        
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "✦ Choose Login Method ✦");
        
        // PIN Login Option
        ItemStack pinItem;
        ItemMeta pinMeta;
        
        if (hasPin) {
            // Player has PIN - can login with it
            pinItem = new ItemStack(Material.IRON_DOOR);
            pinMeta = pinItem.getItemMeta();
            pinMeta.setDisplayName(ChatColor.GREEN + "Login with PIN");
            pinMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Use your secure PIN code",
                ChatColor.GRAY + "to login quickly",
                "",
                ChatColor.YELLOW + "✔ Click to select"
            ));
        } else {
            // Player doesn't have PIN - show setup option
            pinItem = new ItemStack(Material.IRON_DOOR);
            pinMeta = pinItem.getItemMeta();
            pinMeta.setDisplayName(ChatColor.GOLD + "Setup PIN (Optional)");
            pinMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Create a PIN for faster",
                ChatColor.GRAY + "login next time",
                "",
                ChatColor.YELLOW + "Click to setup",
                ChatColor.DARK_GRAY + "(You can skip this)"
            ));
        }
        pinItem.setItemMeta(pinMeta);
        
        // Password Login Option
        ItemStack passItem = new ItemStack(Material.PAPER);
        ItemMeta passMeta = passItem.getItemMeta();
        passMeta.setDisplayName(ChatColor.AQUA + "Login with Password");
        passMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Use your account password",
            ChatColor.GRAY + "to login",
            "",
            ChatColor.YELLOW + "✔ Click to select"
        ));
        passItem.setItemMeta(passMeta);
        
        // Decorative items
        ItemStack glass = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        // Fill with glass
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, glass);
        }
        
        // Set login options
        inv.setItem(11, pinItem);
        inv.setItem(15, passItem);
        
        player.openInventory(inv);
    }
    
    private void handleLoginMethodClick(Player player, InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        
        UUID uuid = player.getUniqueId();
        ItemStack clicked = event.getCurrentItem();
        
        if (clicked.getType() == Material.IRON_DOOR) {
            // Player chose PIN option
            loginChoiceMade.add(uuid); // Mark that choice was made
            loginMethod.put(uuid, "PIN");
            player.closeInventory();
            
            if (hasPin(uuid)) {
                // Has PIN - open vault to login
                openPinVault(player, false);
            } else {
                // No PIN - prompt to setup
                sendMessage(player, "");
                sendMessage(player, "&6&l✦ PIN SETUP");
                sendMessage(player, "&7You chose to setup a PIN for faster login.");
                sendMessage(player, "&7Use: &a/setuppin <pin> <confirm>");
                sendMessage(player, "&7Example: &a/setuppin 1234 1234");
                sendMessage(player, "");
                sendMessage(player, "&7Or use &e/login <password> &7to login with password instead.");
                sendMessage(player, "");
            }
        } else if (clicked.getType() == Material.PAPER) {
            // Player chose Password login
            loginChoiceMade.add(uuid); // Mark that choice was made
            loginMethod.put(uuid, "PASSWORD");
            player.closeInventory();
            sendLoginPrompt(player);
        }
    }
    
    // Helper method to create textured skull using texture URL directly
    private ItemStack createTexturedSkull(String textureUrl, String displayName, List<String> lore) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        
        try {
            // Create player profile with texture
            PlayerProfile profile = Bukkit.createPlayerProfile(java.util.UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(new URL(textureUrl));
            profile.setTextures(textures);
            meta.setOwnerProfile(profile);
        } catch (Exception e) {
            getLogger().warning("Failed to set skull texture: " + e.getMessage());
        }
        
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        
        if (lore != null && !lore.isEmpty()) {
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(coloredLore);
        }
        
        skull.setItemMeta(meta);
        return skull;
    }
    
    // PIN Vault GUI
    private void openPinVault(Player player, boolean isSetup) {
        java.util.UUID uuid = player.getUniqueId();
        pinInput.put(uuid, "");
        pinSetupMode.put(uuid, isSetup);
        
        Inventory vault = Bukkit.createInventory(null, 54, ChatColor.GOLD + "✦ " + (isSetup ? "Setup" : "Enter") + " PIN Vault ✦");
        
        // Balloon number head texture URLs (Blue balloons from minecraft-heads.com)
        String[] balloonTextures = {
            "http://textures.minecraft.net/texture/bdb01a3077562843090e030ee7b4a8634e6fc2d53e4603a34f28f5f2adc3371d", // 0
            "http://textures.minecraft.net/texture/7e69b5cfebe05dd43050277e5be83c807b45ade30e08291f6867c723e854b482", // 1
            "http://textures.minecraft.net/texture/43dbcd32b55e8bbc363a7497f0757638a7bb078be07f362d11128cf4288a6bcc", // 2
            "http://textures.minecraft.net/texture/3c29e477401889c9e263cd49ec4c98805401499b27c954614201018b9170076d", // 3
            "http://textures.minecraft.net/texture/4d86f0a05dc6fa76bc46c4e3c121b4e0b95178228084747cad67e51d9c5e6bab", // 4
            "http://textures.minecraft.net/texture/93f1c35240d4df8a2e5f8159347a1d42fb7bdd50c90de95af306841825d72bbd", // 5
            "http://textures.minecraft.net/texture/4f1ba5599722dba61d16f75961fe094de451d51b7ddf087bdce41ef93196d816", // 6
            "http://textures.minecraft.net/texture/108b88efa0fb1fd23013aa136acc1af44bded5f3ae417ef7f6d47b4f2c4550a3", // 7
            "http://textures.minecraft.net/texture/915fd1b7c63773f9324536e48cbd0ac101e2c18cabaa55993c55db02a86e196a", // 8
            "http://textures.minecraft.net/texture/8143f22c09c88c41fb0c4d6ff24f497d8c286345a0df6998a050cf9759a72d1a"  // 9
        };
        
        // Glass pane colors matching balloon colors
        Material[] glassPaneColors = {
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,  // 0 - Light Blue
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,  // 1 - Light Blue
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,  // 2 - Light Blue  
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,  // 3 - Light Blue
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,  // 4 - Light Blue
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,  // 5 - Light Blue
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,  // 6 - Light Blue
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,  // 7 - Light Blue
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,  // 8 - Light Blue
            Material.LIGHT_BLUE_STAINED_GLASS_PANE   // 9 - Light Blue
        };
        
        // Create number buttons (0-9) with balloon heads
        for (int i = 0; i <= 9; i++) {
            ItemStack number = createTexturedSkull(
                balloonTextures[i],
                "&b&l" + i,
                Arrays.asList(
                    "&7Click to enter " + i,
                    "",
                    "&b● Number: &f" + i
                )
            );
            
            int slot = getKeypadSlot(i);
            vault.setItem(slot, number);
            
            // Add decorative glass pane behind the number
            ItemStack glassPane = new ItemStack(glassPaneColors[i]);
            ItemMeta glassMeta = glassPane.getItemMeta();
            glassMeta.setDisplayName(" ");
            glassPane.setItemMeta(glassMeta);
        }
        
        // Clear button (Red X)
        ItemStack clear = createTexturedSkull(
            "http://textures.minecraft.net/texture/beb588b21a6f98ad1ff4e085c552dcb050efc9cab427f46048f18fc803475f7",
            "&e&lCLEAR",
            Arrays.asList(
                "&7Clear entered PIN",
                "",
                "&c● Reset your input"
            )
        );
        vault.setItem(48, clear);
        
        // Submit button (Green checkmark)
        ItemStack submit = createTexturedSkull(
            "http://textures.minecraft.net/texture/a92e31ffb59c90ab08fc9dc1fe26802035a3a47c42fee63423bcdb4262ecb9b6",
            "&a&lSUBMIT",
            Arrays.asList(
                "&7Submit your PIN",
                "",
                "&a● Confirm your input"
            )
        );
        vault.setItem(50, submit);
        
        // Display area
        updatePinDisplay(vault, "");
        
        // Decorative glass (Light blue to match balloon theme)
        ItemStack glass = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        for (int i = 0; i < 54; i++) {
            if (vault.getItem(i) == null) {
                vault.setItem(i, glass);
            }
        }
        
        pinInventories.put(uuid, vault);
        player.openInventory(vault);
    }
    
    private int getKeypadSlot(int number) {
        // Keypad layout in inventory (54 slots, 6 rows):
        // Row 3: [1] [2] [3]
        // Row 4: [4] [5] [6]
        // Row 5: [7] [8] [9]
        // Row 6:     [0]
        switch (number) {
            case 1: return 20;
            case 2: return 22;
            case 3: return 24;
            case 4: return 29;
            case 5: return 31;
            case 6: return 33;
            case 7: return 38;
            case 8: return 40;
            case 9: return 42;
            case 0: return 49; // Center bottom row
            default: return 49;
        }
    }
    
    private void updatePinDisplay(Inventory vault, String pin) {
        // Display entered PIN as asterisks in top row
        ItemStack display = new ItemStack(Material.PAPER);
        ItemMeta meta = display.getItemMeta();
        
        String displayText = "";
        for (int i = 0; i < pinLength; i++) {
            if (i < pin.length()) {
                displayText += "● ";
            } else {
                displayText += "○ ";
            }
        }
        
        meta.setDisplayName(ChatColor.YELLOW + "PIN: " + ChatColor.WHITE + displayText);
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + "Entered: " + pin.length() + "/" + pinLength + " digits"
        ));
        display.setItemMeta(meta);
        
        vault.setItem(4, display);
    }
    
    private void handlePinVaultClick(Player player, InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        
        UUID uuid = player.getUniqueId();
        ItemStack clicked = event.getCurrentItem();
        String currentPin = pinInput.getOrDefault(uuid, "");
        
        if (clicked.getType() != Material.PLAYER_HEAD) return;
        
        String displayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        
        // Check if it's a number (0-9)
        if (displayName.matches("[0-9]")) {
            if (currentPin.length() < pinLength) {
                currentPin += displayName;
                pinInput.put(uuid, currentPin);
                updatePinDisplay(event.getInventory(), currentPin);
                player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
        }
        // Clear button
        else if (displayName.equals("CLEAR")) {
            pinInput.put(uuid, "");
            updatePinDisplay(event.getInventory(), "");
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 0.5f);
        }
        // Submit button
        else if (displayName.equals("SUBMIT")) {
            if (currentPin.length() == pinLength) {
                player.closeInventory();
                pinInventories.remove(uuid);
                processPinSubmission(player, currentPin);
            } else {
                sendMessage(player, "&cPlease enter all " + pinLength + " digits!");
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }
    }
    
    private void processPinSubmission(Player player, String pin) {
        UUID uuid = player.getUniqueId();
        boolean isSetup = pinSetupMode.getOrDefault(uuid, false);
        pinSetupMode.remove(uuid); // Clean up
        
        // SETUP MODE: Save the PIN
        if (isSetup) {
            String hashedPin = hashPassword(pin);
            dataConfig.set("players." + uuid.toString() + ".pin", hashedPin);
            dataConfig.set("players." + uuid.toString() + ".pin-setup-date", System.currentTimeMillis());
            savePlayerData();
            
            playersWithPin.add(uuid);
            pinVerified.put(uuid, true);
            pinInput.remove(uuid);
            
            enablePlayer(player);
            
            sendMessage(player, "&a&l✔ PIN Setup Complete!");
            sendMessage(player, "&7Your PIN has been saved successfully.");
            player.sendMessage(formatMessage(msgWelcomeBack));
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            
            // Discord notification
            sendRegistrationNotification("🛡️ **" + player.getName() + "** completed PIN setup");
            return;
        }
        
        // LOGIN MODE: Verify the PIN
        String storedHash = dataConfig.getString("players." + uuid.toString() + ".pin");
        
        if (storedHash == null) {
            sendMessage(player, "&cNo PIN found! Please use /setuppin first.");
            return;
        }
        
        if (hashPassword(pin).equals(storedHash)) {
            authenticatedPlayers.put(uuid, true); // Set authenticated!
            pinVerified.put(uuid, true);
            pinAttempts.put(uuid, 0);
            pinInput.remove(uuid);
            
            enablePlayer(player);
            
            dataConfig.set("players." + uuid.toString() + ".last-login", System.currentTimeMillis());
            savePlayerData();
            
            player.sendMessage(formatMessage(msgPinVerified));
            player.sendMessage(formatMessage(msgWelcomeBack));
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            
            // Discord notification
            sendLoginNotification("🔓 **" + player.getName() + "** logged in successfully (PIN)");
            
        } else {
            int attempts = pinAttempts.getOrDefault(uuid, 0) + 1;
            pinAttempts.put(uuid, attempts);
            pinInput.remove(uuid);
            
            if (attempts >= maxPinAttempts) {
                player.kickPlayer(ChatColor.RED + "Too many failed PIN attempts!");
            } else {
                sendMessage(player, "&c&l✘ Incorrect PIN!");
                sendMessage(player, "&7Attempts remaining: &c" + (maxPinAttempts - attempts));
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                
                // Reopen vault for another attempt
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!isPinVerified(player) && player.isOnline()) {
                            openPinVault(player, false);
                        }
                    }
                }.runTaskLater(this, 20L);
            }
        }
    }
    
    // ===== MESSAGE HELPER =====
    private String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message.replace("{server}", serverName));
    }
    
    // Send repeating action bar messages
    private void sendRepeatingActionBar(Player player, String message) {
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (!player.isOnline() || isAuthenticated(player) || count >= 60) {
                    this.cancel();
                    return;
                }
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', message));
                count++;
            }
        }.runTaskTimer(this, 0L, 20L); // Every second for 60 seconds
    }
    
    // Remove authentication boss bar
    private void removeAuthBossBar(UUID uuid) {
        BossBar bossBar = authBossBars.get(uuid);
        if (bossBar != null) {
            bossBar.setVisible(false);
            bossBar.removeAll();
            authBossBars.remove(uuid);
            getLogger().info("[BOSS BAR] Removed boss bar for " + uuid);
        }
    }
    
    // Start rainbow color animation for boss bar
    private void startRainbowBossBar(UUID uuid, BossBar bossBar) {
        // Rainbow colors in order
        final BarColor[] rainbowColors = {
            BarColor.RED,
            BarColor.YELLOW,
            BarColor.GREEN,
            BarColor.BLUE,
            BarColor.PURPLE,
            BarColor.PINK,
            BarColor.WHITE
        };
        
        new BukkitRunnable() {
            int colorIndex = 0;
            
            @Override
            public void run() {
                // Stop if player authenticated or boss bar removed
                if (!authBossBars.containsKey(uuid)) {
                    this.cancel();
                    return;
                }
                
                // Change color
                bossBar.setColor(rainbowColors[colorIndex]);
                colorIndex = (colorIndex + 1) % rainbowColors.length;
            }
        }.runTaskTimer(this, 0L, 10L); // Change color every 0.5 seconds (10 ticks)
    }
    
    // ===== RELOAD COMMAND =====
    private boolean handleReload(CommandSender sender) {
        // Check permissions
        if (sender instanceof Player) {
            Player admin = (Player) sender;
            if (!admin.hasPermission("mbth.admin") && !admin.isOp()) {
                sendMessageToSender(sender, "&c&l✘ You don't have permission to use this command!");
                return true;
            }
        }
        
        // Reload configuration
        reloadConfig();
        loadConfiguration();
        
        sender.sendMessage(formatMessage(msgReloadSuccess));
        sender.sendMessage(formatMessage(msgReloadDetails));
        sendMessageToSender(sender, "&7Server Name: &e" + serverName);
        sendMessageToSender(sender, "&7Discord Integration: " + (discordEnabled ? "&aEnabled" : "&cDisabled"));
        sendMessageToSender(sender, "&7Login Method Choice: " + (allowLoginMethodChoice ? "&aEnabled" : "&cDisabled"));
        
        getLogger().info("Configuration reloaded by " + sender.getName());
        
        return true;
    }
    
    // ===== DISCORD WEBHOOK INTEGRATION =====
    private void sendDiscordWebhook(String webhookUrl, String content, String embedColor, String embedTitle) {
        if (!discordEnabled || webhookUrl == null || webhookUrl.isEmpty()) {
            return;
        }
        
        // Run async to not block main thread
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                URL url = new URL(webhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "MBTH-Security-Webhook");
                connection.setDoOutput(true);
                
                // Build JSON payload with embed
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"username\":\"").append(escapeJson(discordUsername)).append("\",");
                json.append("\"avatar_url\":\"").append(escapeJson(discordAvatarUrl)).append("\",");
                json.append("\"embeds\":[{");
                json.append("\"description\":\"").append(escapeJson(content)).append("\",");
                json.append("\"color\":").append(embedColor).append(",");
                json.append("\"title\":\"").append(escapeJson(embedTitle)).append("\",");
                json.append("\"timestamp\":\"").append(java.time.Instant.now().toString()).append("\"");
                json.append("}]");
                json.append("}");
                
                // Send request
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = json.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                // Check response
                int responseCode = connection.getResponseCode();
                if (responseCode != 200 && responseCode != 204) {
                    getLogger().warning("Discord webhook returned code " + responseCode);
                }
                
                connection.disconnect();
        } catch (Exception e) {
                getLogger().warning("Failed to send Discord webhook: " + e.getMessage());
            }
        });
    }
    
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    // Convenience methods for different notification types
    private void sendLoginNotification(String message) {
        sendDiscordWebhook(discordLoginWebhook, message, "3066993", "🔓 Login Event");
    }
    
    private void sendRegistrationNotification(String message) {
        sendDiscordWebhook(discordRegistrationWebhook, message, "5763719", "📝 Registration Event");
    }
    
    private void sendSecurityNotification(String message) {
        sendDiscordWebhook(discordLoginWebhook, message, "15158332", "⚠️ Security Alert");
    }
}