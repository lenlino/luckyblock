package lenlino.com.luckyblock;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class EntityGetPlayer {
    static Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 0.6F);
    static Particle.DustOptions dustOptions1 = new Particle.DustOptions(Color.BLUE, 0.6F);
    static Random random=new Random();
    @Nullable
    public static Entity getStraightEntity(Player p,int length,boolean Type){
        Location location=p.getLocation().clone();
        ArmorStand entity=(ArmorStand)p.getWorld().spawnEntity(location,EntityType.ARMOR_STAND);
        entity.setInvulnerable(true);
        entity.setSmall(true);
        Location l=entity.getLocation().clone();
        l.setY(l.getY()+1.6);
        for(int i=0;i<length;i++){
            if(!l.getBlock().getType().isAir()){
                if(Type) {
                    entity.remove();
                    return null;
                }else {
                    location.setDirection(InversionVector(location.getDirection()));
                    l.setDirection(InversionVector(l.getDirection()));
                }
            }
            List<Entity> entities=entity.getNearbyEntities(0.5,0.5,0.1);
            if(!(entities.size()==0)){
                for(Entity entity1:entities){
                    if(entity1 instanceof Player){
                        if(!entity1.getName().equals(p.getName())){
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
            entity.getWorld().spawnParticle(Particle.REDSTONE,l,20,Type?dustOptions:dustOptions1);
        }
        entity.remove();
        return null;
    }
    private static Vector InversionVector(Vector vector){
        switch (random.nextInt(3)){
            case 0:
                vector.setX(vector.getX()*-1);
                break;
            case 1:
                vector.setY(vector.getY()*-1);
                break;
            case 2:
                vector.setZ(vector.getZ()*-1);
                break;
        }
        return vector;
    }
}
