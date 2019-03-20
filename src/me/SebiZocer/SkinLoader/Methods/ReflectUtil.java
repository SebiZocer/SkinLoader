package me.SebiZocer.SkinLoader.Methods;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectUtil {

    public static Field modifiers = getField( Field.class, "modifiers" );

    public static Class< ? > getNMSClass( String name ) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[ 3 ];
        try {
            return Class.forName( "net.minecraft.server." + version + "." + name );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPacket( Player to, Object packet ) {
        try {
            Object playerHandle = to.getClass().getMethod( "getHandle" ).invoke( to );
            Object playerConnection = playerHandle.getClass().getField( "playerConnection" ).get( playerHandle );
            playerConnection.getClass().getMethod( "sendPacket", getNMSClass( "Packet" ) ).invoke( playerConnection, packet );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static void setField( Object change, String name, Object to ) throws Exception {
        Field field = change.getClass().getDeclaredField( name );
        field.setAccessible( true );
        field.set( change, to );
        field.setAccessible( false );
    }

    public static Field getField( Class< ? > clazz, String name ) {
        try {
            Field field = clazz.getDeclaredField( name );
            field.setAccessible( true );
            if( Modifier.isFinal( field.getModifiers() ) ) {
                modifiers.set( field, field.getModifiers() & ~Modifier.FINAL );
            }
            return field;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[ 3 ];
    }

}
