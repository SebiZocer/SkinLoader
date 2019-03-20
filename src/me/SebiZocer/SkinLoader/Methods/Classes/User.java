package me.SebiZocer.SkinLoader.Methods.Classes;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import me.SebiZocer.SkinLoader.Methods.GameProfileEditor;
import me.SebiZocer.SkinLoader.Methods.Management.Manager;
import me.SebiZocer.SkinLoader.Methods.Management.SkinManager;
import me.SebiZocer.SkinLoader.Methods.MySQL.MySQLNick;
import me.SebiZocer.SkinLoader.Methods.MySQL.MySQLSkin;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;

@SuppressWarnings("deprecation")
public class User implements Player{
	
	//Custom Methods
	
	private boolean nicked = false;
	private String realname;
	private String nickname;
	private Skin skin;
	private GameProfileEditor gpe;
	
	/**
	 * Returns the real name of the Player. This Name will always be the right name of the player.
	 */
	public String getRealname(){
		return realname;
	}
	
	/**
	 * Returns the nickname of the player. Can be set by "setNickname(nickname)" or if the player nicks with the included nick-method.
	 * Gets resetted to realname if the method "setSkin(skin)" or "loadSkin()" get used.
	 */
	public String getNickname(){
		return nickname;
	}
	
	/**
	 * Sets the nickname of the player. This will not be seen ingame, just inside the code. Please only use this method if you want to use your own nick-method.
	 * 
	 * @param nickname The custom nickname
	 */
	public void setNickname(String nickname){
		this.nickname = nickname;
	}
	
	/**
	 * Returns if the player is nicked. Can be changed by the method "setNicked(nicked)", "setSkin(skin)" or "loadSkin()".
	 */
	public boolean isNicked(){
		return nicked;
	}
	
	/**
	 * Sets the nicked-boolean. Please only use this method if you want to use your own nick-method.
	 * 
	 * @param nicked Boolean if the player is nicked or not
	 */
	public void setNicked(boolean nicked){
		this.nicked = nicked;
	}
	
	/**
	 * Returns a boolean from the table "autonick_database" if connected. Can be used for an AutoNick-plugin. 
	 */
	public boolean getAutonick(){
		return MySQLNick.getAutonick(p.getUniqueId());
	}
	
	/**
	 * Nicks the player with a random unused name and skin.
	 * 
	 * @return The new nickname of the player
	 */
	public String nick(){
		return nick(Manager.getRandomNickname(true));
	}
	
	/**
	 * Nicks the player with an name you choose. If the name is already used as realname or nickname of an other player, the name will be changed.
	 * The skin will be the one of the player, if he exists and no other player on the server is using this skin currently.
	 * 
	 * @param name The nickname of the player you want
	 * @return The new nickname of the player
	 */
	public String nick(String name){
		Info.custom("Trying to nick §6" + realname + " §3as §6" + name);
		Skin skin = null;
		if(SkinManager.skinExists(name)){
			Info.custom("Skin of §6" + name + " §3found");
			skin = SkinManager.getSkin(name);
		} else {
			Info.custom("Skin of §6" + name + " §3not exists. Getting random skin from SkinManager");
			skin = SkinManager.getRandomSkin(true);
		}
		Info.custom("Calling §6nick(name, skin) §3to nick §6" + realname);
		return nick(name, skin);
	}
	
	/**
	 * Nicks the player with an name and skin you choose. If the name is already used as realname or nickname of an other player, the name will be changed.
	 * The skin will be changed if an player on the server is already using it.
	 * 
	 * @param name The name of the player you want
	 * @param skin The owner of the skin
	 * @return The new nickname of the player
	 */
	
	public String nick(String name, Skin skin){
		Info.custom("Trying to nick §6" + realname + " §3as §6" + name + " §3with the skin of §6" + skin.getName());
		boolean b1 = false;
		boolean b2 = false;
		if(!SkinManager.skinExists(skin.getName())){
			b1 = true;
		}
		for(User u : users){
			if(u.getSkin().getName().equals(skin.getName())){
				b1 = true;
			}
			if(u.getName().equals(name) || u.getRealname().equals(name)){
				b2 = true;
			}
		}
		if(b1){
			Info.custom("Skin of §6" + skin.getName() + " §3not exists or is already in use. Getting random skin from SkinManager");
			skin = SkinManager.getRandomSkin(true);
		}
		if(b2){
			Info.custom("Name §6" + name + " §3ist already in use. Getting random nickname");
			name = Manager.getRandomNickname(true);
		}
		nickname = name;
		this.skin = skin;
		Info.custom("Starting with GameProfileEditing...");
		gpe.edit(name, skin, true, true, true, true, false);
		nicked = true;
		return name;
	}
	
	/**
	 * If nicked is true the name will be changed to the realname and the skin will be the "selected" skin (If you dont know what i mean with "selected", its not important, then the skin of the player will be loaded).
	 */
	public void unnick(){
		if(nicked){
			nicked = false;
			nickname = realname;
			skin = MySQLSkin.getCurrentSkin(getUniqueId());
			Info.custom("Unnicking §6" + realname);
			Info.custom("Starting with GameProfileEditing...");
			gpe.edit(realname, skin, true, true, true, true, false);
		}
	}
	
	/**
	 * Sets the skin of the player to the "selected" skin (If you dont know what i mean with "selected", its not important, then the skin of the player will be loaded).
	 */
	public void loadSkin(){
		nicked = false;
		nickname = realname;
		skin = MySQLSkin.getCurrentSkin(getUniqueId());
		Info.custom("Loading selected skin for §6" + realname);
		Info.custom("Starting with GameProfileEditing...");
		gpe.edit(realname, skin, true, true, true, true, false);
	}
	
	/**
	 * Sets the skin of the player to the one you want. The name will be changed to realname and nicked will be set to false.
	 */
	public void setSkin(Skin skin){
		nicked = false;
		nickname = realname;
		this.skin = skin;
		Info.custom("Setting skin of §6" + realname + " §3to " + skin.getName());
		Info.custom("Starting with GameProfileEditing...");
		gpe.edit(realname, skin, true, true, true, true, false);
	}
	
	/**
	 * Returns the current skin of the player.
	 * 
	 * @return The current skin of the player
	 */
	public Skin getSkin(){
		return skin;
	}
	
	/**
	 * This method returns the GameProfileEditor of the player. With that you can write your own methods to change the name or the skin of the player.
	 * 
	 * @return The GameProfileEditor of the player
	 */
	public GameProfileEditor getGameProfileEditor(){
		return gpe;
	}
	
	/**
	 * Sends a message to the player. The player can see this message over his hotbar.
	 */
	public void sendHotbarMessage(String msg){
        IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + msg + "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(icbc, (byte) 2);
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(bar);
	}
	
	/**
	 * Sends a title to the player.
	 */
	public void sendTitle(Player p, String title, String subtitle, int fadein, int stay, int fadeout) {
		PlayerConnection con = ((CraftPlayer)p).getHandle().playerConnection;
		IChatBaseComponent Ititle = ChatSerializer.a("{\"text\":\"" + title + "\"}");
	   	IChatBaseComponent Isub = ChatSerializer.a("{\"text\":\"" + subtitle + "\"}");
	   	con.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TIMES, Ititle, fadein, stay, fadeout));
	   	con.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TIMES, Isub));
	   	con.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, Ititle));
	   	con.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, Isub));
	}
	
	//Custom Methods End
	
	//Class Management
	
	private Player p;
	
	public User(Player p){
		this.p = p;
		this.realname = p.getName();
		this.nickname = p.getName();
		this.skin = SkinManager.getSkin(p.getUniqueId());
		this.gpe = new GameProfileEditor(p);
	}
	
	private static ArrayList<User> users = new ArrayList<>();
	
	public static void onQuit(Player p){
		User u = User.getUser(p);
		users.remove(u);
	}
	
	/**
	 * Returns the user by an name. If the players name or realname equals the name -> return.
	 */
	public static User getUser(String name){
		for(User u : users){
			if(u.getName().equals(name) || u.getRealname().equals(name)){
				return u;
			}
		}
		if(Bukkit.getPlayer(name) != null){
			User u = new User(Bukkit.getPlayer(name));
			users.add(u);
			return u;
		}
		return null;
		/*Info.error();
		Info.custom("§cCouldnt find a user with the name §6" + name + "§c. Please use §3User.getUser(Player) §cto get a user. The following error shows you where you have to change your code.");
		throw new NullPointerException();*/
	}
	
	/**
	 * Returns the user by an player. If no user with this player exists it will return a new user, but at reloading and joining a new user will be registered, so this will never happen.
	 */
	public static User getUser(Player p){
		for(User u : users){
			if(u.getPlayer().equals(p)){
				return u;
			}
		}
		User u = new User(p);
		users.add(u);
		return u;
	}
	
	//Class Management End
	
	@Override
	public void closeInventory() {
		p.closeInventory();
	}

	@Override
	public Inventory getEnderChest() {
		return p.getEnderChest();
	}

	@Override
	public int getExpToLevel() {
		return p.getExpToLevel();
	}

	@Override
	public GameMode getGameMode() {
		return p.getGameMode();
	}

	@Override
	public PlayerInventory getInventory() {
		return p.getInventory();
	}

	@Override
	public ItemStack getItemInHand() {
		return p.getItemInHand();
	}

	@Override
	public ItemStack getItemOnCursor() {
		return p.getItemOnCursor();
	}

	@Override
	public String getName() {
		return p.getName();
	}

	@Override
	public InventoryView getOpenInventory() {
		return p.getOpenInventory();
	}

	@Override
	public int getSleepTicks() {
		return p.getSleepTicks();
	}

	@Override
	public boolean isBlocking() {
		return p.isBlocking();
	}

	@Override
	public boolean isSleeping() {
		return p.isSleeping();
	}

	@Override
	public InventoryView openEnchanting(Location loc, boolean bol) {
		return p.openEnchanting(loc, bol);
	}

	@Override
	public InventoryView openInventory(Inventory inv) {
		return p.openInventory(inv);
	}

	@Override
	public void openInventory(InventoryView inv) {
		p.openInventory(inv);
	}

	@Override
	public InventoryView openWorkbench(Location loc, boolean bol){
		return p.openWorkbench(loc, bol);
	}

	@Override
	public void setGameMode(GameMode gm) {
		p.setGameMode(gm);
	}

	@Override
	public void setItemInHand(ItemStack is) {
		p.setItemInHand(is);
	}

	@Override
	public void setItemOnCursor(ItemStack is) {
		p.setItemOnCursor(is);
	}

	@Override
	public boolean setWindowProperty(Property pro, int i1) {
		return p.setWindowProperty(pro, i1);
	}

	@Override
	public boolean addPotionEffect(PotionEffect pe) {
		return p.addPotionEffect(pe);
	}

	@Override
	public boolean addPotionEffect(PotionEffect pe, boolean bol) {
		return p.addPotionEffect(pe, bol);
	}

	@Override
	public boolean addPotionEffects(Collection<PotionEffect> pes) {
		return p.addPotionEffects(pes);
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects() {
		return p.getActivePotionEffects();
	}

	@Override
	public boolean getCanPickupItems() {
		return p.getCanPickupItems();
	}

	@Override
	public EntityEquipment getEquipment() {
		return p.getEquipment();
	}

	@Override
	public double getEyeHeight() {
		return p.getEyeHeight();
	}

	@Override
	public double getEyeHeight(boolean bol) {
		return p.getEyeHeight(bol);
	}

	@Override
	public Location getEyeLocation() {
		return p.getEyeLocation();
	}

	@Override
	public Player getKiller() {
		return p.getKiller();
	}

	@Override
	public double getLastDamage() {
		return p.getLastDamage();
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(Set<Material> mat, int i1) {
		return p.getLastTwoTargetBlocks(mat, i1);
	}

	@Override
	public Entity getLeashHolder() throws IllegalStateException {
		return p.getLeashHolder();
	}

	@Override
	public List<Block> getLineOfSight(Set<Material> i1, int i2) {
		return p.getLineOfSight(i1, i2);
	}

	@Override
	public int getMaximumAir() {
		return p.getMaximumAir();
	}

	@Override
	public int getMaximumNoDamageTicks() {
		return p.getMaximumNoDamageTicks();
	}

	@Override
	public int getNoDamageTicks() {
		return p.getNoDamageTicks();
	}

	@Override
	public int getRemainingAir() {
		return p.getRemainingAir();
	}

	@Override
	public boolean getRemoveWhenFarAway() {
		return p.getRemoveWhenFarAway();
	}

	@Override
	public Block getTargetBlock(Set<Material> mat, int i1) {
		return p.getTargetBlock(mat, i1);
	}

	@Override
	public boolean hasLineOfSight(Entity e) {
		return p.hasLineOfSight(e);
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType pef) {
		return p.hasPotionEffect(pef);
	}

	@Override
	public boolean isLeashed() {
		return p.isLeashed();
	}

	@Override
	public void removePotionEffect(PotionEffectType pef) {
		p.removePotionEffect(pef);
	}

	@Override
	public void setCanPickupItems(boolean bol) {
		p.setCanPickupItems(bol);
	}

	@Override
	public void setLastDamage(double i1) {
		p.setLastDamage(i1);
	}

	@Override
	public boolean setLeashHolder(Entity e) {
		return p.setLeashHolder(e);
	}

	@Override
	public void setMaximumAir(int i1) {
		p.setMaximumAir(i1);
	}

	@Override
	public void setMaximumNoDamageTicks(int i1) {
		p.setMaximumNoDamageTicks(i1);
	}

	@Override
	public void setNoDamageTicks(int i1) {
		p.setNoDamageTicks(i1);
	}

	@Override
	public void setRemainingAir(int i1) {
		p.setRemainingAir(i1);
	}

	@Override
	public void setRemoveWhenFarAway(boolean bol) {
		p.setRemoveWhenFarAway(bol);
	}

	@Override
	public boolean eject() {
		return p.eject();
	}

	@Override
	public String getCustomName() {
		return p.getCustomName();
	}

	@Override
	public int getEntityId() {
		return p.getEntityId();
	}

	@Override
	public float getFallDistance() {
		return p.getFallDistance();
	}

	@Override
	public int getFireTicks() {
		return p.getFireTicks();
	}

	@Override
	public EntityDamageEvent getLastDamageCause() {
		return p.getLastDamageCause();
	}

	@Override
	public Location getLocation() {
		return p.getLocation();
	}

	@Override
	public Location getLocation(Location loc) {
		return p.getLocation(loc);
	}

	@Override
	public int getMaxFireTicks() {
		return p.getMaxFireTicks();
	}

	@Override
	public List<Entity> getNearbyEntities(double i1, double i2, double i3) {
		return p.getNearbyEntities(i1, i2, i3);
	}

	@Override
	public Entity getPassenger() {
		return p.getPassenger();
	}

	@Override
	public Server getServer() {
		return p.getServer();
	}

	@Override
	public int getTicksLived() {
		return p.getTicksLived();
	}

	@Override
	public EntityType getType() {
		return p.getType();
	}

	@Override
	public UUID getUniqueId() {
		return p.getUniqueId();
	}

	@Override
	public Entity getVehicle() {
		return p.getVehicle();
	}

	@Override
	public Vector getVelocity() {
		return p.getVelocity();
	}

	@Override
	public World getWorld() {
		return p.getWorld();
	}

	@Override
	public boolean isCustomNameVisible() {
		return p.isCustomNameVisible();
	}

	@Override
	public boolean isDead() {
		return p.isDead();
	}

	@Override
	public boolean isEmpty() {
		return p.isEmpty();
	}

	@Override
	public boolean isInsideVehicle() {
		return p.isInsideVehicle();
	}

	@Override
	public boolean isValid() {
		return p.isValid();
	}

	@Override
	public boolean leaveVehicle() {
		return p.leaveVehicle();
	}

	@Override
	public void playEffect(EntityEffect ee) {
		p.playEffect(ee);
	}

	@Override
	public void remove() {
		p.remove();
	}

	@Override
	public void setCustomName(String name) {
		p.setCustomName(name);
	}

	@Override
	public void setCustomNameVisible(boolean bol) {
		p.setCustomNameVisible(bol);
	}

	@Override
	public void setFallDistance(float flo) {
		p.setFallDistance(flo);
	}

	@Override
	public void setFireTicks(int i1) {
		p.setFireTicks(i1);
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent ede) {
		p.setLastDamageCause(ede);
	}

	@Override
	public boolean setPassenger(Entity e) {
		return p.setPassenger(e);
	}

	@Override
	public void setTicksLived(int i1) {
		p.setTicksLived(i1);
	}

	@Override
	public void setVelocity(Vector vec) {
		p.setVelocity(vec);
	}

	@Override
	public boolean teleport(Location loc) {
		return p.teleport(loc);
	}

	@Override
	public boolean teleport(Entity e) {
		return p.teleport(e);
	}

	@Override
	public boolean teleport(Location loc, TeleportCause tc) {
		return p.teleport(p, tc);
	}

	@Override
	public boolean teleport(Entity e, TeleportCause tc) {
		return p.teleport(e, tc);
	}

	@Override
	public List<MetadataValue> getMetadata(String arg0) {
		return p.getMetadata(arg0);
	}

	@Override
	public boolean hasMetadata(String arg0) {
		return p.hasMetadata(arg0);
	}

	@Override
	public void removeMetadata(String arg0, Plugin pl) {
		p.removeMetadata(arg0, pl);
	}

	@Override
	public void setMetadata(String arg0, MetadataValue arg1) {
		p.setMetadata(arg0, arg1);
	}

	@Override
	public void sendMessage(String msg) {
		p.sendMessage(msg);
	}

	@Override
	public void sendMessage(String[] msg) {
		p.sendMessage(msg);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin pl) {
		return p.addAttachment(pl);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin pl, int i1) {
		return p.addAttachment(pl, i1);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin pl, String arg1, boolean bol) {
		return p.addAttachment(pl, arg1, bol);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin pl, String arg1, boolean bol, int i1) {
		return p.addAttachment(pl, arg1, bol, i1);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return p.getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(String per) {
		return p.hasPermission(per);
	}

	@Override
	public boolean hasPermission(Permission per) {
		return p.hasPermission(per);
	}

	@Override
	public boolean isPermissionSet(String per) {
		return p.isPermissionSet(per);
	}

	@Override
	public boolean isPermissionSet(Permission per) {
		return p.isPermissionSet(per);
	}

	@Override
	public void recalculatePermissions() {
		p.recalculatePermissions();
	}

	@Override
	public void removeAttachment(PermissionAttachment pa) {
		p.removeAttachment(pa);
	}

	@Override
	public boolean isOp() {
		return p.isOp();
	}

	@Override
	public void setOp(boolean bol) {
		p.setOp(bol);
	}

	@Override
	public void damage(double i1) {
		p.damage(i1);
	}

	@Override
	public void damage(double i1, Entity e) {
		p.damage(i1, e);
	}

	@Override
	public double getHealth() {
		return p.getHealth();
	}

	@Override
	public double getMaxHealth() {
		return p.getMaxHealth();
	}

	@Override
	public void resetMaxHealth() {
		p.resetMaxHealth();
	}

	@Override
	public void setHealth(double i1) {
		p.setHealth(i1);
	}

	@Override
	public void setMaxHealth(double i1) {
		p.setMaxHealth(i1);
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> clazz) {
		return p.launchProjectile(clazz);
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> clazz, Vector vec) {
		return p.launchProjectile(clazz, vec);
	}

	@Override
	public void abandonConversation(Conversation con) {
		p.abandonConversation(con);
	}

	@Override
	public void abandonConversation(Conversation con, ConversationAbandonedEvent cae) {
		p.abandonConversation(con, cae);
	}

	@Override
	public void acceptConversationInput(String arg0) {
		p.acceptConversationInput(arg0);
	}

	@Override
	public boolean beginConversation(Conversation arg0) {
		return p.beginConversation(arg0);
	}

	@Override
	public boolean isConversing() {
		return p.isConversing();
	}

	@Override
	public long getFirstPlayed() {
		return p.getFirstPlayed();
	}

	@Override
	public long getLastPlayed() {
		return p.getLastPlayed();
	}

	@Override
	public Player getPlayer() {
		return p.getPlayer();
	}

	@Override
	public boolean hasPlayedBefore() {
		return p.hasPlayedBefore();
	}

	@Override
	public boolean isBanned() {
		return p.isBanned();
	}

	@Override
	public boolean isOnline() {
		return p.isOnline();
	}

	@Override
	public boolean isWhitelisted() {
		return p.isWhitelisted();
	}

	@Override
	public void setWhitelisted(boolean bol) {
		p.setWhitelisted(bol);
	}

	@Override
	public Map<String, Object> serialize() {
		return p.serialize();
	}

	@Override
	public Set<String> getListeningPluginChannels() {
		return p.getListeningPluginChannels();
	}

	@Override
	public void sendPluginMessage(Plugin pl, String arg1, byte[] arg2) {
		p.sendPluginMessage(pl, arg1, arg2);
	}

	@Override
	public void awardAchievement(Achievement ach) {
		p.awardAchievement(ach);
	}

	@Override
	public boolean canSee(Player p) {
		return this.p.canSee(p);
	}

	@Override
	public void chat(String msg) {
		p.chat(msg);
	}

	@Override
	public void decrementStatistic(Statistic sta) throws IllegalArgumentException {
		p.decrementStatistic(sta);
	}

	@Override
	public void decrementStatistic(Statistic sta, int i1) throws IllegalArgumentException {
		p.decrementStatistic(sta, i1);
	}

	@Override
	public void decrementStatistic(Statistic sta, Material mat) throws IllegalArgumentException {
		p.decrementStatistic(sta, mat);
	}

	@Override
	public void decrementStatistic(Statistic sta, EntityType et) throws IllegalArgumentException {
		p.decrementStatistic(sta, et);
	}

	@Override
	public void decrementStatistic(Statistic sta, Material mat, int i1) throws IllegalArgumentException {
		p.decrementStatistic(sta, mat, i1);
	}

	@Override
	public void decrementStatistic(Statistic sta, EntityType et, int i1) {
		p.decrementStatistic(sta, et, i1);
	}

	@Override
	public InetSocketAddress getAddress() {
		return p.getAddress();
	}

	@Override
	public boolean getAllowFlight() {
		return p.getAllowFlight();
	}

	@Override
	public Location getBedSpawnLocation() {
		return p.getBedSpawnLocation();
	}

	@Override
	public Location getCompassTarget() {
		return p.getCompassTarget();
	}

	@Override
	public String getDisplayName() {
		return p.getDisplayName();
	}

	@Override
	public float getExhaustion() {
		return p.getExhaustion();
	}

	@Override
	public float getExp() {
		return p.getExp();
	}

	@Override
	public float getFlySpeed() {
		return p.getFlySpeed();
	}

	@Override
	public int getFoodLevel() {
		return p.getFoodLevel();
	}

	@Override
	public double getHealthScale() {
		return p.getHealthScale();
	}

	@Override
	public int getLevel() {
		return p.getLevel();
	}

	@Override
	public String getPlayerListName() {
		return p.getPlayerListName();
	}

	@Override
	public long getPlayerTime() {
		return p.getPlayerTime();
	}

	@Override
	public long getPlayerTimeOffset() {
		return p.getPlayerTimeOffset();
	}

	@Override
	public WeatherType getPlayerWeather() {
		return p.getPlayerWeather();
	}

	@Override
	public float getSaturation() {
		return p.getSaturation();
	}

	@Override
	public Scoreboard getScoreboard() {
		return p.getScoreboard();
	}

	@Override
	public Entity getSpectatorTarget() {
		return p.getSpectatorTarget();
	}

	@Override
	public int getStatistic(Statistic sta) throws IllegalArgumentException {
		return p.getStatistic(sta);
	}

	@Override
	public int getStatistic(Statistic sta, Material mat) throws IllegalArgumentException {
		return p.getStatistic(sta, mat);
	}

	@Override
	public int getStatistic(Statistic sta, EntityType et) throws IllegalArgumentException {
		return p.getStatistic(sta, et);
	}

	@Override
	public int getTotalExperience() {
		return p.getTotalExperience();
	}

	@Override
	public float getWalkSpeed() {
		return p.getWalkSpeed();
	}

	@Override
	public void giveExp(int i1) {
		p.giveExp(i1);
	}

	@Override
	public void giveExpLevels(int i1) {
		p.giveExpLevels(i1);
	}

	@Override
	public boolean hasAchievement(Achievement ach) {
		return p.hasAchievement(ach);
	}

	@Override
	public void hidePlayer(Player p) {
		this.p.hidePlayer(p);
	}

	@Override
	public void incrementStatistic(Statistic sta) throws IllegalArgumentException {
		p.incrementStatistic(sta);
	}

	@Override
	public void incrementStatistic(Statistic sta, int i1) throws IllegalArgumentException {
		p.incrementStatistic(sta, i1);
	}

	@Override
	public void incrementStatistic(Statistic sta, Material mat) throws IllegalArgumentException {
		p.incrementStatistic(sta, mat);
	}

	@Override
	public void incrementStatistic(Statistic sta, EntityType et) throws IllegalArgumentException {
		p.incrementStatistic(sta, et);
	}

	@Override
	public void incrementStatistic(Statistic sta, Material mat, int i1) throws IllegalArgumentException {
		p.incrementStatistic(sta, mat, i1);
	}

	@Override
	public void incrementStatistic(Statistic sta, EntityType et, int i1) throws IllegalArgumentException {
		p.incrementStatistic(sta, et, i1);
	}

	@Override
	public boolean isFlying() {
		return p.isFlying();
	}

	@Override
	public boolean isHealthScaled() {
		return p.isHealthScaled();
	}

	@Override
	public boolean isOnGround() {
		return p.isOnGround();
	}

	@Override
	public boolean isPlayerTimeRelative() {
		return p.isPlayerTimeRelative();
	}

	@Override
	public boolean isSleepingIgnored() {
		return p.isSleepingIgnored();
	}

	@Override
	public boolean isSneaking() {
		return p.isSneaking();
	}

	@Override
	public boolean isSprinting() {
		return p.isSprinting();
	}

	@Override
	public void kickPlayer(String msg) {
		p.kickPlayer(msg);
	}

	@Override
	public void loadData() {
		p.loadData();
	}

	@Override
	public boolean performCommand(String cmd) {
		return p.performCommand(cmd);
	}

	@Override
	public void playEffect(Location loc, Effect eff, int i1) {
		p.playEffect(loc, eff, i1);
	}

	@Override
	public <T> void playEffect(Location loc, Effect eff, T arg2) {
		p.playEffect(loc, eff, arg2);
		
	}

	@Override
	public void playNote(Location loc, byte arg1, byte arg2) {
		p.playNote(loc, arg1, arg2);
	}

	@Override
	public void playNote(Location loc, Instrument ins, Note n) {
		p.playNote(loc, ins, n);
	}

	@Override
	public void playSound(Location loc, Sound s, float flo1, float flo2) {
		p.playSound(loc, s, flo1, flo2);
	}

	@Override
	public void playSound(Location loc, String arg1, float flo1, float flo2) {
		p.playSound(loc, arg1, flo1, flo2);
	}

	@Override
	public void removeAchievement(Achievement ach) {
		p.removeAchievement(ach);
	}

	@Override
	public void resetPlayerTime() {
		p.resetPlayerTime();
	}

	@Override
	public void resetPlayerWeather() {
		p.resetPlayerWeather();
	}

	@Override
	public void resetTitle() {
		p.resetTitle();
	}

	@Override
	public void saveData() {
		p.saveData();
	}

	@Override
	public void sendBlockChange(Location loc, Material mat, byte arg2) {
		p.sendBlockChange(loc, mat, arg2);
	}

	@Override
	public boolean sendChunkChange(Location loc, int i1, int i2, int i3, byte[] arg4) {
		return p.sendChunkChange(loc, i1, i2, i3, arg4);
	}

	@Override
	public void sendMap(MapView mv) {
		p.sendMap(mv);
	}

	@Override
	public void sendRawMessage(String msg) {
		p.sendRawMessage(msg);
	}

	@Override
	public void sendSignChange(Location loc, String[] msg) throws IllegalArgumentException {
		p.sendSignChange(loc, msg);
	}

	@Override
	public void sendTitle(String title, String subtitle) {
		p.sendTitle(title, subtitle);
	}

	@Override
	public void setAllowFlight(boolean bol) {
		p.setAllowFlight(bol);
	}

	@Override
	public void setBedSpawnLocation(Location loc) {
		p.setBedSpawnLocation(loc);
	}

	@Override
	public void setBedSpawnLocation(Location loc, boolean bol) {
		p.setBedSpawnLocation(loc, bol);
	}

	@Override
	public void setCompassTarget(Location loc) {
		p.setCompassTarget(loc);
	}

	@Override
	public void setDisplayName(String name) {
		p.setDisplayName(name);
	}

	@Override
	public void setExhaustion(float flo1) {
		p.setExhaustion(flo1);
	}

	@Override
	public void setExp(float flo1) {
		p.setExp(flo1);
	}

	@Override
	public void setFlySpeed(float flo1) throws IllegalArgumentException {
		p.setFlySpeed(flo1);
	}

	@Override
	public void setFlying(boolean bol) {
		p.setFlying(bol);
	}

	@Override
	public void setFoodLevel(int i1) {
		p.setFoodLevel(i1);
	}

	@Override
	public void setHealthScale(double i1) throws IllegalArgumentException {
		p.setHealthScale(i1);
	}

	@Override
	public void setHealthScaled(boolean bol) {
		p.setHealthScaled(bol);
	}

	@Override
	public void setLevel(int i1) {
		p.setLevel(i1);
	}

	@Override
	public void setPlayerListName(String name) {
		p.setPlayerListName(name);
	}

	@Override
	public void setPlayerTime(long i1, boolean bol) {
		p.setPlayerTime(i1, bol);
	}

	@Override
	public void setPlayerWeather(WeatherType wt) {
		p.setPlayerWeather(wt);
	}

	@Override
	public void setResourcePack(String arg0) {
		p.setResourcePack(arg0);
	}

	@Override
	public void setSaturation(float flo1) {
		p.setSaturation(flo1);
	}

	@Override
	public void setScoreboard(Scoreboard sb) throws IllegalArgumentException, IllegalStateException {
		p.setScoreboard(sb);
	}

	@Override
	public void setSleepingIgnored(boolean bol) {
		p.setSleepingIgnored(bol);
	}

	@Override
	public void setSneaking(boolean bol) {
		p.setSneaking(bol);
	}

	@Override
	public void setSpectatorTarget(Entity e) {
		p.setSpectatorTarget(e);
	}

	@Override
	public void setSprinting(boolean bol) {
		p.setSprinting(bol);
	}

	@Override
	public void setStatistic(Statistic sta, int i1) throws IllegalArgumentException {
		p.setStatistic(sta, i1);
	}

	@Override
	public void setStatistic(Statistic sta, Material mat, int i1) throws IllegalArgumentException {
		p.setStatistic(sta, mat, i1);
	}

	@Override
	public void setStatistic(Statistic sta, EntityType et, int i1) {
		p.setStatistic(sta, et, i1);
	}

	@Override
	public void setTexturePack(String arg0) {
		p.setTexturePack(arg0);
	}

	@Override
	public void setTotalExperience(int i1) {
		p.setTotalExperience(i1);
	}

	@Override
	public void setWalkSpeed(float flo1) throws IllegalArgumentException {
		p.setWalkSpeed(flo1);
	}

	@Override
	public void showPlayer(Player p) {
		this.p.showPlayer(p);
	}

	@Override
	public Spigot spigot() {
		return p.spigot();
	}

	@Override
	public void updateInventory() {
		p.updateInventory();
	}
	
	//only 1.8.8
	
	@Override
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hs, int i1) {
		return p.getLastTwoTargetBlocks(hs, i1);
	}

	@Override
	public List<Block> getLineOfSight(HashSet<Byte> hs, int i1) {
		return p.getLineOfSight(hs, i1);
	}

	@Override
	public Block getTargetBlock(HashSet<Byte> hs, int i1) {
		return p.getTargetBlock(hs, i1);
	}

	@Override
	public Arrow shootArrow() {
		return p.shootArrow();
	}

	@Override
	public Egg throwEgg() {
		return p.throwEgg();
	}

	@Override
	public Snowball throwSnowball() {
		return p.throwSnowball();
	}

	@Override
	public void setBanned(boolean b1) {
		p.setBanned(b1);
	}

	@Override
	public void sendBlockChange(Location loc, int i1, byte i2) {
		p.sendBlockChange(loc, i1, i2);
	}
}
