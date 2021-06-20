package lenlino.com.luckyblock;

import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.ItemArmorStand;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.maven.artifact.repository.metadata.Metadata;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

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
            } else if (e.getBow().getItemMeta().getDisplayName().equals("§lByeBow")) {
                List<Entity> near = e.getEntity().getNearbyEntities(5,5,5);
                Effect a = Effect.ANVIL_BREAK;
                e.getEntity().getWorld().playEffect(e.getEntity().getLocation(), a, 100);
                near.get(0).setInvulnerable(true);
                e.getProjectile().addPassenger(near.get(0));
                near.get(0).setInvulnerable(false);
            }
        }
        @EventHandler
        public void TNTBowHitEvent(ProjectileHitEvent e){
            if(e.getEntity().hasMetadata("TNTarrow")){
                if(e.getHitEntity()==null) {
                    Location location = e.getEntity().getLocation();
                    location.setY(e.getEntity().getLocation().getY() - 0.25);
                    e.getHitBlock().getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
                    e.getEntity().removeMetadata("TNTarrow", plugin);
                }else{
                    Location location =e.getHitEntity().getLocation();
                    location.setY(location.getY()-0.25);
                    e.getHitEntity().getWorld().spawnEntity(location,EntityType.PRIMED_TNT);
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
                } else if (e.getItem().getItemMeta().getDisplayName().equals("§e雷の杖(未完成)") && e.getItem().getItemMeta().getLore().get(0).equals("充電しないといけない") && e.getClickedBlock().getType() == (Material.GOLD_BLOCK)) {
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
                }else if(e.getItem().getItemMeta().getDisplayName().equals("§c§lデス回避棒")){
                    if(!e.getPlayer().hasMetadata("noDeath")) {
                        e.getItem().setAmount(e.getItem().getAmount() - 1);
                        e.getPlayer().sendMessage("デスノートの死に耐えられるようになった");
                        e.getPlayer().setMetadata("noDeath", new FixedMetadataValue(plugin, e.getPlayer().getLocation()));
                    }else{
                        e.getPlayer().sendMessage("§cあなたは既にデスノートの死から耐えれてます");
                    }
                }
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
        public void DamageEvent(EntityDamageEvent e){
            if(e.getEntityType().equals(EntityType.PLAYER)&&e.getEntity().hasMetadata("noDeath")&&e.getDamage()==999*999&&e.getEntity().getLocation().getWorld().getName().indexOf("_the_end")==-1){
                e.setCancelled(true);
                ((Player)e.getEntity()).sendMessage("死から回避しました");
            }
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
            if (d>0.99 && e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) {
                e.getEntity().setMetadata("mob", new FixedMetadataValue(plugin,e.getEntity().getLocation().clone()));
                e.getEntity().setCustomName("LuckyMob");
                e.getEntity().setCustomNameVisible(true);
            }
        }

        @EventHandler
        public void dropevent(EntityDropItemEvent e) {
            if (e.getEntity().hasMetadata("mob")) {
                e.setCancelled(true);
                e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), createskull(1));
            }
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

        //ウィザー
        i.add(b -> b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(), EntityType.WITHER));
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
        //デス回避
        i.add(b->{
            if(Math.random()*10<2) {
                ItemStack item = new ItemStack(Material.STICK);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§c§lデス回避棒");
                ArrayList<String> lore = new ArrayList<>();
                lore.add("デスの死から回避できる棒");
                lore.add("クリックすると消える");
                meta.setLore(lore);
                item.setItemMeta(meta);
                b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
            }else{
                i.get((new Random()).nextInt(i.size())).onigiri(b);
            }
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
}