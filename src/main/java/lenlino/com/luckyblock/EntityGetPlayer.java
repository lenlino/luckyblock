package lenlino.com.luckyblock;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public class EntityGetPlayer {
    static Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 0.6F);
    @Nullable
    public static Entity getStraightEntity(Player p,int length){
        Location location=p.getLocation().clone();
        Entity entity=p.getWorld().spawnEntity(location,EntityType.ARMOR_STAND);
        entity.setInvulnerable(true);
        Location l=entity.getLocation().clone();
        l.setY(l.getY()+1.6);
        for(int i=0;i<length;i++){
            if(!l.getBlock().getType().isAir()){
                entity.remove();
                return null;
            }
            List<Entity> entities=entity.getNearbyEntities(0.5,0.5,0.1);
            if(!(entities.size()==0)){
                for(Entity entity1:entities){
                    if(entity1 instanceof Player){
                        if(!((Player)entity1).getName().equals(p.getName())){
                            entity.remove();
                            return entity1;
                        }
                    }else if(entity1 instanceof LivingEntity){
                        entity.remove();
                        return entity1;
                    }
                }
            }
            location.add(location.getDirection());
            l.add(l.getDirection());
            entity.teleport(location);
            entity.getWorld().spawnParticle(Particle.REDSTONE,l,20,dustOptions);
        }
        entity.remove();
        return null;
    }
}
