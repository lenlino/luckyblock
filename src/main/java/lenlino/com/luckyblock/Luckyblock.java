package lenlino.com.luckyblock;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.maven.artifact.repository.metadata.Metadata;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;

import java.io.File;
import  java.io.IOException;
import java.util.*;

import static org.bukkit.Bukkit.*;


public final class Luckyblock extends JavaPlugin {

    Plugin plugin=this;
    ArrayList<risuto> i=new ArrayList<risuto>();



    public class BlockBreak implements Listener {
        //ブロック破壊されたとき
        @EventHandler
        public void breakblock(BlockBreakEvent b) {
            if(b.getBlock().hasMetadata("lucky")){
                b.getBlock().setType(Material.AIR);
                b.getBlock().removeMetadata("lucky",plugin);
                i.get((new Random()).nextInt(i.size())).onigiri(b);
                b.getBlock().getWorld().spawnParticle(Particle.EXPLOSION_LARGE,b.getBlock().getLocation(),1);
                b.getBlock().getWorld().playSound(b.getPlayer().getLocation(),Sound.ENTITY_SPLASH_POTION_BREAK,100,1);
            }
        }
        @EventHandler
        public void placeblock(BlockPlaceEvent b) {

            if (b.getItemInHand().getItemMeta().getDisplayName().equals("§lluckyblock")) {
                b.getBlock().setMetadata("lucky", new FixedMetadataValue(plugin,b.getBlock().getLocation().clone()));

            }
        }
        @EventHandler
        public void PlayerEntityShootBowEvent(EntityShootBowEvent e){
            if (e.getBow().getItemMeta().getDisplayName().equals("§lPlayerBow")) {
                e.getProjectile().addPassenger(e.getEntity());
            } else if (e.getBow().getItemMeta().getDisplayName().equals("§lTNTBow")) {
                e.getProjectile().setMetadata("TNTarrow", new FixedMetadataValue(plugin,e.getProjectile().getLocation().clone()));
            } else if (e.getBow().getItemMeta().getDisplayName().equals("§lByeBow") && e.getEntity().getNearbyEntities(6,6,6).size()!=0) {
                List<Entity> near = e.getEntity().getNearbyEntities(5,5,5);
                near.get(0).setInvulnerable(true);
                e.getProjectile().addPassenger(near.get(0));
                near.get(0).setInvulnerable(false);
            }
        }
        @EventHandler
        public void TNTBowHitEvent(ProjectileHitEvent e){
            if(e.getEntity().hasMetadata("TNTarrow")) {
                if (e.getHitEntity() == null) {
                    Location location = e.getEntity().getLocation();
                    location.setY(e.getEntity().getLocation().getY() - 0.25);
                    e.getHitBlock().getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
                    e.getEntity().removeMetadata("TNTarrow", plugin);
                } else {
                    Location location = e.getHitEntity().getLocation();
                    location.setY(location.getY() - 0.25);
                    e.getHitEntity().getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
                    e.getEntity().removeMetadata("TNTarrow", plugin);
                }
            }
        }
        @EventHandler
        public void ClickEvent(PlayerInteractEvent e){
            if(e.getItem()!=null) {
                if (e.getItem().getItemMeta().getDisplayName().equals("§a§lkoufuのパン")) {
                    if (e.getItem().getItemMeta().getLore().size() == 3) {
                        if (e.getItem().getItemMeta().getLore().get(2).equals("create by koufu")) {
                            if (e.getPlayer().getFoodLevel() < 20) {
                                e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel() + 5);
                            } else {
                                e.getPlayer().sendMessage("§cもうお腹がいっぱいです");
                            }
                        }
                    }
                } else if (e.getItem().getItemMeta().getDisplayName().equals("§e雷の杖(未完成)") && Objects.requireNonNull(e.getClickedBlock()).getType() == (Material.GOLD_BLOCK)) {
                    e.getItem().setAmount(e.getItem().getAmount() - 1);
                    e.getClickedBlock().getWorld().strikeLightningEffect(e.getClickedBlock().getLocation());
                    ItemStack item = new ItemStack(Material.STICK);
                    ItemMeta meta = item.getItemMeta();
                    ArrayList<String> lis = new ArrayList<String>();
                    lis.add("経験値を1レべ消費して雷が打てる");
                    meta.setDisplayName("§e§l雷の杖");
                    meta.addEnchant(Enchantment.DURABILITY, 1, true);
                    meta.setLore(lis);
                    item.setItemMeta(meta);
                    e.getPlayer().getInventory().addItem(item);
                } else if (e.getItem().getItemMeta().getDisplayName().equals("§e§l雷の杖") && e.getItem().getItemMeta().getLore().get(0).equals("経験値を1レべ消費して雷が打てる")) {
                    Block focusBlock = getCursorFocusBlock(e.getPlayer());
                    if (1 <= e.getPlayer().getLevel()) {
                        if (focusBlock != null) {
                            focusBlock.getWorld().strikeLightning(focusBlock.getLocation());
                            e.getPlayer().giveExpLevels(-1);
                        } else {
                            e.getPlayer().sendMessage("§4100ブロック以内に空気以外のブロックが見つかりませんでした");
                        }
                    } else {
                        e.getPlayer().sendMessage("§4経験値が足りません");
                    }
                } else if(e.getItem().getItemMeta().getDisplayName().equals("§l食べれる紙") && e.getPlayer().getFoodLevel()!=20) {
                    e.getItem().setAmount(e.getItem().getAmount()-1);
                    e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel()+2);
                } else if(e.getItem().getItemMeta().getDisplayName().equals("§lDon't read!")) {
                    e.getPlayer().setMetadata("LuckyDead",new FixedMetadataValue(plugin,e.getPlayer().getLocation().clone()));
                    e.getPlayer().setHealth(0);
                } else if(e.getItem().getItemMeta().getDisplayName().equals("§lBighoe") && e.getAction()==Action.RIGHT_CLICK_BLOCK && (e.getClickedBlock().getType()==Material.GRASS_BLOCK || e.getClickedBlock().getType()==Material.DIRT))  {
                    Location l = e.getClickedBlock().getLocation();
                    l.setX(l.getX()-1);
                    l.setZ(l.getZ()-1);
                    for (int x = 0;x <3;x++) {
                        for (int y = 0;y<3; y++) {
                            if (x!=1||y!=1) {
                                if (l.getBlock().getType()==Material.GRASS_BLOCK) {
                                    l.getBlock().setType(Material.FARMLAND);
                                }else if(l.getBlock().getType()==Material.DIRT) {
                                    l.getBlock().setType(Material.FARMLAND);
                                }
                            }
                            l.setX(l.getX()+1);
                        }
                        l.setX(l.getX()-3);
                        l.setZ(l.getZ()+1);
                    }
                } else if(e.getItem().getItemMeta().getDisplayName().equals("§lFireStick")) {
                    Fireball living=(Fireball) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getLocation(),EntityType.FIREBALL);
                    living.setVelocity(e.getPlayer().getVelocity());
                    Location location=living.getLocation();
                    location.setY(location.getY()+1);
                    location.add(e.getPlayer().getVelocity());
                    living.teleport(location);
                }
            }
        }
        @EventHandler
        public void WaterEvent(PlayerBucketEmptyEvent e){
            e.setCancelled(true);
            if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§c§lInfiniteWaterBucket")) {
                e.getBlock().setType(Material.WATER);
            }else{
                e.setCancelled(false);
            }
        }
        @EventHandler
        public void DeathPlayerEvent(PlayerDeathEvent e){
            if(e.getEntity().hasMetadata("LuckyDead")){
                e.setDeathMessage(e.getEntity().getDisplayName()+"は本の物理攻撃によって殺された");
                e.getEntity().removeMetadata("LuckyDead",plugin);
            }
        }
        private Block getCursorFocusBlock(Player player) {
            BlockIterator blocks = new BlockIterator(player, 100);
            while (blocks.hasNext()) {
                Block block = blocks.next();
                if ( block.getType() != Material.AIR ) {
                    return block;
                }
            }
            return null;
        }
        @EventHandler
        public void EatBreadEvent(PlayerItemConsumeEvent e){
            ItemStack item = new ItemStack(Material.BREAD);
            ItemMeta meta = item.getItemMeta();
            ArrayList<String> lis=new ArrayList<String>();
            lis.add("koufuが作ったパン");
            lis.add("水が欲しくなる");
            lis.add("create by koufu");
            meta.setDisplayName("§a§lkoufuのパン");
            meta.addEnchant(Enchantment.DURABILITY,1,true);
            meta.setLore(lis);
            item.setItemMeta(meta);
            if(e.getItem().isSimilar(item)){
                i.get(10).onigiri(new BlockBreakEvent(e.getPlayer().getLocation().getBlock(),e.getPlayer()));
            }
        }
        @EventHandler
        public void TNTJoinEvent(PlayerJoinEvent e){
            if(e.getPlayer().hasMetadata("TNT")){
                for(int i=0;i<10;i++) {
                    Entity tnt = e.getPlayer().getWorld().spawnEntity(e.getPlayer().getLocation(), EntityType.PRIMED_TNT);
                    e.getPlayer().addPassenger(tnt);
                }
                e.getPlayer().removeMetadata("TNT",plugin);
            }
        }
        @EventHandler
        public void CreatureSpawnEvent(CreatureSpawnEvent e) {
            double d;
            d = Math.random();
            List<EntityType> spawn = new ArrayList<EntityType> ();
            spawn.add(EntityType.ZOMBIE);
            spawn.add(EntityType.SKELETON);
            spawn.add(EntityType.ZOMBIFIED_PIGLIN);
            spawn.add(EntityType.CREEPER);
            if (d>0.99 && e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) {
                e.getEntity().setMetadata("mob", new FixedMetadataValue(plugin,e.getEntity().getLocation().clone()));
                e.getEntity().setCustomName("LuckyMob");
                e.getEntity().setCustomNameVisible(true);
            }
        }

        @EventHandler
        public void dropevent(EntityDeathEvent e) {
            if (e.getEntity().hasMetadata("mob")) {
                e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), createskull(1));
            }
        }

        @EventHandler
        public void damageevent(EntityDamageByEntityEvent e) {
            if (e.getDamager().getType().isAlive()) {
                LivingEntity l = (LivingEntity) e.getDamager();
                if (l.getEquipment().getItemInMainHand().hasItemMeta()) {
                    if (l.getEquipment().getItemInMainHand().getItemMeta().getDisplayName().equals("§lFlySword")) {
                        LivingEntity j = (LivingEntity) e.getEntity();
                        PotionEffect p = new PotionEffect(PotionEffectType.LEVITATION, 10, 30);
                        j.addPotionEffect(p);
                    } else if (l.getEquipment().getItemInMainHand().getItemMeta().getDisplayName().equals("§lRideStick")) {
                        e.getEntity().addPassenger(e.getDamager());
                    } else if (l.getEquipment().getItemInMainHand().getItemMeta().getDisplayName().equals("§cDGoldSword")) {
                        ItemStack item = new ItemStack(Material.GOLD_NUGGET,1);
                        e.getEntity().getWorld().dropItem(e.getEntity().getLocation(),item);
                    } else if (l.getEquipment().getItemInMainHand().getItemMeta().getDisplayName().equals("§a§lHealStick") && e.getEntityType()==EntityType.PLAYER) {
                        LivingEntity j = (LivingEntity) e.getEntity();
                        j.setHealth(((LivingEntity) e.getEntity()).getHealth()+3);
                    } else if (l.getEquipment().getItemInMainHand().getItemMeta().getDisplayName().equals("§7§lBone of life") && e.getEntityType().equals(EntityType.ZOMBIE_VILLAGER)) {
                        e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(),EntityType.VILLAGER);
                        e.getEntity().remove();
                    }
                }
            }
        }
    }
    @EventHandler
    public void getWaterEvent(PlayerBucketFillEvent e){
        e.setCancelled(true);
        if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§c§lInfiniteNoneBucket")){
            e.getBlock().setType(Material.AIR);
        }else{
            e.setCancelled(false);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lbget")) {
            if(sender instanceof  Player) {
                Player p = (Player) sender;
                p.getInventory().addItem(createskull(Integer.parseInt(args[0])));
            }else{
                System.out.println("コンソール側からやコマンドブロックからこのコマンドを実行しないでください");
            }
        } else if (cmd.getName().equalsIgnoreCase("lbgive")) {
            Player p = getPlayer(args[0]);
            p.getWorld().dropItem(p.getLocation(), createskull(Integer.parseInt(args[1])));
        }else if (cmd.getName().equalsIgnoreCase("lbdo")) {
            if(sender instanceof Player) {
                if (Integer.parseInt(args[0]) < i.size() && Integer.parseInt(args[0]) >= 0) {
                    Player p = (Player) sender;
                    (i.get(Integer.parseInt(args[0]))).onigiri(new BlockBreakEvent(p.getLocation().getBlock(), p));
                } else {
                    sender.sendMessage("指定された数がおかしいです。最大値:" + (i.size() - 1));
                }
            }else{
                System.out.println("コンソール側からやコマンドブロックからこのコマンドを実行しないでください");
            }
        }
        return false;
    }

    @Override
    public void onEnable() {
        if(!getDataFolder().exists()){
            new File(String.valueOf(getDataFolder().toPath())).mkdir();
        }
        File f=new File(String.valueOf(getDataFolder()));
        File[] files=f.listFiles();       //牛召喚
        for(int j=0;j< files.length;j++){
            if(files[j].isFile()&&!(files[j].getName()).equals("magma.nbt")){
                int finalJ = j;
                i.add(b->{try {
                    Structure.placeStructure(files[finalJ], b.getBlock().getLocation(), false, false);
                } catch (IOException e) {
                    broadcastMessage(e.toString());
                }});
            }
        }
        //code by lenlino
        i.add(b->getWorld(b.getBlock().getWorld().getName()).spawnEntity(b.getBlock().getLocation(), EntityType.COW));
        //ゾンビ召喚
        i.add(b->getWorld(b.getBlock().getWorld().getName()).spawnEntity(b.getBlock().getLocation(), EntityType.ZOMBIE));
        //ストレイTNT
        i.add(b->{
            for (int j = 0;j<10;j++) {
                Entity vex = b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(), EntityType.VINDICATOR);
                Entity tnt = b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(), EntityType.PRIMED_TNT);
                vex.addPassenger(tnt);
            }
        });
        i.add(b->{
            for (int j = 0;j<10;j++) {
                Entity vex = b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(), EntityType.VEX);
                Entity tnt = b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(), EntityType.PRIMED_TNT);
                vex.addPassenger(tnt);
            }
        });
        //TNTアイテム
        i.add(b -> b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), new ItemStack(Material.TNT, 32)));
        i.add(b->{
            ItemStack item = new ItemStack(Material.BOW);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§lPlayerBow");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), item);
        });
        i.add(b -> {
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), new ItemStack(Material.DIAMOND, 15));
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), new ItemStack(Material.EMERALD, 15));
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT, 15));
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT, 15));
            Effect a = Effect.ANVIL_BREAK;
            b.getBlock().getWorld().playEffect(b.getBlock().getLocation(), a, 100);
        });
        i.add(b -> {
            ItemStack item = new ItemStack(Material.BOW);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§lByeBow");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), item);
        });
        i.add(b -> {
            ItemDrop(Material.GOLDEN_CHESTPLATE,1,b);
            ItemDrop(Material.GOLDEN_BOOTS,1,b);
            ItemDrop(Material.GOLDEN_HELMET,1,b);
            ItemDrop(Material.GOLDEN_LEGGINGS,1,b);
        });
        i.add(b -> {
            ItemDrop(Material.DIAMOND_CHESTPLATE,1,b);
            ItemDrop(Material.DIAMOND_BOOTS,1,b);
            ItemDrop(Material.DIAMOND_HELMET,1,b);
            ItemDrop(Material.DIAMOND_LEGGINGS,1,b);
        });
        i.add(b -> {
            ItemDrop(Material.LEATHER_CHESTPLATE,1,b);
            ItemDrop(Material.LEATHER_BOOTS,1,b);
            ItemDrop(Material.LEATHER_HELMET,1,b);
            ItemDrop(Material.LEATHER_LEGGINGS,1,b);
        });
        i.add(b -> {
            ItemDrop(Material.IRON_CHESTPLATE,1,b);
            ItemDrop(Material.IRON_BOOTS,1,b);
            ItemDrop(Material.IRON_HELMET,1,b);
            ItemDrop(Material.IRON_LEGGINGS,1,b);
        });
        i.add(b -> {
            ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§lDon't read!");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), item);
        });
        i.add(b -> {
            b.getBlock().getWorld().createExplosion(b.getBlock().getLocation(),3);
        });
        i.add(b -> {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§l食べれる紙");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), item);
        });
        i.add(b -> {
            ItemStack item = new ItemStack(Material.IRON_HOE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§lBighoe");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b -> {
            ItemStack item = new ItemStack(Material.LEATHER_BOOTS);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§lSuperSpeedBoots");
            AttributeModifier m = new AttributeModifier(UUID.randomUUID(),"speed", 10, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.FEET);
            meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,m);
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), item);
        });

        i.add(b -> {
            ItemStack item = new ItemStack(Material.ELYTRA);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("Armored Elytra");
            AttributeModifier m = new AttributeModifier(UUID.randomUUID(), "tough",8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR,m);
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), item);
        });

        i.add(b -> {
            for (int i = 0; i<6;i++) {
                Entity bat = b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(), EntityType.BAT);
                Entity s = b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(), EntityType.SKELETON);
                bat.addPassenger(s);
            }
        });

        i.add(b -> {
            ItemStack item = new ItemStack(Material.STICK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("bat");
            meta.addEnchant(Enchantment.KNOCKBACK,30, true);
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), item);
        });

        i.add(b -> {
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.RESET+"§lFlySword");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });

        i.add(b -> {
            ItemStack item = new ItemStack(Material.STICK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§lRideStick");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });

        i.add(b -> {
            EnderCrystal enderCrystal=(EnderCrystal)b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(), EntityType.ENDER_CRYSTAL);;
            enderCrystal.setRotation(200,200);

        });
        i.add(b -> {
            LivingEntity e = (LivingEntity) b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(), EntityType.SLIME);
            Slime slime=(Slime)e;
            slime.setSize(25);
        });

        i.add(b -> {
            ItemStack item = new ItemStack(Material.STICK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§lFireStick");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });

        i.add(b -> {
            ItemStack item = new ItemStack(Material.REDSTONE);
            ItemMeta meta = item.getItemMeta();
            AttributeModifier m = new AttributeModifier(UUID.randomUUID(),"quick", 10, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.OFF_HAND);
            meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH,m);
            meta.setDisplayName("§c§lHeart");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });

        i.add(b -> {
            for (int i =0;i<6;i++) {
                Wolf w = (Wolf) b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.WOLF);
                w.setAngry(true);
            }
        });

        i.add(b -> {
            for (int i=0;i<3;i++) {
                ItemStack item = new ItemStack(Material.BUCKET);
                ItemStack item2 = new ItemStack(Material.COD_BUCKET);
                ItemStack item3 = new ItemStack(Material.LAVA_BUCKET);
                ItemStack item4 = new ItemStack(Material.MILK_BUCKET);
                ItemStack item5 = new ItemStack(Material.PUFFERFISH_BUCKET);
                ItemStack item6 = new ItemStack(Material.SALMON_BUCKET);
                ItemStack item7 = new ItemStack(Material.TROPICAL_FISH_BUCKET);
                ItemStack item8 = new ItemStack(Material.WATER_BUCKET);
                b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
                b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item2);
                b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item3);
                b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item4);
                b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item5);
                b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item6);
                b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item7);
                b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item8);
            }
        });

        i.add(b -> {
            ItemStack item = new ItemStack(Material.WATER_BUCKET);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§c§lInfiniteWaterBucket");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b -> {
            ItemStack item = new ItemStack(Material.BUCKET);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§c§lInfiniteNoneBucket");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b -> {
            b.getBlock().getWorld().generateTree(b.getBlock().getLocation(),TreeType.JUNGLE);
        });

        i.add(b -> {
            b.getBlock().getWorld().strikeLightning(b.getPlayer().getLocation());
        });

        i.add(b -> {
            Horse h = (Horse) b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.HORSE);
            h.setJumpStrength(2);
            h.setTamed(true);
            h.setOwner(b.getPlayer());
            h.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(1);
            h.setCustomNameVisible(true);
            h.setCustomName("§cRampage horse");
        });

        i.add(b -> {
            for (int i=0;i<11;i++) {
                Wolf w = (Wolf) b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.WOLF);
                w.setOwner(b.getPlayer());
            }
        });

        i.add(b -> {
            ItemStack item = new ItemStack(Material.GOLDEN_SWORD);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cDGoldSword");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });

        i.add(b -> {
            ItemStack item = new ItemStack(Material.CARROT_ON_A_STICK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a§lHealStick");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });

        i.add(b -> {
            ItemStack item = new ItemStack(Material.BONE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§7§lBone of life");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });

        i.add(b -> {
            ItemStack item = new ItemStack(Material.ENCHANTING_TABLE,1);
            ItemStack item1 = new ItemStack(Material.BOOKSHELF,21);
            ItemStack item3 = new ItemStack(Material.LAPIS_LAZULI,32);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item1);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item3);
        });

        i.add(b -> {
            for (int i=0;i<11;i++){
                for (int p=0;p<11;p++) {
                    Location l = b.getBlock().getLocation().add(i,0,p);
                    l.getBlock().setType(Material.GOLD_BLOCK);
                }
            }
        });

        i.add(b -> {
            b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.PANDA);
        });

        i.add(b -> {
            ItemStack item = new ItemStack(Material.SHULKER_SHELL,5);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });

        i.add(b -> {
            PotionEffect p = new PotionEffect(PotionEffectType.GLOWING,1000,1);
            b.getPlayer().addPotionEffect(p);
        });

        i.add(b -> {
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.IRON_AXE));
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.STONE_AXE));
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.GOLDEN_AXE));
        });

        i.add(b -> {
            PotionEffect p = new PotionEffect(PotionEffectType.LEVITATION, 100, 30);
            PotionEffect p2 = new PotionEffect(PotionEffectType.SLOW_FALLING,500,1);
            b.getPlayer().addPotionEffect(p);
            b.getPlayer().addPotionEffect(p2);
        });


        //code by koufu193
        File data=new File(getDataFolder()+"/magma.nbt");
        if(data.exists()) {
            i.add(b -> {
                Location location = b.getPlayer().getLocation();
                location.setX(b.getPlayer().getLocation().getX() - 2);
                location.setZ(b.getPlayer().getLocation().getZ() - 2);
                location.setY(b.getPlayer().getLocation().getY() - 7);
                try {
                    Structure.placeStructure(data, location, false, false);
                } catch (IOException e) {
                    broadcastMessage(e.toString());
                }
            });
        }
        i.add(b->{
            ItemStack item = new ItemStack(Material.BOW);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§lTNTBow");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), item);
        });
        i.add(b->{ItemStack item = new ItemStack(Material.BREAD);
            ItemMeta meta = item.getItemMeta();
            ArrayList<String> lis=new ArrayList<String>();
            lis.add("koufuが作ったパン");
            lis.add("水が欲しくなる");
            lis.add("create by koufu");
            meta.setDisplayName("§a§lkoufuのパン");
            meta.addEnchant(Enchantment.DURABILITY,1,true);
            meta.setLore(lis);
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), item);
        });
        i.add(b -> {
            b.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,1000,3));
            b.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,1000,3));
        });
        i.add(b ->{
            b.getPlayer().setMetadata("TNT",new FixedMetadataValue(plugin,b.getBlock().getLocation().clone()));
            b.getPlayer().sendMessage("§cTNT");
            i.get((new Random()).nextInt(i.size())).onigiri(b);
        });
        i.add(b->{
            ItemStack item = new ItemStack(Material.STICK);
            ItemMeta meta = item.getItemMeta();
            ArrayList<String> lis=new ArrayList<String>();
            lis.add("充電しないといけない");
            meta.setDisplayName("§e雷の杖(未完成)");
            meta.addEnchant(Enchantment.DURABILITY,1,true);
            meta.setLore(lis);
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), item);
        });
        i.add(b->{
            b.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW,1000,1000));
        });
        i.add(b -> {
            Entity entity=b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(), EntityType.SKELETON);
            LivingEntity entity1 = (LivingEntity) entity;
            ItemStack item = new ItemStack(Material.BOW);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§lPlayerBow");
            item.setItemMeta(meta);
            entity1.getEquipment().setItemInMainHand(item);
            entity1.setAI(true);
        });
        getServer().getPluginManager().registerEvents(new BlockBreak(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public ItemStack createskull(Integer index) {
        String base64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWM5ZDVkNzhiM2ZlNzFjOWZhODk4MTk4OGY4MWNhMzdlYjlkZmFiYmY0NzdkZmI4OGNmMWJlN2U3YWFkMWUifX19";
        ItemStack skull = SkullCreator.itemFromBase64(base64,index);
        SkullMeta skullmeta = (SkullMeta)  skull.getItemMeta();
        skullmeta.setDisplayName("§lluckyblock");
        skull.setItemMeta(skullmeta);

        return skull;
    }

    public void ItemDrop(Material i,Integer a,BlockBreakEvent e) {
        ItemStack item = new ItemStack(i,a);
        e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), item);
    }
}