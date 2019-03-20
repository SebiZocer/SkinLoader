package me.SebiZocer.SkinLoader.Methods;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import me.SebiZocer.SkinLoader.Events.CallScoreboardEvent;
import me.SebiZocer.SkinLoader.Main.Mainclass;
import me.SebiZocer.SkinLoader.Methods.Classes.Profile;
import me.SebiZocer.SkinLoader.Methods.Classes.Skin;
import me.SebiZocer.SkinLoader.Methods.Classes.User;
import me.SebiZocer.SkinLoader.Methods.Management.ProfileManager;

public class GameProfileEditor extends ReflectUtil {
	
	private Player p;
	private boolean active = false;
	
	private Field f = null;
	private int level = -1;
	private int slot = -1;
	private List<String> permissions = null;
	
	/**
	 * Please dont use this method. You can get the GameProfileEditor by an user.
	 */
	public GameProfileEditor(Player p){
		if(p instanceof User){
			this.p = ((User)p).getPlayer();
		} else {
			this.p = p;
		}
	}
	
	/**
	 * Returns if the GameProfileEditor can directly start with editing.
	 */
	public boolean canStart(){
		return !active;
	}
	
	/**
	 * This list contains all GameProfileEditor-calls to prevent overlapping of editing. Dont edit!
	 */
	private List<GameProfileEditorInfo> gpei = new ArrayList<>();
	
	/**
	 * Edits the GameProfile of the player.
	 * 
	 * @param name The new name of the player
	 * @param skin the new skin of the player
	 * @param nameSelf Should the player see his new name
	 * @param skinSelf Should the player see his new skin
	 * @param nameOthers Should others see the new name of the player
	 * @param skinOthers Should others see the new skin of the player
	 * @param sync Always false but true when onDisable() calls this method
	 */
	public void edit(String name, Skin skin, boolean nameSelf, boolean skinSelf, boolean nameOthers, boolean skinOthers, boolean sync){
		if(!sync){
			gpei.add(new GameProfileEditorInfo(name, skin, nameSelf, skinSelf, nameOthers, skinOthers));
			if(canStart()){
				if(!gpei.isEmpty()){
					start(gpei.get(0), false);
					gpei.remove(0);
				}
			}
		} else {
			gpei.clear();
            try {
                Thread.sleep( 100 );
            } catch(InterruptedException e){
                e.printStackTrace();
            }
            start(new GameProfileEditorInfo(name, skin, nameSelf, skinSelf, nameOthers, skinOthers), sync);
		}
	}
	
	/**
	 * Gets called when a edit process is finished. Dont call this method!
	 */
	public void finish(){
		active = false;
		if(canStart()){
			if(!gpei.isEmpty()){
				start(gpei.get(0), false);
				gpei.remove(0);
			}
		}
	}
	
	/**
	 * Gets called when a edit process begins. Dont call this method!
	 */
	private void start(GameProfileEditorInfo gpei, boolean sync){
		active = true;
		f = getField(GameProfile.class, "name");
		level = p.getLevel();
		slot = p.getInventory().getHeldItemSlot();
		permissions = new ArrayList<>();
		if(gpei.getNameSelf() || gpei.getSkinSelf()){
			nickSelf(gpei, sync);
		}
		if(gpei.getNameOthers() || gpei.getSkinOthers()){
			if(!sync){
		        new BukkitRunnable() {
		            @Override
		            public void run() {
		                nickOthers(gpei, sync);
		            }
		        }.runTaskLaterAsynchronously(Mainclass.plugin, 2);
			} else {
	            try {
	                Thread.sleep( 100 );
	            } catch( InterruptedException e ) {
	                e.printStackTrace();
	            }
                nickOthers(gpei, sync);
			}
		}
		if(!sync){
	        new BukkitRunnable() {
	            @Override
	            public void run() {
	                finish();
	            }
	        }.runTaskLaterAsynchronously(Mainclass.plugin, 2);
		} else {
            try {
                Thread.sleep( 100 );
            } catch( InterruptedException e ) {
                e.printStackTrace();
            }
            finish();
		}
	}
	
	/**
	 * Gets called when the player should see his new name or skin. Dont call this method!
	 */
	private void nickSelf(GameProfileEditorInfo gpei, boolean sync){
		destroySelf(gpei, sync);
        if(!sync){
            new BukkitRunnable() {
                @Override
                public void run() {
                    buildSelf(gpei, sync);
                }
            }.runTaskLaterAsynchronously(Mainclass.plugin, 1);
        } else {
            try {
                Thread.sleep( 100 );
            } catch( InterruptedException e ) {
                e.printStackTrace();
            }
            buildSelf(gpei, sync);
        }
	}
	
	/**
	 * Gets called when others should see the new name or skin of the player. Dont call this method!
	 */
	private void nickOthers(GameProfileEditorInfo gpei, boolean sync){
		destroyOthers(gpei, sync);
        if(!sync){
            new BukkitRunnable(){
                @Override
                public void run(){
                    buildOthers(gpei, sync);
                	Bukkit.getPluginManager().callEvent(new CallScoreboardEvent());
                }
            }.runTaskLaterAsynchronously(Mainclass.plugin, 2);
        } else {
            try {
                Thread.sleep( 100 );
            } catch( InterruptedException e ) {
                e.printStackTrace();
            }
            buildOthers(gpei, sync);
        	Bukkit.getPluginManager().callEvent(new CallScoreboardEvent());
        }
	}
	
	
	
	//--------------------------------------------------------------------------------------------
	
	
	
	/**
	 * Dont call this method!
	 */
    @SuppressWarnings("deprecation")
	private void buildSelf(GameProfileEditorInfo gpei, boolean sync){
    	try {
    		
            Object enumDifficulty = getNMSClass("EnumDifficulty").getMethod("getById", int.class).invoke(null, 0);
            Object worldType = getNMSClass("WorldType").getDeclaredField(p.getWorld().getWorldType().toString()).get(null);
            Object enumGameMode = null;

            try {
                enumGameMode = Class.forName("net.minecraft.server." + getVersion() + "." + "EnumGamemode").getMethod("getById", int.class).invoke(null, p.getGameMode().getValue());
            } catch( Exception ex ) {
                enumGameMode = Class.forName("net.minecraft.server." + getVersion() + "." + "WorldSettings").getDeclaredClasses()[0].getMethod("valueOf", String.class).invoke(null, p.getGameMode().toString());
            }
    		
            Constructor< ? > respawnConstructor = getNMSClass( "PacketPlayOutRespawn" ).getConstructor( int.class, enumDifficulty.getClass(), worldType.getClass(), enumGameMode.getClass() );
            Object respawnPacket = respawnConstructor.newInstance( 0, enumDifficulty, worldType, enumGameMode );

            Constructor< ? > positionConstructor = null;
            Object positionPacket = null;

            try {
                positionConstructor = getNMSClass("PacketPlayOutPosition").getConstructor(double.class, double.class, double.class, float.class, float.class, Set.class, int.class);
                positionPacket = positionConstructor.newInstance(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch(), new HashSet<Enum<?>>(), 0);
            } catch( Exception ex ) {
                positionConstructor = getNMSClass("PacketPlayOutPosition").getConstructor(double.class, double.class, double.class, float.class, float.class, Set.class);
                positionPacket = positionConstructor.newInstance(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch(), new HashSet<Enum<?>>());
            }
            
            if(gpei.getSkinSelf()){
                sendPacket(p, respawnPacket);
                sendPacket(p, positionPacket);
            }
    		
            final Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
            final Object entityPlayerArray = Array.newInstance(entityPlayer.getClass(), 1);
            Array.set(entityPlayerArray, 0, entityPlayer);
            
            Object enumAddPlayer = getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[2].getField("ADD_PLAYER").get(null);
            Constructor< ? > tabaddConstructor = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumAddPlayer.getClass(), entityPlayerArray.getClass());
            Object tabaddPacket = tabaddConstructor.newInstance(enumAddPlayer, entityPlayerArray);
            
            sendPacket(p, tabaddPacket);
            
            p.getInventory().setHeldItemSlot(slot);
            p.setLevel(level);
    	} catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
	/**
	 * Dont call this method!
	 */
    private void destroySelf(GameProfileEditorInfo gpei, boolean sync){
    	try {
            GameProfile targetProfile = ProfileManager.getProfile(gpei.getSkin().getName()).getGameProfile();
            if(targetProfile == null) {
                return;
            }
            
            targetProfile = manipulateProfile(targetProfile, gpei.getNameSelf(), gpei.getSkinSelf(), gpei);
            
            final Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
            final Object entityPlayerArray = Array.newInstance(entityPlayer.getClass(), 1);
            Array.set( entityPlayerArray, 0, entityPlayer );

            Object playerProfile = p.getClass().getMethod("getProfile").invoke(p);

            f.set(playerProfile, targetProfile.getName());
            p.setDisplayName( targetProfile.getName() );

            PropertyMap playerProps = ( PropertyMap ) playerProfile.getClass().getMethod( "getProperties" ).invoke( playerProfile );
            playerProps.removeAll( "textures" );
            playerProps.putAll( "textures", targetProfile.getProperties().get( "textures" ) );
            
            Object enumRemovePlayer = getNMSClass( "PacketPlayOutPlayerInfo" ).getDeclaredClasses()[ 2 ].getField( "REMOVE_PLAYER" ).get( null );
            Constructor< ? > tabremoveConstructor = getNMSClass( "PacketPlayOutPlayerInfo" ).getConstructor( enumRemovePlayer.getClass(), entityPlayerArray.getClass() );
            Object tabremovePacket = tabremoveConstructor.newInstance( enumRemovePlayer, entityPlayerArray );
            sendPacket(p, tabremovePacket);
    	} catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
	/**
	 * Dont call this method!
	 */
    private GameProfile manipulateProfile(GameProfile profile, boolean showName, boolean showSkin, GameProfileEditorInfo gpei){
    	try {
    		Iterator<Property> iterator = profile.getProperties().get("textures").iterator();
			Property prop = (Property)iterator.next();
			String name = profile.getName();
			String uuid = profile.getId().toString();
			String signature = prop.getSignature();
			String value = prop.getValue();
			
			Profile self = ProfileManager.getProfile(name);
			
			String selfsignature = self.getSignature();
			String selfvalue = self.getValue();
			
			if(showSkin == false){
				signature = selfsignature;
				value = selfvalue;
			} else {
				signature = gpei.getSkin().getSignature();
				value = gpei.getSkin().getValue();
			}
			
			if(showName == false){
				name = gpei.getName();
				uuid = p.getUniqueId().toString();
			}
			
			profile = new GameProfile(UUID.fromString(uuid), name);
			profile.getProperties().removeAll("textures");
			profile.getProperties().put("textures", new Property("textures", value, signature));
			return profile;
    	} catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return null;
    }
    
	/**
	 * Dont call this method!
	 */
    private void buildOthers(GameProfileEditorInfo gpei, boolean sync){
        try {

            final Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
            final Object entityPlayerArray = Array.newInstance(entityPlayer.getClass(), 1);
            Array.set(entityPlayerArray, 0, entityPlayer);

            Constructor<?> spawnConstructor = getNMSClass("PacketPlayOutNamedEntitySpawn").getConstructor(getNMSClass("EntityHuman"));
            Object spawnPacket = spawnConstructor.newInstance(entityPlayer);
            
            try {
                for(Player t : Bukkit.getOnlinePlayers()){
                    if(t != p && t.hasPermission("x.y")){
                        sendPacket(t, spawnPacket);
                    }
                }
            } catch(ConcurrentModificationException ex){
            }
            
            showPlayer(p, sync);
            p.updateInventory();
            
            if(!sync){
                try {
                    p.getEffectivePermissions().clear();
                    for(String perm : permissions) {
                        PermissionAttachment attachment = p.addAttachment(Mainclass.plugin);
                        attachment.setPermission( perm, true );
                    }
                } catch(Exception ex){
                }
            }
        } catch( Exception ex ) {
            ex.printStackTrace();
        }
    }

	/**
	 * Dont call this method!
	 */
    private void destroyOthers(GameProfileEditorInfo gpei, boolean sync){
        try {
            permissions.clear();
            
            for(PermissionAttachmentInfo info : p.getEffectivePermissions()) {
            	permissions.add(info.getPermission());
            }
            
            hidePlayer(p, sync);
            GameProfile targetProfile = ProfileManager.getProfile(gpei.getName()).getGameProfile();
            if( targetProfile == null ) {
                showPlayer(p, sync);
                return;
            }
            
            targetProfile = manipulateProfile(targetProfile, gpei.getNameOthers(), gpei.getSkinOthers(), gpei);
            
            final Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
            final Object entityPlayerArray = Array.newInstance( entityPlayer.getClass(),1);
            Array.set(entityPlayerArray, 0, entityPlayer);
            
            Object playerProfile = p.getClass().getMethod("getProfile").invoke(p);
            
            f.set(playerProfile, targetProfile.getName());
            p.setDisplayName(targetProfile.getName());
            
            PropertyMap playerProps = ( PropertyMap ) playerProfile.getClass().getMethod( "getProperties" ).invoke( playerProfile );
            playerProps.removeAll( "textures" );
            playerProps.putAll( "textures", targetProfile.getProperties().get( "textures" ) );
            
            Constructor< ? > destroyConstructor = getNMSClass( "PacketPlayOutEntityDestroy" ).getConstructor( int[].class );
            Object destroyPacket = destroyConstructor.newInstance( new int[]{(int)p.getClass().getMethod("getEntityId").invoke(p)});
                        
            for( Player t : Bukkit.getOnlinePlayers() ) {
                if(t != p && !t.hasPermission("x.y")){
                    sendPacket(t, destroyPacket);
                }
            }
            
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }
    
	/**
	 * Dont edit!
	 */
    private static HashMap<Player, List<Player>> cansee = new HashMap<>();
    
	/**
	 * Dont call this method!
	 */
    public static void hidePlayer(Player p, boolean sync){
        if(sync){
        	List<Player> l = new ArrayList<>();
            for(Player t : Bukkit.getOnlinePlayers()){
            	if(t != p){
            		t.hidePlayer(p);
            		l.add(t);
            	}
            }
            cansee.put(p, l);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                	List<Player> l = new ArrayList<>();
                    for(Player t : Bukkit.getOnlinePlayers()){
                    	if(t != p){
                    		t.hidePlayer(p);
                    		l.add(t);
                    	}
                    }
                    cansee.put(p, l);
                }
            }.runTask(Mainclass.plugin);
        }
    }
    
	/**
	 * Dont call this method!
	 */
    private static void showPlayer(Player p, boolean sync){
        if(sync){
            if(cansee.containsKey(p)){
            	for(Player t : cansee.get(p)){
                	t.showPlayer(p);
                }
            	cansee.remove(p);
            } else {
            	for(Player t : Bukkit.getOnlinePlayers()){
                	t.showPlayer(p);
                }
            }
        } else {
            new BukkitRunnable(){
                @Override
                public void run(){
                    if(cansee.containsKey(p)){
                    	for(Player t : cansee.get(p)){
                        	t.showPlayer(p);
                        }
                    	cansee.remove(p);
                    } else {
                    	for(Player t : Bukkit.getOnlinePlayers()){
                        	t.showPlayer(p);
                        }
                    }
                }
            }.runTask(Mainclass.plugin);
        }
    }
    
    public class GameProfileEditorInfo{
    	
    	private String name;
    	private Skin skin;
    	private boolean nameSelf;
    	private boolean skinSelf;
    	private boolean nameOthers;
    	private boolean skinOthers;
    	
    	public GameProfileEditorInfo(String name, Skin skin, boolean nameSelf, boolean skinSelf, boolean nameOthers, boolean skinOthers){
    		this.name = name;
    		this.skin = skin;
    		this.nameSelf = nameSelf;
    		this.skinSelf = skinSelf;
    		this.nameOthers = nameOthers;
    		this.skinOthers = skinOthers;
    	}
    	
    	public String getName(){
    		return name;
    	}
    	
    	public Skin getSkin(){
    		return skin;
    	}
    	
    	public boolean getNameSelf(){
    		return nameSelf;
    	}
    	
    	public boolean getSkinSelf(){
    		return skinSelf;
    	}
    	
    	public boolean getNameOthers(){
    		return nameOthers;
    	}
    	
    	public boolean getSkinOthers(){
    		return skinOthers;
    	}
    }
}