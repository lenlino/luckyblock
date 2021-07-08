package lenlino.com.luckyblock;

import com.sun.imageio.plugins.common.SingleTileRenderedImage;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.maven.artifact.repository.metadata.Metadata;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
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
    FileConfiguration fc=getConfig();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lbget")) {
            if(sender instanceof  Player) {
                Player p = (Player) sender;
                p.getInventory().addItem(createskull(Integer.parseInt(args[0])));
            }else{
                sender.sendMessage("コンソール側からやコマンドブロックからこのコマンドを実行しないでください");
            }
        } else if (cmd.getName().equalsIgnoreCase("lbgive")) {
            Player p = getPlayer(args[0]);
            p.getWorld().dropItem(p.getLocation(), createskull(Integer.parseInt(args[1])));
        }else if (cmd.getName().equalsIgnoreCase("lbdo")) {
            if(sender instanceof Player) {
                if(args[0].matches("[0-9]*")){
                    if (Integer.parseInt(args[0]) <=i.size()&&0<Integer.parseInt(args[0])) {
                        Player p = (Player) sender;
                        (i.get(Integer.parseInt(args[0])-1)).onigiri(new BlockBreakEvent(p.getLocation().getBlock(), p));
                    } else {
                        sender.sendMessage("指定された数がおかしいです。最大値:" + i.size());
                    }
                }else{
                    if(fc.contains(args[0])){
                        if(sender instanceof Player) {
                            BlockBreakEvent event = new BlockBreakEvent(((Player)sender).getLocation().getBlock(), (Player)sender);
                            for (ItemStack item : (ArrayList<ItemStack>) fc.get(args[0])) {
                                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), item);
                            }
                        }else{
                            sender.sendMessage("コンソール側からやコマンドブロックからこのコマンドを実行しないでください");
                        }
                    }else{
                        sender.sendMessage("指定された文字が不正です");
                    }
                }
            }else{
                sender.sendMessage("コンソール側からやコマンドブロックからこのコマンドを実行しないでください");
            }
        }else if(cmd.getName().equalsIgnoreCase("lbadd")){
            if(!fc.contains(args[0])) {
                if (sender instanceof Player) {
                    Inventory inv = Bukkit.createInventory(null, 27, "§lAdd Lucky Item:" + args[0]);
                    ((Player) sender).openInventory(inv);
                } else {
                    sender.sendMessage("コンソール側からやコマンドブロックからこのコマンドを実行しないでください");
                }
            }else{
                sender.sendMessage("すでに同じ名前のものがあります");
            }
        }else if(cmd.getName().equalsIgnoreCase("lbremove")){
            if(fc.contains(args[0])){
                fc.set(args[0],null);
                sender.sendMessage("削除しました");
            }else{
                sender.sendMessage("キーが見つかりませんでした");
            }
        }else if(cmd.getName().equalsIgnoreCase("lbgetlist")){
            sender.sendMessage("要素数:"+fc.getKeys(false).size());
            for(String key:fc.getKeys(false)){
                sender.sendMessage(key);
            }
        }else if(cmd.getName().equalsIgnoreCase("lbreload")){
            if(sender.isOp()){
                sender.sendMessage("リロード中...");
                i.clear();
                onDisable();
                onEnable();
                sender.sendMessage("リロードが終了しました");
            }else{
                sender.sendMessage("権限がありません");
            }
        }
        return false;
    }

    @Override
    public void onEnable() {
        if(!getDataFolder().exists()){
            new File(String.valueOf(getDataFolder().toPath())).mkdir();
        }
        File[] files=getDataFolder().listFiles();       //牛召喚
        for(int j=0;j< files.length;j++){
            if(files[j].isFile()&&!(files[j].getName()).equals("magma.nbt")&&files[j].getName().matches(".*\\.nbt")){
                int finalJ = j;
                i.add(b->{try {
                    Structure.placeStructure(files[finalJ], b.getBlock().getLocation(), false, false);
                } catch (IOException e) {
                    broadcastMessage(e.toString());
                }});
            }
        }
        for(String key:fc.getKeys(false)){
            i.add(b->{
                if(fc.contains(key)) {
                    for (ItemStack item : (ArrayList<ItemStack>) fc.get(key)) {
                        b.getBlock().getWorld().dropItem(b.getBlock().getLocation(), item);
                    }
                }else{
                    i.get((new Random()).nextInt(i.size())).onigiri(b);
                }
            });
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
        i.add(b->{
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.ORANGE_TULIP,10));
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.OXEYE_DAISY,10));
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.WITHER_ROSE,10));
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.CHORUS_FLOWER,10));
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.ROSE_BUSH,10));
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.SUNFLOWER,10));
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.BLUE_ORCHID,10));
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.ALLIUM,10));
        });
        i.add(b->{
           ItemStack item=new ItemStack(Material.STICK);
           ItemMeta meta=item.getItemMeta();
           meta.setDisplayName("§e§lHeadStick");
           item.setItemMeta(meta);
           b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.SPONGE);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§e§lSPONGE");
            item.setItemMeta(meta);
           b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.DIAMOND_PICKAXE);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§e§lBigPickaxe");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        getServer().getPluginManager().registerEvents(new LuckyBlockEvent(this), this);
    }
    @Override
    public void onDisable() {
        saveConfig();
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