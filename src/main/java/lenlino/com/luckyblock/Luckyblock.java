package lenlino.com.luckyblock;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.*;
import java.util.*;

import static org.bukkit.Bukkit.*;


public final class Luckyblock extends JavaPlugin {
    Plugin plugin=this;
    ArrayList<risuto> i=new ArrayList<>();
    FileConfiguration fc=getConfig();
    Random random=new Random();
    Map<EntityType,Double> SpawnEnetities=new HashMap<>();
    boolean IsWorldGuard=true;
    WorldGuardPlugin worldGuardPlugin;
    RegionContainer container;
    RegionQuery query;
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
            Player p = Bukkit.getPlayer(args[0]);
            p.getWorld().dropItem(p.getLocation(), createskull(Integer.parseInt(args[1])));
        }else if (cmd.getName().equalsIgnoreCase("lbdo")) {
            if(args.length==1) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                        if (args[0].matches("[0-9]*")) {
                            if (Integer.parseInt(args[0]) <= i.size() && 0 < Integer.parseInt(args[0])) {
                                i.get(Integer.parseInt(args[0]) - 1).onigiri(new BlockBreakEvent(p.getLocation().getBlock(), p));
                            } else {
                                sender.sendMessage("指定された数がおかしいです。最大値:" + i.size());
                            }
                        } else {
                            if (fc.contains(args[0])) {
                                BlockBreakEvent event = new BlockBreakEvent(p.getLocation().getBlock(), p);
                                for (ItemStack item : (ArrayList<ItemStack>) fc.get(args[0])) {
                                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), item);
                                }
                            } else {
                                sender.sendMessage("指定された文字が不正です");
                            }
                        }
                } else {
                    sender.sendMessage("コンソール側からやコマンドブロックからこのコマンドを実行しないでください");
                }
            }else{
                if(args[0].matches("[0-9]*")){
                    if (Integer.parseInt(args[0]) <= i.size() && 0 < Integer.parseInt(args[0])) {
                        Player p=Bukkit.getPlayer(args[1]);
                        i.get(Integer.parseInt(args[0]) - 1).onigiri(new BlockBreakEvent(p.getLocation().getBlock(), p));
                    } else {
                        sender.sendMessage("指定された数がおかしいです。最大値:" + i.size());
                    }
                }else{
                    if (fc.contains(args[0])) {
                        BlockBreakEvent event = new BlockBreakEvent(Bukkit.getPlayer(args[1]).getLocation().getBlock(), Bukkit.getPlayer(args[1]));
                        for (ItemStack item : (ArrayList<ItemStack>) fc.get(args[0])) {
                            event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), item);
                        }
                    } else {
                        sender.sendMessage("指定された文字が不正です");
                    }
                }
            }
        }else if(cmd.getName().equalsIgnoreCase("lbadd")){
            if(!fc.contains(args[0])) {
                if (sender instanceof Player) {
                    if(!args[0].matches("[0-9]*")) {
                        Inventory inv = Bukkit.createInventory(null, 27, "§lAdd Lucky Item:" + args[0]);
                        ((Player) sender).openInventory(inv);
                    }else{
                        sender.sendMessage("名前が、数字だけです");
                    }
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
                sender.sendMessage("見つかりませんでした");
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
            EnderCrystal enderCrystal=(EnderCrystal)b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(), EntityType.ENDER_CRYSTAL);
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
        i.add(b -> {
            ItemStack i = new ItemStack(Material.EGG);
            ItemMeta meta = i.getItemMeta();
            meta.setDisplayName("§c§lhand grenade");
            i.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),i);
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
        i.add(b->{
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.SNOWBALL,16));
        });
        i.add(b->{
            FireworkEffect.Type[] types={FireworkEffect.Type.STAR, FireworkEffect.Type.BURST, FireworkEffect.Type.BALL, FireworkEffect.Type.BALL_LARGE, FireworkEffect.Type.CREEPER};
           for(int i=0;i<20;i++){
               Location location=b.getBlock().getLocation();
               location.setX(location.getX()-2+random.nextInt(5));
               location.setZ(location.getZ()-2+random.nextInt(5));
               Firework firework=(Firework)b.getBlock().getWorld().spawnEntity(location,EntityType.FIREWORK);
               FireworkMeta meta= firework.getFireworkMeta();
               meta.setPower(2);
               meta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(random.nextInt(255),random.nextInt(255),random.nextInt(255))).with(types[random.nextInt(types.length)]).build());
               firework.setFireworkMeta(meta);
           }
        });
        i.add(b->{
           Location location=b.getPlayer().getLocation();
           location.setY(location.getY()-1);
           location.setX(location.getX()-1);
           location.setZ(location.getZ()-1);
           for(int i=0;i<3;i++){
               for(int j=0;j<3;j++){
                   for(int k=0;k<4;k++){
                       if((k==1||k==2)&&i==1&&j==1) {
                           b.getPlayer().getWorld().getBlockAt(location).setType(Material.WATER);
                       }else{
                           b.getPlayer().getWorld().getBlockAt(location).setType(Material.OBSIDIAN);
                       }
                       location.setY(location.getY()+1);
                   }
                   location.setY(location.getY()-4);
                   location.setX(location.getX()+1);
               }
               location.setX(location.getX()-3);
               location.setZ(location.getZ()+1);
           }
        });
        i.add(b->{
           ItemStack item=new ItemStack(Material.APPLE);
           ItemMeta meta=item.getItemMeta();
           meta.setDisplayName("§e§lBigApple");
           item.setItemMeta(meta);
           b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            TNTPrimed tnt=(TNTPrimed)b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.PRIMED_TNT);
            tnt.setYield(tnt.getYield()*2);
        });
        i.add(b->{
            Location location=b.getPlayer().getLocation();
           for(int j=-2;j<3;j++){
               for(int k=-2;k<3;k++){
                   b.getPlayer().getWorld().getBlockAt((int)location.getX()+j,(int)location.getY(),(int)location.getZ()+k).setType(Material.LAVA);
               }
           }
        });
        i.add(b->{
           ItemStack item=new ItemStack(Material.EGG);
           ItemMeta meta=item.getItemMeta();
           meta.setDisplayName("§c§lFang Egg");
           item.setItemMeta(meta);
           item.setAmount(8);
           b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
           b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.SADDLE,2));
        });
        i.add(b->{
           for(int i=0;i<5;i++){
               b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.SNOWMAN);
           }
        });
        i.add(b->{
            for(int i=0;i<5;i++){
                b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.IRON_GOLEM);
            }
        });
        i.add(b->{
            for(int i=0;i<5;i++){
                b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.CAT);
            }
        });
        i.add(b->{
           b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),createskull(2));
        });
        i.add(b->{
           b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.NETHER_STAR));
        });
        i.add(b->{
           ItemStack item=new ItemStack(Material.ENDER_CHEST);
           ItemMeta meta=item.getItemMeta();
           meta.setDisplayName("§c§lEnder Chest");
           item.setItemMeta(meta);
           b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b -> {
            ItemStack item = new ItemStack(Material.GOLDEN_SWORD);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cEmeraldSword");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemDrop(Material.TOTEM_OF_UNDYING,2,b);
        });
        i.add(b->{
            Location location=b.getBlock().getLocation();
            for(int j=0;j<3;j++){
                for(int k=0;k<3;k++){
                    for(int nu=0;nu<3;nu++){
                        location.getBlock().setType(Material.REDSTONE_BLOCK);
                        location.setY(location.getY()+1);
                    }
                    location.setY(location.getY()-3);
                    location.setX(location.getX()+1);
                }
                location.setX(location.getX()-3);
                location.setZ(location.getZ()+1);
            }
        });
        i.add(b->{
            b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.LLAMA);
        });
        i.add(b->{
            Location location=b.getBlock().getLocation();
            location.setY(location.getY()+10);
            b.getBlock().getWorld().spawnFallingBlock(location, new MaterialData(Material.DIAMOND_BLOCK));
        });
        i.add(b->{
            Location location=b.getBlock().getLocation();
            location.setY(location.getY()+10);
            b.getBlock().getWorld().spawnFallingBlock(location, new MaterialData(Material.GOLD_BLOCK));
        });
        i.add(b->{
            Location location=b.getBlock().getLocation();
            location.setY(location.getY()+10);
            b.getBlock().getWorld().spawnFallingBlock(location, new MaterialData(Material.EMERALD_BLOCK));
        });
        i.add(b->{
            Location location=b.getBlock().getLocation();
            location.setY(location.getY()+10);
            b.getBlock().getWorld().spawnFallingBlock(location, new MaterialData(Material.IRON_BLOCK));
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.DIAMOND_SWORD);
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL,10);
            item.addUnsafeEnchantment(Enchantment.KNOCKBACK,5);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
           b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.ARROW,16));
        });
        i.add(b->{
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),new ItemStack(Material.BEACON,2));
        });
        i.add(b->{
           for(int j=0;j<10;j++){
               for(int k=0;k<10;k++){
                   b.getBlock().getWorld().getBlockAt(b.getBlock().getX()+j,b.getBlock().getY(),b.getBlock().getZ()+k).setType(Material.IRON_BLOCK);
               }
           }
        });
        i.add(b -> {
            ItemStack item = new ItemStack(Material.FIREWORK_ROCKET,5);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cRocket?");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemDrop(Material.PRISMARINE,64,b);
            ItemDrop(Material.CONDUIT,1,b);
        });
        i.add(b->{
           b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.PARROT);
        });
        i.add(b->{
            ItemStack item = new ItemStack(Material.STONE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cMagmaSponge");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
           ItemDrop(Material.SLIME_BLOCK,32,b);
        });
        i.add(b->{
            ItemStack item = new ItemStack(Material.STICK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cFallingBlockStick");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
           ItemStack item=new ItemStack(Material.DIAMOND_SWORD);
           ItemMeta meta=item.getItemMeta();
           meta.setDisplayName("§c強そうな剣");
           item.setItemMeta(meta);
           b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemDrop(Material.NAME_TAG,8,b);
        });
        i.add(b->{
           ItemDrop(Material.OAK_BOAT,2,b);
        });
        i.add(b->{
            for(Material material: new Material[]{Material.MAGMA_BLOCK, Material.ICE, Material.PACKED_ICE}){
                ItemDrop(material,10,b);
            }
        });
        i.add(b->{
            Villager villager= (Villager) b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.VILLAGER);
            Villager.Type[] types={Villager.Type.JUNGLE, Villager.Type.DESERT, Villager.Type.PLAINS, Villager.Type.SAVANNA, Villager.Type.SNOW, Villager.Type.SWAMP, Villager.Type.TAIGA};
            villager.setVillagerType(types[random.nextInt(types.length)]);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.IRON_BLOCK,4);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§c携帯型アイアンゴーレム");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.SNOW_BLOCK,8);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§c携帯型スノーゴーレム");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
           ItemDrop(Material.ENDER_PEARL,16,b);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.BOW);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§cFallingBlockBow");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemDrop(Material.WHITE_BED,4,b);
        });
        i.add(b->{
           ItemDrop(Material.TORCH,64,b);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.STICK);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§cNoRideStick");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
           ItemDrop(Material.ENDER_EYE,16,b);
        });
        i.add(b->{
            Location location=b.getBlock().getLocation();
            location.setX(location.getX()-1);
            location.setZ(location.getZ()-1);
           for(int i=0;i<3;i++){
               for(int j=0;j<3;j++){
                   for(int k=0;k<3;k++){
                       b.getPlayer().getWorld().spawnFallingBlock(location,Material.OBSIDIAN.createBlockData());
                       location.setZ(location.getZ()+1);
                   }
                   location.setZ(location.getZ()-3);
                   location.setX(location.getX()+1);
               }
               location.setX(location.getX()-3);
               location.setY(location.getY()+1);
           }
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.ARROW,4);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§cInfinityArrow");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item = new ItemStack(Material.GOLDEN_SWORD);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cCoalSword");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item = new ItemStack(Material.TNT,8);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cTNT");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item = new ItemStack(Material.BEETROOT,16);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cBeetRoot");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item = new ItemStack(Material.MILK_BUCKET);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cInfinityMilk");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item = new ItemStack(Material.EMERALD);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cLuckyItem");
            List<String> stringArrayList=new ArrayList<>();
            stringArrayList.add(b.getPlayer().getName());
            meta.setLore(stringArrayList);
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemDrop(Material.COBWEB,8,b);
        });
        i.add(b->{
            ItemStack item = new ItemStack(Material.FEATHER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cなんかできそうな羽");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            for(int j=0;j<random.nextInt(3)+2;j++){
                i.get(random.nextInt(i.size())).onigiri(b);
            }
        });
        i.add(b->{
            ItemStack item = new ItemStack(Material.DIAMOND_AXE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cBigAxe");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            b.getBlock().setType(Material.CAKE);
        });
        i.add(b->{
            ItemStack item = new ItemStack(Material.EGG,4);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cCreeperEgg");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item = new ItemStack(Material.SHULKER_BOX);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cShulker Box");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemDrop(Material.BLAZE_ROD,16,b);
        });
        i.add(b->{
            b.getPlayer().setLevel(b.getPlayer().getLevel()+10);
        });
        i.add(b->{
            ItemStack item = new ItemStack(Material.STICK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cTPStick");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            Location location=b.getPlayer().getLocation();
            location.setY(location.getY()+100);
           b.getPlayer().teleport(location);
        });
        i.add(b->{
            ItemDrop(Material.COMPASS,1,b);
        });
        i.add(b->{
           b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.ELDER_GUARDIAN);
        });
        i.add(b->{
            b.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,400,5));
            Location location=b.getPlayer().getLocation();
            location.setY(location.getY()-1);
            location.setX(location.getX()-1);
            location.setZ(location.getZ()-1);
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    for(int k=0;k<4;k++){
                        if(!((k==1||k==2)&&i==1&&j==1)) {
                            b.getPlayer().getWorld().getBlockAt(location).setType(Material.GLASS);
                        }
                        location.setY(location.getY()+1);
                    }
                    location.setY(location.getY()-4);
                    location.setX(location.getX()+1);
                }
                location.setX(location.getX()-3);
                location.setZ(location.getZ()+1);
            }
        });
        i.add(b->{
           for(PotionEffectType type:PotionEffectType.values()){
               b.getPlayer().addPotionEffect(new PotionEffect(type,100,10));
           }
        });
        i.add(b->{
            Location location=b.getBlock().getLocation();
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    for(int k=0;k<3;k++){
                        b.getPlayer().getWorld().getBlockAt(location).setType(Material.DIAMOND_BLOCK);
                        location.setY(location.getY()+1);
                    }
                    location.setY(location.getY()-3);
                    location.setX(location.getX()+1);
                }
                location.setX(location.getX()-3);
                location.setZ(location.getZ()+1);
            }
        });
        i.add(b->{
           ItemStack item=new ItemStack(Material.IRON_SHOVEL);
           ItemMeta meta=item.getItemMeta();
           meta.setDisplayName("§cBigShovel");
           item.setItemMeta(meta);
           b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.POTION);
            PotionMeta meta=(PotionMeta)item.getItemMeta();
            meta.setDisplayName("§f§lMilk");
            meta.setColor(Color.WHITE);
            ArrayList<String> list=new ArrayList<>();
            list.add(ChatColor.RESET+"§fバケツいっぱいの牛乳だと体を壊しそうだから作った");
            list.add(ChatColor.RESET+"§f飲んだところで何か起きるわけではない");
            meta.setLore(list);
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.STICK);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§cこうふ銃");
            ArrayList<String> list=new ArrayList<>();
            list.add(ChatColor.RESET+"§fこうふさん特製の銃を工場で作った銃");
            meta.setLore(list);
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
           b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.SHEEP);
        });
        i.add(b->{
           ItemDrop(Material.DIRT,16,b);
            ItemDrop(Material.GRASS_BLOCK,16,b);
            ItemDrop(Material.PODZOL,16,b);
            ItemDrop(Material.COARSE_DIRT,16,b);
            ItemDrop(Material.MYCELIUM,16,b);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.EMERALD_BLOCK,8);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§c携帯型村人");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.STICK);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§cとうふ銃");
            ArrayList<String> list=new ArrayList<>();
            list.add(ChatColor.RESET+"§fこうふさんがおもろそうと思って作った銃");
            list.add(ChatColor.RESET+"§f何かに当たると変な方向に行ってしまうというデメリット付き");
            meta.setLore(list);
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
           ItemDrop(Material.OAK_LOG,16,b);
        });
        i.add(b->{
            ItemDrop(Material.ACACIA_LOG,16,b);
        });
        i.add(b->{
            ItemDrop(Material.BIRCH_LOG,16,b);
        });
        i.add(b->{
            ItemDrop(Material.DARK_OAK_LOG,16,b);
        });
        i.add(b->{
            ItemDrop(Material.JUNGLE_LOG,16,b);
        });
        i.add(b->{
            ItemDrop(Material.SPRUCE_LOG,16,b);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.FIREWORK_ROCKET);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§c無限大の可能性がある花火");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.OAK_LOG,16);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("オークの原木?§c");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.BOW);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§cSuperTNTBow");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemDrop(Material.END_CRYSTAL,7,b);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.MILK_BUCKET);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§cとうふ牛乳");
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        i.add(b->{
            ItemStack item=new ItemStack(Material.SPLASH_POTION);
            PotionMeta meta=(PotionMeta) item.getItemMeta();
            meta.setDisplayName("§c無限水");
            meta.setBasePotionData(new PotionData(PotionType.WATER));
            item.setItemMeta(meta);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
        });
        getServer().getPluginManager().registerEvents(new LuckyBlockEvent(this), this);
        File EntityData=new File(getDataFolder(),"EntityData.txt");
        try{
            if(!EntityData.exists()){
                EntityData.createNewFile();
                try(FileWriter fw=new FileWriter(EntityData)){
                    for(EntityType type:EntityType.values()){
                        if(type.isAlive()) {
                            fw.write(type.name() + ":0.01" + System.lineSeparator());
                        }
                    }
                    fw.flush();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        try(BufferedReader br=new BufferedReader(new FileReader(EntityData))){
            String str;
            while((str=br.readLine())!=null){
                if(str.matches("[A-Z]+:[0-9\\.]+")) {
                    String[] strs = str.split(":", 2);
                    if (strs.length != 2) {
                        getLogger().info("ファイルの内容がおかしいです");
                    } else {
                        SpawnEnetities.put(EntityType.valueOf(strs[0]), Double.parseDouble(strs[1]));
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        /*
        使うファイルはcommands.txt
        ブロックを壊した人を指定するときは%player%を使う
        実行したいコマンドを打つ(初めの/はいらない)
        複数のコマンドを実行させたいときは{}を
        {
        give %player% sand 1
        say %player% example
        }
        このように使う
        */
        File file=new File(getDataFolder().getPath()+"/commands.txt");
        if(file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String str;
                int number = 0;
                ArrayList<ArrayList<String>> commands = new ArrayList<>();
                boolean IsData = false;
                boolean IsFirst = false;
                while ((str = br.readLine()) != null) {
                    if (str.equals("{")) {
                        if (IsData) {
                            Bukkit.getLogger().warning("かっこが二重以上になっています");
                            return;
                        } else {
                            IsData = true;
                            IsFirst = true;
                        }
                        number++;
                    } else if (str.equals("}")) {
                        number--;
                        IsData = false;
                    } else {
                        if (IsData) {
                            if (IsFirst) {
                                IsFirst = false;
                                ArrayList<String> command = new ArrayList<>();
                                command.add(str);
                                commands.add(command);
                            } else {
                                commands.get(commands.size() - 1).add(str);
                            }
                        } else {
                            ArrayList<String> command = new ArrayList<>();
                            command.add(str);
                            commands.add(command);
                        }
                    }
                }
                if (number != 0) {
                    getLogger().warning("{と}の数が同じじゃないです");
                } else {
                    for (ArrayList<String> command : commands) {
                        i.add(b -> {
                            for (String command1 : command) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command1.replace("%player%", b.getPlayer().getName()));
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try{
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    Plugin plugin1=Bukkit.getPluginManager().getPlugin("WorldGuard");
        if(plugin1==null||!(plugin1 instanceof WorldGuardPlugin)){
            IsWorldGuard=false;
        }else{
            worldGuardPlugin=WorldGuardPlugin.inst();
            container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            query = container.createQuery();
        }
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
        e.getBlock().getWorld().dropItem(e.getBlock().getLocation(),new ItemStack(i,a));
    }
}