package lenlino.com.luckyblock;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class EntityGetPlayer {
    @Nullable
    public static Entity getStraightEntity(Player p,int length){
        Location location=p.getLocation().clone();
        Entity entity=p.getWorld().spawnEntity(location,EntityType.ARMOR_STAND);
        entity.setInvulnerable(true);
        RunnableEntity aaa=new RunnableEntity(entity,location,p,length);
        aaa.run();
        entity.remove();
        return  aaa.getEntity();
    }
}
