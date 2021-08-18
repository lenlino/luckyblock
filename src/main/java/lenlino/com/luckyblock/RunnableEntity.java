package lenlino.com.luckyblock;

import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.List;

public class RunnableEntity{
    int length;
    Entity entity;
    Location location;
    Player p;
    Entity entity1=null;
    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 0.6F);
    public RunnableEntity(Entity entity,Location location,Player p,int length){
        this.entity=entity;
        this.location=location;
        this.p=p;
        this.length=length;
    }
    public void run() {
        Location l=entity.getLocation().clone();
        l.setY(l.getY()+1.6);
        for(int i=0;i<length;i++){
            if(!l.getBlock().getType().isAir()){
                return;
            }
            List<Entity> entities=entity.getNearbyEntities(0.5,0.5,0.1);
            if(!(entities.size()==0)){
                for(Entity entity1:entities){
                    if(entity1 instanceof Player){
                        if(!((Player)entity1).getName().equals(p.getName())){
                            this.entity1=entity1;
                            return;
                        }
                    }else if(entity1 instanceof LivingEntity){
                        this.entity1=entity1;
                        return;
                    }
                }
            }
            location.add(location.getDirection());
            l.add(l.getDirection());
            entity.teleport(location);
            entity.getWorld().spawnParticle(Particle.REDSTONE,l,20,dustOptions);
        }
    }
    @Nullable
    public Entity getEntity() {
        return entity1;
    }
}
