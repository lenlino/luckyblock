package lenlino.com.luckyblock;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.Flags;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.*;

public class LuckyBlockEvent implements Listener {
    Luckyblock luckyblock;
    Set<String> names=new HashSet<>();
    Material[] DontBlocks={
            Material.STRUCTURE_BLOCK,
            Material.STRUCTURE_VOID,
            Material.COMMAND_BLOCK,
            Material.CHAIN_COMMAND_BLOCK,
            Material.REPEATING_COMMAND_BLOCK,
            Material.BEDROCK,
            Material.END_PORTAL_FRAME,
            Material.END_PORTAL,
            Material.BARRIER,
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.HOPPER,
            Material.DISPENSER,
            Material.DROPPER,
            Material.IRON_DOOR,
            Material.IRON_TRAPDOOR,
            Material.JUKEBOX,
            Material.BEACON,
            Material.FURNACE,
            Material.ENCHANTING_TABLE,
    };
    BlockFace[] blockFaces={BlockFace.UP,BlockFace.DOWN,BlockFace.WEST,BlockFace.EAST,BlockFace.NORTH,BlockFace.SOUTH};
    HashMap<String,BreakMode> BigPicMode=new HashMap<>();
    HashSet<String> stringHashSet=new HashSet<>();
    HashMap<String,WoodBreakMood> WoodBreak=new HashMap<>();
    HashMap<Material,Material> Wood_With_Leaf=new HashMap<>();
    Set<String> WoodSet=new HashSet<>();
    public LuckyBlockEvent(Luckyblock luckyblock){
        this.luckyblock=luckyblock;
        Wood_With_Leaf.put(Material.OAK_LOG,Material.OAK_LEAVES);
        Wood_With_Leaf.put(Material.ACACIA_LOG,Material.ACACIA_LEAVES);
        Wood_With_Leaf.put(Material.BIRCH_LOG,Material.BIRCH_LEAVES);
        Wood_With_Leaf.put(Material.JUNGLE_LOG,Material.JUNGLE_LEAVES);
        Wood_With_Leaf.put(Material.DARK_OAK_LOG,Material.DARK_OAK_LEAVES);
        Wood_With_Leaf.put(Material.SPRUCE_LOG,Material.SPRUCE_LEAVES);
    }
    @EventHandler
    public void breakblock(BlockBreakEvent b) {
        if(b.isCancelled()){
            return;
        }
        if(b.getBlock().hasMetadata("lucky")){
            b.getBlock().setType(Material.AIR);
            b.getBlock().removeMetadata("lucky",this.luckyblock.plugin);
            this.luckyblock.i.get(this.luckyblock.random.nextInt(this.luckyblock.i.size())).onigiri(b);
            b.getBlock().getWorld().spawnParticle(Particle.EXPLOSION_LARGE,b.getBlock().getLocation(),1);
            b.getBlock().getWorld().playSound(b.getPlayer().getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK,100,1);
        }else if(b.getBlock().hasMetadata("luckySponge")){
            ItemStack item=new ItemStack(Material.SPONGE);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§e§lSPONGE");
            item.setItemMeta(meta);
            b.setDropItems(false);
            b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
            b.getBlock().removeMetadata("luckySponge",this.luckyblock.plugin);
        }else if(b.getPlayer().getInventory().getItemInMainHand().getItemMeta()!=null){
            if(b.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§e§lBigPickaxe")){
                if(!BigPicMode.containsKey(b.getPlayer().getName())){
                    BigPicMode.put(b.getPlayer().getName(),BreakMode.TREE);
                }
                if(BigPicMode.get(b.getPlayer().getName())!=BreakMode.ONE) {
                    if(BigPicMode.get(b.getPlayer().getName())==BreakMode.FIVE) {
                        if (1 <= b.getPlayer().getLevel()) {
                            b.getPlayer().setLevel(b.getPlayer().getLevel()-1);
                        }else{
                            b.getPlayer().sendMessage("§4経験値が足りません");
                            b.setCancelled(true);
                            return;
                        }
                    }
                    BreakBlocks(b.getBlock(),b.getPlayer().getInventory().getItemInMainHand(),BigPicMode.get(b.getPlayer().getName()),b.getPlayer());
                }
            }else if(b.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§cBigAxe")){
                //一括破壊斧作る部分
                if(Wood_With_Leaf.containsKey(b.getBlock().getType())) {
                    if (!WoodBreak.containsKey(b.getPlayer().getName())) {
                        WoodBreak.put(b.getPlayer().getName(), WoodBreakMood.WITH_LEAF);
                    }
                    List<Block> blocks = new ArrayList<>();
                    blocks.add(b.getBlock());
                    for (int i = 0; i < 100; i++) {
                        if (blocks.size() <= i) {
                            return;
                        }
                        breakBlocks(b.getPlayer(), blocks.get(i), blocks);
                    }
                }
            }
        }
    }
    private void breakBlocks(Player p,Block b,List<Block> blocks){
        for(BlockFace blockFace:blockFaces){
            if(b.getRelative(blockFace).getType()==b.getType()){
                blocks.add(b.getRelative(blockFace));
            }else if(WoodBreak.containsKey(p.getName())){
                if(WoodBreak.get(p.getName())==WoodBreakMood.WITH_LEAF){
                    if(b.getRelative(blockFace).getType()==Wood_With_Leaf.get(b.getType())){
                        blocks.add(b.getRelative(blockFace));
                    }
                }
            }
        }
        if(this.luckyblock.worldGuardPlugin!=null){
            if(!this.luckyblock.query.testState(BukkitAdapter.adapt(b.getLocation()),this.luckyblock.worldGuardPlugin.wrapPlayer(p),Flags.BUILD)){
                return;
            }
        }

        for(ItemStack item:b.getDrops()){
            b.getWorld().dropItem(b.getLocation(),item);
        }
        b.setType(Material.AIR);
    }
    public void BreakBlocks(Block block,ItemStack tool,BreakMode mode,Player p){
        Location l=block.getLocation();
        int number=3;
        if(mode==BreakMode.FIVE){
            number=5;
        }
        l.setY(l.getY()-(number-1)/2);
        l.setX(l.getX()-(number-1)/2);
        l.setZ(l.getZ()-(number-1)/2);
        for(int i=0;i<number;i++){//X
            for(int j=0;j<number;j++){//Y
                for(int k=0;k<number;k++){//Z
                    if(IsRightBlock(block.getWorld().getBlockAt(l),p)){
                            for(ItemStack itemStack:block.getWorld().getBlockAt(l).getDrops(tool)) {
                                block.getWorld().dropItem(block.getWorld().getBlockAt(l).getLocation(),itemStack);
                            }
                            block.getWorld().getBlockAt(l).setType(Material.AIR);
                    }
                    l.setZ(l.getZ()+1);
                }
                l.setZ(l.getZ()-number);
                l.setY(l.getY()+1);
            }
            l.setY(l.getY()-number);
            l.setX(l.getX()+1);
        }
    }
    public boolean IsRightBlock(Block b,Player p){
        if(this.luckyblock.IsWorldGuard==true){
        if(!this.luckyblock.query.testState(BukkitAdapter.adapt(b.getLocation()),this.luckyblock.worldGuardPlugin.wrapPlayer(p),Flags.BUILD)){
                return false;
            }
        }
        if(b.hasMetadata("lucky")||b.hasMetadata("luckySponge")){
            return false;
        }
        for(Material material:DontBlocks){
            if(b.getType()==material){
                return false;
            }
        }
        return true;
    }
    @EventHandler
    public void placeblock(BlockPlaceEvent b) {
        if(b.getItemInHand().getItemMeta()!=null&&b.isCancelled()==false&&b.canBuild()==true) {
            if (b.getItemInHand().getItemMeta().getDisplayName().equals("§lluckyblock")) {
                b.getBlock().setMetadata("lucky", new FixedMetadataValue(this.luckyblock.plugin, b.getBlock().getLocation().clone()));
            }else if(b.getItemInHand().getType() == Material.SPONGE && b.getItemInHand().getItemMeta().getDisplayName().equals("§e§lSPONGE")) {
                b.getBlock().setMetadata("luckySponge", new FixedMetadataValue(this.luckyblock.plugin, b.getBlock().getLocation().clone()));
            }else if(b.getItemInHand().getItemMeta().getDisplayName().equals("§c§lHeart")||b.getItemInHand().getItemMeta().getDisplayName().equals("§c§lEnder Chest")){
                b.setCancelled(true);
            }else if(b.getItemInHand().getItemMeta().getDisplayName().equals("§c金床")){
                b.setCancelled(true);
                Inventory inv=Bukkit.createInventory(null,9,"§c金床");
                ItemStack item=new ItemStack(Material.GLASS_PANE);
                for(int i=2;i<9;i++){
                    inv.setItem(i,item);
                }
                b.getPlayer().openInventory(inv);
            }else if(b.getItemInHand().getItemMeta().getDisplayName().equals("§c携帯型アイアンゴーレム")){
                b.setCancelled(true);
                b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.IRON_GOLEM);
                b.getItemInHand().setAmount(b.getItemInHand().getAmount()-1);
            }else if(b.getItemInHand().getItemMeta().getDisplayName().equals("§c携帯型スノーゴーレム")){
                b.setCancelled(true);
                b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.SNOWMAN);
                b.getItemInHand().setAmount(b.getItemInHand().getAmount()-1);
            }else if(b.getItemInHand().getItemMeta().getDisplayName().equals("§cTNT")){
                b.setCancelled(true);
                b.getItemInHand().setAmount(b.getItemInHand().getAmount()-1);
                b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.PRIMED_TNT);
            }else if(b.getItemInHand().getItemMeta().getDisplayName().equals("§cShulker Box")){
                b.setCancelled(true);
            }else if(b.getItemInHand().getItemMeta().getDisplayName().equals("§c携帯型村人")){
                b.setCancelled(true);
                b.getItemInHand().setAmount(b.getItemInHand().getAmount()-1);
                b.getBlock().getWorld().spawnEntity(b.getBlock().getLocation(),EntityType.VILLAGER);
            }else if(b.getItemInHand().getItemMeta().getDisplayName().equals("オークの原木?§c")&&!b.isCancelled()){
                b.getPlayer().damage(999,b.getPlayer());
            }else if(b.getItemInHand().getItemMeta().getDisplayName().equals("§c無限松明")&&!b.isCancelled()){
                b.getItemInHand().setAmount(b.getItemInHand().getAmount()+1);
            }
        }
    }
    @EventHandler
    public void MoveByPistonEvent(BlockPistonExtendEvent e){
        if(hasBlockMeta(e.getBlocks(),"luckySponge","lucky")){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void MoveByPistonEvent(BlockPistonRetractEvent e){
        if(hasBlockMeta(e.getBlocks(),"luckySponge","lucky")){
            e.setCancelled(true);
        }
    }
    private boolean hasBlockMeta(List<Block> blockList, String metadataKey, String key){
        for(Block b:blockList){
            if(b.hasMetadata(metadataKey)||b.hasMetadata(key)){
                return true;
            }
        }
        return false;
    }
    @EventHandler
    public void PlayerEntityShootBowEvent(EntityShootBowEvent e){
        if(e.getBow().hasItemMeta()) {
            if(e.getConsumable().getItemMeta().getDisplayName().equals("§cInfinityArrow")){
                e.setProjectile(e.getProjectile().getWorld().spawnArrow(e.getProjectile().getLocation(),e.getProjectile().getVelocity(),5,5));
                return;
            }
            if (e.getBow().getItemMeta().getDisplayName().equals("§lPlayerBow")) {
                e.getProjectile().addPassenger(e.getEntity());
            } else if (e.getBow().getItemMeta().getDisplayName().equals("§lTNTBow")) {
                e.getProjectile().setMetadata("TNTarrow", new FixedMetadataValue(this.luckyblock.plugin, e.getProjectile().getLocation().clone()));
            } else if (e.getBow().getItemMeta().getDisplayName().equals("§lByeBow") && e.getEntity().getNearbyEntities(6, 6, 6).size() != 0) {
                List<Entity> near = e.getEntity().getNearbyEntities(5, 5, 5);
                near.get(0).setInvulnerable(true);
                e.getProjectile().addPassenger(near.get(0));
                near.get(0).setInvulnerable(false);
            } else if (e.getBow().getItemMeta().getDisplayName().equals("§cFallingBlockBow")) {
                if (e.getEntity().getEquipment().getItemInOffHand() != null) {
                    if (e.getEntity().getEquipment().getItemInOffHand().getType().isBlock()) {
                        Material material = e.getEntity().getLocation().getBlock().getType();
                        e.getEntity().getWorld().getBlockAt(e.getEntity().getLocation()).setType(e.getEntity().getEquipment().getItemInOffHand().getType());
                        if (!(e.getEntity().getLocation().getBlock().getState() instanceof ShulkerBox)) {
                            e.getProjectile().addPassenger(e.getProjectile().getWorld().spawnFallingBlock(e.getProjectile().getLocation(), e.getEntity().getEquipment().getItemInOffHand().getType(), e.getEntity().getEquipment().getItemInOffHand().getData().getData()));
                            e.getEntity().getEquipment().getItemInOffHand().setAmount(e.getEntity().getEquipment().getItemInOffHand().getAmount() - 1);
                            e.getProjectile().setMetadata("FallingBlock", new FixedMetadataValue(this.luckyblock, e.getProjectile().getLocation()));
                            e.getProjectile().getLocation().add(e.getEntity().getVelocity());
                        }
                        e.getEntity().getLocation().getBlock().setType(material);
                    }
                }
            }else if(e.getBow().getItemMeta().getDisplayName().equals("§cSuperTNTBow")){
                e.getProjectile().setMetadata("LuckyTNTBow",new FixedMetadataValue(this.luckyblock,"aaa"));
            }
        }
    }
    @EventHandler
    public void TNTBowHitEvent(ProjectileHitEvent e){
        if(e.getEntity().hasMetadata("TNTarrow")) {
            if (e.getHitEntity() == null) {
                Location location = e.getEntity().getLocation();
                location.setY(e.getEntity().getLocation().getY() - 0.25);
                e.getHitBlock().getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
                e.getEntity().removeMetadata("TNTarrow", this.luckyblock.plugin);
            } else {
                Location location = e.getHitEntity().getLocation();
                location.setY(location.getY() - 0.25);
                e.getHitEntity().getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
                e.getEntity().removeMetadata("TNTarrow", this.luckyblock.plugin);
            }
        }else if(e.getEntity().hasMetadata("FallingBlock")){
            e.getEntity().removeMetadata("FallingBlock",this.luckyblock);
            e.getEntity().remove();
        }else if(e.getEntity().hasMetadata("LuckyTNTBow")){
            e.getEntity().removeMetadata("LuckyTNTBow",this.luckyblock);
            ((TNTPrimed)e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(),EntityType.PRIMED_TNT)).setFuseTicks(0);
        }
    }
    @EventHandler
    public void ClickEvent(PlayerInteractEvent e){
        if(e.getItem()!=null) {
            if(e.getItem().hasItemMeta()) {
                if(e.getItem().getItemMeta().getDisplayName()!=null) {
                    if (e.getItem().getItemMeta().getDisplayName().equals("§a§lkoufuのパン")) {
                        if (e.getItem().getItemMeta().getLore().size() == 3) {
                            if (e.getItem().getItemMeta().getLore().get(2).equals("create by koufu")) {
                                if (e.getPlayer().getFoodLevel() < 40) {
                                    e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel() + 5);
                                } else {
                                    e.getPlayer().sendMessage("§cもうお腹がいっぱいです");
                                }
                            }
                        }
                    } else if (e.getItem().getItemMeta().getDisplayName().equals("§e雷の杖(未完成)")) {
                        if(e.getClickedBlock()!=null) {
                            if(e.getClickedBlock().getType()==Material.GOLD_BLOCK) {
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
                            }
                        }
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
                    } else if (e.getItem().getItemMeta().getDisplayName().equals("§l食べれる紙") && e.getPlayer().getFoodLevel() != 20) {
                        e.getItem().setAmount(e.getItem().getAmount() - 1);
                        e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel() + 2);
                    } else if (e.getItem().getItemMeta().getDisplayName().equals("§lDon't read!")) {
                        e.getPlayer().setMetadata("LuckyDead", new FixedMetadataValue(this.luckyblock.plugin, e.getPlayer().getLocation().clone()));
                        e.getPlayer().setHealth(0);
                    } else if (e.getItem().getItemMeta().getDisplayName().equals("§lBighoe") && e.getAction() == Action.RIGHT_CLICK_BLOCK && (e.getClickedBlock().getType() == Material.GRASS_BLOCK || e.getClickedBlock().getType() == Material.DIRT)) {
                        setMaterials(e,Material.FARMLAND);
                    }else if (e.getItem().getItemMeta().getDisplayName().equals("§lFireStick")) {
                        Fireball living = (Fireball) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getLocation(), EntityType.FIREBALL);
                        living.setVelocity(e.getPlayer().getVelocity());
                        Location location = living.getLocation();
                        location.setY(location.getY() + 1);
                        location.add(e.getPlayer().getVelocity());
                        living.teleport(location);
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§e§lHeadStick")&&e.getItem().getType()==Material.STICK){
                        if(e.getPlayer().getInventory().getHelmet()==null){
                            if(e.getPlayer().getInventory().getItemInOffHand().getType()!=Material.AIR) {
                                ItemStack itemStack=e.getPlayer().getInventory().getItemInOffHand().clone();
                                itemStack.setAmount(1);
                                e.getPlayer().getInventory().setHelmet(itemStack);
                                e.getPlayer().getInventory().getItemInOffHand().setAmount(e.getPlayer().getInventory().getItemInOffHand().getAmount() - 1);
                            }else{
                                e.getPlayer().sendMessage("オフハンドにアイテムを置いてください");
                            }
                        }else{
                            e.getPlayer().sendMessage("頭にかぶっているものを外してください");
                        }
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§e§lBigPickaxe")&&(e.getAction()==Action.RIGHT_CLICK_AIR||e.getAction()==Action.RIGHT_CLICK_BLOCK)){
                        if(!e.getPlayer().isSneaking()) {
                            if(names.contains(e.getPlayer().getName())){
                                e.getPlayer().sendMessage("ロックを解除してください(スニーク+右クリック)");
                            }else {
                                if (!BigPicMode.containsKey(e.getPlayer().getName())) {
                                    BigPicMode.put(e.getPlayer().getName(), BreakMode.TREE);
                                }
                                switch (BigPicMode.get(e.getPlayer().getName())) {
                                    case ONE:
                                        BigPicMode.replace(e.getPlayer().getName(), BreakMode.TREE);
                                        break;
                                    case TREE:
                                        BigPicMode.replace(e.getPlayer().getName(), BreakMode.FIVE);
                                        break;
                                    case FIVE:
                                        BigPicMode.replace(e.getPlayer().getName(), BreakMode.ONE);
                                        break;
                                }
                                e.getPlayer().sendMessage("つるはしのモードを§e§l" + BigPicMode.get(e.getPlayer().getName()).toString() + ChatColor.RESET + "に変更しました");
                            }
                        }else{
                            if(names.contains(e.getPlayer().getName())){
                                names.remove(e.getPlayer().getName());
                                e.getPlayer().sendMessage("ロックを解除しました Shift+Right Click で変えれます");
                            }else{
                                names.add(e.getPlayer().getName());
                                e.getPlayer().sendMessage("ピッケルのモードをロックしました Shift+Right Click で変えれます");
                            }
                        }
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§c§lEnder Chest")){
                        e.getPlayer().openInventory(e.getPlayer().getEnderChest());
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§cRocket?")&&e.getAction()==Action.RIGHT_CLICK_BLOCK) {
                        e.setCancelled(true);
                        List<Entity> entities = e.getPlayer().getNearbyEntities(2.5, 2.5, 2.5);
                        Location location = e.getClickedBlock().getLocation();
                        location.setY(location.getY()+1);
                        if (entities.size() != 0) {
                            Firework entity = (Firework)e.getPlayer().getWorld().spawnEntity(location, EntityType.FIREWORK);
                            FireworkMeta meta=entity.getFireworkMeta();
                            meta.setPower(2);
                            meta.addEffect(FireworkEffect.builder().withColor(Color.RED).build());
                            entity.setFireworkMeta(meta);
                            e.getItem().setAmount(e.getItem().getAmount() - 1);
                            if (entities.size() == 1) {
                                entity.addPassenger(entities.get(0));
                            } else {
                                entity.addPassenger(entities.get(0));
                                entity.addPassenger(entities.get(1));
                            }
                        }
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§cMagmaSponge")&&e.getAction()==Action.RIGHT_CLICK_BLOCK){
                        e.setCancelled(true);
                        for(int i=-3;i<4;i++){
                            for(int j=-3;j<4;j++){
                                for(int k=-3;k<4;k++){
                                    Block block=e.getClickedBlock().getWorld().getBlockAt(e.getClickedBlock().getX()+i,e.getClickedBlock().getY()+j,e.getClickedBlock().getZ()+k);
                                    if(block.getType()==Material.LAVA){
                                        block.setType(Material.AIR);
                                    }
                                }
                            }
                        }
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§cFallingBlockStick")&&e.getAction()==Action.LEFT_CLICK_BLOCK&&e.getPlayer().getInventory().getItemInOffHand()!=null){
                        if(e.getPlayer().getInventory().getItemInOffHand().getType().isBlock()) {
                            Material material=e.getPlayer().getLocation().getBlock().getType();
                            e.getPlayer().getWorld().getBlockAt(e.getPlayer().getLocation()).setType(e.getPlayer().getEquipment().getItemInOffHand().getType());
                            if(!(e.getPlayer().getLocation().getBlock().getState() instanceof ShulkerBox)) {
                                e.getPlayer().getLocation().getBlock().setType(material);
                                Location location = e.getClickedBlock().getLocation();
                                location.setY(location.getY() + 5);
                                e.getClickedBlock().getWorld().spawnFallingBlock(location,e.getPlayer().getInventory().getItemInOffHand().getType(),e.getPlayer().getInventory().getItemInOffHand().getData().getData());
                                e.getPlayer().getInventory().getItemInOffHand().setAmount(e.getPlayer().getInventory().getItemInOffHand().getAmount()-1);
                                return;
                            }
                            e.getPlayer().getLocation().getBlock().setType(material);
                        }
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§c金床")){
                        Inventory inv=Bukkit.createInventory(null,9,"§c金床");
                        ItemStack item=new ItemStack(Material.GLASS_PANE);
                        for(int i=2;i<9;i++){
                            inv.setItem(i,item);
                        }
                        e.getPlayer().openInventory(inv);
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§cNoRideStick")){
                        for(Entity entity:e.getPlayer().getPassengers()){
                            e.getPlayer().removePassenger(entity);
                        }
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§cLuckyItem")){
                        if(e.getItem().getItemMeta().getLore().size()==1){
                            if(e.getItem().getItemMeta().getLore().get(0).equals(e.getPlayer().getName())){
                                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LUCK,Integer.MAX_VALUE,5));
                            }
                        }
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§cなんかできそうな羽")&&!stringHashSet.contains(e.getPlayer().getName())){
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,4,10));
                        stringHashSet.add(e.getPlayer().getName());
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                stringHashSet.remove(e.getPlayer().getName());
                            }
                        }.runTaskLater(this.luckyblock,20);
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§cBigAxe")&&(e.getAction()==Action.RIGHT_CLICK_AIR||e.getAction()==Action.RIGHT_CLICK_BLOCK)){
                        if(!e.getPlayer().isSneaking()) {
                            if(!WoodSet.contains(e.getPlayer().getName())) {
                                if (!WoodBreak.containsKey(e.getPlayer().getName())) {
                                    e.getPlayer().sendMessage("斧のモードを§e§l葉を入れないモード" + ChatColor.RESET + "にしました");
                                    WoodBreak.put(e.getPlayer().getName(), WoodBreakMood.NO_LEAF);
                                } else if (WoodBreak.get(e.getPlayer().getName()) == WoodBreakMood.WITH_LEAF) {
                                    e.getPlayer().sendMessage("斧のモードを§e§l葉を入れないモード" + ChatColor.RESET + "にしました");
                                    WoodBreak.replace(e.getPlayer().getName(), WoodBreakMood.NO_LEAF);
                                } else {
                                    e.getPlayer().sendMessage("斧のモードを§e§l葉を入れるモード" + ChatColor.RESET + "にしました");
                                    WoodBreak.replace(e.getPlayer().getName(), WoodBreakMood.WITH_LEAF);
                                }
                            }else{
                                e.getPlayer().sendMessage("ロックを解除してください");
                            }
                        }else{
                            if(WoodSet.contains(e.getPlayer().getName())){
                                WoodSet.remove(e.getPlayer().getName());
                                e.getPlayer().sendMessage("ロックを解除しました Shift+Right Click で変えれます");
                            }else{
                                WoodSet.add(e.getPlayer().getName());
                                e.getPlayer().sendMessage("ロックしました Shift+Right Click で変えれます");
                            }
                        }
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§cShulker Box")){
                        if(e.getItem().getItemMeta() instanceof BlockStateMeta){
                            BlockStateMeta im = (BlockStateMeta)e.getItem().getItemMeta();
                            if(im.getBlockState() instanceof ShulkerBox){
                                ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                                Inventory inv = Bukkit.createInventory(null, 27, "§cShulker Box");
                                inv.setContents(shulker.getInventory().getContents());
                                e.getPlayer().openInventory(inv);
                            }
                        }
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§cTPStick")&&e.getAction()==Action.RIGHT_CLICK_BLOCK){
                        if(1<=e.getPlayer().getLevel()) {
                            e.getPlayer().teleport(e.getClickedBlock().getRelative(e.getBlockFace()).getLocation());
                            e.getPlayer().setLevel(e.getPlayer().getLevel()-1);
                        }else{
                            e.getPlayer().sendMessage("Need 1 level");
                        }
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§cBigShovel")&& e.getAction() == Action.RIGHT_CLICK_BLOCK && (e.getClickedBlock().getType() == Material.GRASS_BLOCK || e.getClickedBlock().getType() == Material.DIRT)){
                        setMaterials(e,Material.GRASS_PATH);
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§cこうふ銃")||e.getItem().getItemMeta().getDisplayName().equals("§cとうふ銃")){
                        if(e.getAction()==Action.RIGHT_CLICK_AIR||e.getAction()==Action.RIGHT_CLICK_BLOCK) {
                            Entity entity;
                            entity = EntityGetPlayer.getStraightEntity(e.getPlayer(), e.getPlayer().hasMetadata("LuckyGun")?90:75,e.getItem().getItemMeta().getDisplayName().equals("§cこうふ銃"));
                            e.getPlayer().playSound(e.getPlayer().getLocation(), e.getItem().getItemMeta().getDisplayName().equals("§cこうふ銃")?Sound.ENTITY_GENERIC_EXPLODE:Sound.ENTITY_GENERIC_BIG_FALL, 1, e.getItem().getItemMeta().getDisplayName().equals("§cこうふ銃")?1:2);
                            if (entity != null) {
                                ((LivingEntity) entity).damage(10, e.getPlayer());
                            }
                        }else if(e.getPlayer().isSneaking()){
                            if(e.getPlayer().hasMetadata("LuckyGun")){
                                e.getPlayer().removePotionEffect(PotionEffectType.SLOW);
                                e.getPlayer().removeMetadata("LuckyGun",this.luckyblock);
                            }else{
                                e.getPlayer().setMetadata("LuckyGun",new FixedMetadataValue(this.luckyblock,"aaaa"));
                                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW,Integer.MAX_VALUE,100));
                            }
                        }
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§c無限大の可能性がある花火")&&e.getAction()==Action.RIGHT_CLICK_BLOCK){
                        e.setCancelled(true);
                        Firework firework=(Firework)e.getClickedBlock().getWorld().spawnEntity(e.getClickedBlock().getRelative(BlockFace.UP).getLocation(),EntityType.FIREWORK);
                        FireworkMeta meta=firework.getFireworkMeta();
                        meta.setPower(3);
                        meta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(this.luckyblock.random.nextInt(255),this.luckyblock.random.nextInt(255),this.luckyblock.random.nextInt(255))).with(FireworkEffect.Type.values()[this.luckyblock.random.nextInt(FireworkEffect.Type.values().length)]).build());
                        firework.setFireworkMeta(meta);
                    }else if(e.getItem().getItemMeta().getDisplayName().equals("§c無限水")&&(e.getAction()==Action.RIGHT_CLICK_AIR||e.getAction()==Action.RIGHT_CLICK_BLOCK)){
                        e.getItem().setAmount(e.getItem().getAmount()+1);
                    }
                }
            }
        }
    }
    private void setMaterials(PlayerInteractEvent e,Material material){
        Location l = e.getClickedBlock().getLocation();
        l.setX(l.getX() - 1);
        l.setZ(l.getZ() - 1);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (x != 1 || y != 1) {
                    if(this.luckyblock.IsWorldGuard){
                        if(!this.luckyblock.query.testState(BukkitAdapter.adapt(l),this.luckyblock.worldGuardPlugin.wrapPlayer(e.getPlayer()),Flags.BUILD)){
                            if (l.getBlock().getType() == Material.GRASS_BLOCK||l.getBlock().getType() == Material.DIRT) {
                                l.getBlock().setType(material);
                            }
                        }
                    }else{
                        if (l.getBlock().getType() == Material.GRASS_BLOCK||l.getBlock().getType() == Material.DIRT) {
                            l.getBlock().setType(material);
                        }
                    }
                }
                l.setX(l.getX() + 1);
            }
            l.setX(l.getX() - 3);
            l.setZ(l.getZ() + 1);
        }
    }
    @EventHandler
    public void DeathPlayerEvent(PlayerDeathEvent e){
        if(e.getEntity().hasMetadata("LuckyDead")){
            e.setDeathMessage(e.getEntity().getDisplayName()+"は本の物理攻撃によって殺された");
            e.getEntity().removeMetadata("LuckyDead",this.luckyblock.plugin);
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
        if(e.getItem().hasItemMeta()) {
            if(e.getItem().getItemMeta().getDisplayName().equals("§a§lkoufuのパン")){
                e.setCancelled(true);
            }else if(e.getItem().getItemMeta().getDisplayName().equals("§e§lBigApple")){
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST,1200,3));
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,1200,3));
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,1200,3));
            }else if(e.getItem().getItemMeta().getDisplayName().equals("§cBeetRoot")){
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HEAL,200,3));
            }else if(e.getItem().getItemMeta().getDisplayName().equals("§cInfinityMilk")) {
                e.setCancelled(true);
                for(PotionEffect effect:e.getPlayer().getActivePotionEffects()){
                    e.getPlayer().removePotionEffect(effect.getType());
                }
            }else if(e.getItem().getItemMeta().getDisplayName().equals("§cとうふ牛乳")){
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.values()[this.luckyblock.random.nextInt(PotionEffectType.values().length)],1000,5));
            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void WaterEvent(PlayerBucketEmptyEvent e){
        if(e.isCancelled()){
            return;
        }
        e.setCancelled(true);
        if(e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {
            if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§c§lInfiniteWaterBucket")) {
                e.getBlock().setType(Material.WATER);
                return;
            }
        }
        if(e.getPlayer().getInventory().getItemInOffHand().hasItemMeta()) {
            if (e.getPlayer().getInventory().getItemInOffHand().getItemMeta().getDisplayName().equals("§c§lInfiniteWaterBucket")) {
                e.getBlock().setType(Material.WATER);
                return;
            }
        }
        e.setCancelled(false);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void BucketEvent(PlayerBucketFillEvent e){
        if(e.isCancelled()){
            return;
        }
        e.setCancelled(true);
        if(e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {
            if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§c§lInfiniteNoneBucket")) {
                e.getBlock().setType(Material.AIR);
                return;
            }
        }
        if(e.getPlayer().getInventory().getItemInOffHand().hasItemMeta()) {
            if (e.getPlayer().getInventory().getItemInOffHand().getItemMeta().getDisplayName().equals("§c§lInfiniteNoneBucket")) {
                e.getBlock().setType(Material.AIR);
                return;
            }
        }
        e.setCancelled(false);
    }
    @EventHandler
    public void CreatureSpawnEvent(CreatureSpawnEvent e) {
        if(this.luckyblock.SpawnEnetities.containsKey(e.getEntityType())) {
            double d = ((double)this.luckyblock.random.nextInt(100))/100;
            if (d<this.luckyblock.SpawnEnetities.get(e.getEntityType())&&e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) {
                e.getEntity().setMetadata("mob", new FixedMetadataValue(this.luckyblock.plugin, e.getEntity().getLocation().clone()));
                e.getEntity().setCustomName("LuckyMob");
                e.getEntity().setCustomNameVisible(true);
                e.getEntity().setPersistent(false);
            }
        }
    }
    @EventHandler
    public void dropevent(EntityDeathEvent e) {
        if (e.getEntity().hasMetadata("mob")) {
            e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), luckyblock.createskull(1));
        }
    }

    @EventHandler
    public void damageeventByEntity(EntityDamageByEntityEvent e) {
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
                    j.setHealth(j.getHealth()+3);
                } else if (l.getEquipment().getItemInMainHand().getItemMeta().getDisplayName().equals("§7§lBone of life") && e.getEntityType().equals(EntityType.ZOMBIE_VILLAGER)) {
                    e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(),EntityType.VILLAGER);
                    e.getEntity().remove();
                } else if (l.getEquipment().getItemInMainHand().getItemMeta().getDisplayName().equals("§cEmeraldSword")) {
                    ItemStack item = new ItemStack(Material.EMERALD,1);
                    e.getEntity().getWorld().dropItem(e.getEntity().getLocation(),item);
                }else if(l.getEquipment().getItemInMainHand().getItemMeta().getDisplayName().equals("§c強そうな剣")&&e.isCancelled()==false){
                    ((LivingEntity)e.getEntity()).damage(e.getDamage());
                }else if(l.getEquipment().getItemInMainHand().getItemMeta().getDisplayName().equals("§cCoalSword")){
                    ItemStack item = new ItemStack(Material.COAL,1);
                    e.getEntity().getWorld().dropItem(e.getEntity().getLocation(),item);
                }
            }
        }
    }
    @EventHandler
    public void CloseGUIEvent(InventoryCloseEvent e){
        if(e.getView().getTitle().matches("§lAdd Lucky Item:.*")){
            ArrayList<ItemStack> list=new ArrayList<>();
            for(int j=0;j<27;j++) {
                if (e.getView().getItem(j) != null) {
                    list.add(e.getView().getItem(j));
                }
            }
            this.luckyblock.fc.set(e.getView().getTitle().replaceFirst("§lAdd Lucky Item:",""),list);
            this.luckyblock.i.add(b->{
                for(ItemStack item:list){
                    b.getBlock().getWorld().dropItem(b.getBlock().getLocation(),item);
                }
            });
            e.getPlayer().sendMessage("追加に成功しました");
        }else if(e.getView().getTitle().equals("§c金床")){
            if(e.getInventory().getItem(0)!=null||e.getInventory().getItem(1)!=null) {
                if (e.getInventory().getItem(0).hasItemMeta() && e.getInventory().getItem(0).getAmount() == 1 && e.getInventory().getItem(0).getType() == e.getInventory().getItem(1).getType() && e.getInventory().getItem(0).getAmount() == e.getInventory().getItem(1).getAmount()) {
                    ItemStack item = e.getInventory().getItem(0);
                    item.addUnsafeEnchantments(e.getInventory().getItem(1).getEnchantments());
                    e.getPlayer().getInventory().addItem(item);
                } else {
                    e.getPlayer().sendMessage("入っていたアイテムが違うか個数が1じゃないです");
                    e.getPlayer().getInventory().addItem(e.getInventory().getItem(0));
                    e.getPlayer().getInventory().addItem(e.getInventory().getItem(1));
                }
            }
        }else if(e.getView().getTitle().equals("§cShulker Box")){
            ItemStack item=null;
            if(e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()){
                if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§cShulker Box")){
                    item=e.getPlayer().getInventory().getItemInMainHand();
                }
            }else if(e.getPlayer().getInventory().getItemInOffHand().hasItemMeta()){
                if(e.getPlayer().getInventory().getItemInOffHand().getItemMeta().getDisplayName().equals("§cShulker Box")){
                    item=e.getPlayer().getInventory().getItemInOffHand();
                }
            }
            if(item==null){
                for(int i=0;i<27;i++){
                    e.getPlayer().getInventory().addItem(e.getView().getItem(i));
                }
                return;
            }
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName("§cShulker Box");
            item.setItemMeta(meta);
            BlockStateMeta im = (BlockStateMeta)item.getItemMeta();
            if(im.getBlockState() instanceof ShulkerBox){
                ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                for(int i=0;i<shulker.getInventory().getSize();i++){
                    if(!isShulker(e.getInventory().getItem(i))) {
                        shulker.getInventory().setItem(i, e.getInventory().getItem(i));
                    }else{
                        e.getPlayer().getInventory().addItem(e.getInventory().getItem(i));
                    }
                }
                im.setBlockState(shulker);
                item.setItemMeta(im);
            }
        }
    }
    private boolean isShulker(ItemStack item){
        if(item!=null) {
            if (item.hasItemMeta()) {
                if (item.getItemMeta() instanceof BlockStateMeta) {
                    if (((BlockStateMeta) item.getItemMeta()).getBlockState() instanceof ShulkerBox) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent e){
        if(e.getView().getTitle().equals("§c金床")&&e.getCurrentItem()!=null){
            if(e.getCurrentItem().getType()==Material.GLASS_PANE&&e.getClickedInventory().getSize()==9) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void PlayerEggThrowEvent(PlayerEggThrowEvent e) {
        if(e.getEgg().getItem().hasItemMeta()) {
            if(e.getEgg().getItem().getItemMeta().getDisplayName().equals("§c§lhand grenade")) {
                e.getEgg().getWorld().spawnEntity(e.getEgg().getLocation(), EntityType.PRIMED_TNT);
                e.getEgg().remove();
            }else if(e.getEgg().getItem().getItemMeta().getDisplayName().equals("§c§lFang Egg")){
                Location location=e.getEgg().getLocation();
                location.setX(location.getX()-2);
                location.setZ(location.getZ()-2);
                for(int j=0;j<5;j++){
                    for(int k=0;k<5;k++){
                        e.getEgg().getWorld().spawnEntity(location,EntityType.EVOKER_FANGS);
                        location.setZ(location.getZ()+1);
                    }
                    location.setX(location.getX()+1);
                    location.setZ(location.getZ()-5);
                }
                e.getEgg().remove();
            }else if(e.getEgg().getItem().getItemMeta().getDisplayName().equals("§cCreeperEgg")) {
                Creeper creeper = (Creeper) e.getEgg().getWorld().spawnEntity(e.getEgg().getLocation(), EntityType.CREEPER);
                creeper.setExplosionRadius(creeper.getExplosionRadius() * 2);
            }
        }
    }
}
