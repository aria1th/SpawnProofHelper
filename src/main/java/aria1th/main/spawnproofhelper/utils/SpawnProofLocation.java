package aria1th.main.spawnproofhelper.utils;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.text.Text;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpawnProofLocation {
    private final static int reachDistance = 3;
    private final static int lightLevel = 11;
    private final static int maxInteractionPerTick = 1;
    private final static MinecraftClient mc = MinecraftClient.getInstance();
    private final static ClientWorld clientWorld = mc.world;
    private static boolean enabled = false;
    private static String previousMessage = null;
    private static final LinkedHashMap<Long, Long> nanotimeMap = new LinkedHashMap<>();
    private static final List<TagKey<Item>> spawnProofItems =  List.of(
                    ItemTags.WOOL_CARPETS,
                    ItemTags.SLABS,
                    ItemTags.WOODEN_PRESSURE_PLATES,
                    ItemTags.BUTTONS
            );
	private static final List<Item> extraItemList = List.of(
		Items.LIGHT_WEIGHTED_PRESSURE_PLATE,
		Items.HEAVY_WEIGHTED_PRESSURE_PLATE //Add more blocks if you need

	);
    //private final static List<Item> CarpetTypes = ItemTags.CARPETS.values();
    //private final static List<Item> SlabTypes = ItemTags.SLABS.values();
    //private final static List<Item> PressurePlateTypes = ItemTags.WOODEN_PRESSURE_PLATES.values();
    //private final static List<Item> ButtonTypes = ItemTags.BUTTONS.values();
    public static void tick(){
        if(enabled){
            doSpawnProofing();
        }
    }
    public static void switchOnOff(){
        enabled = !enabled;
        printMessageToChat(Text.of("Spawnproofing Turned "+ (enabled ? "ON" : "OFF")));
    }
    public static void refreshInstance(){
        enabled = false;
        printMessageToChat(Text.of("Spawnproofing Turned "+ "OFF"));
    }
    public static boolean isEnabled(){
        return enabled;
    }
    public static void printMessageToChat(Text text){
        if (previousMessage!= null && previousMessage.equals(text.getString())) {return;}
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
        previousMessage = text.getString();
    }
    public static boolean isSpawnableBlock(long longPos){
        World world = MinecraftClient.getInstance().world;
        BlockPos blockPos = BlockPos.fromLong(longPos);
        assert world != null;
        Block block = world.getBlockState(blockPos).getBlock();
        Block offsetBlock = world.getBlockState(blockPos.down()).getBlock();
        if (block instanceof FluidBlock){
            return false;
        }
        if (!world.getBlockState(blockPos.down()).allowsSpawning(world, blockPos.down(), EntityType.ZOMBIFIED_PIGLIN)){
            return false;
        }
        return world.isAir(blockPos) || world.getBlockState(blockPos).isReplaceable() && world.isAir(blockPos.up());
    }
    public static void doSpawnProofing(){
        BlockPos playerPos = MinecraftClient.getInstance().player.getBlockPos();
        BlockPos.streamOutwards(playerPos, reachDistance, reachDistance, reachDistance).
                filter(a-> isSpawnableBlock(a.asLong())).
                filter(a-> playerPos.getSquaredDistance(a)<reachDistance * reachDistance).
                filter(a-> !playerPos.isWithinDistance(a, 2)).
                filter(a-> !nanotimeMap.containsValue(a.asLong()) || nanotimeMap.get(a.asLong()) > System.nanoTime()+1e9).
                limit(maxInteractionPerTick).
                forEach(SpawnProofLocation::placeCarpet);
    }
    private static void placeCarpet(BlockPos blockPos){
        int slotNum = getCarpetItem();
        if (slotNum == -1){
            printMessageToChat(Text.of("No carpet item in inventory"));
            return;
        }
        if (!playerInventorySwitch(mc.player.getInventory().getStack(slotNum).getItem())){
            printMessageToChat(Text.of("Failed switching items with inventory"));
            return;
        }
        Vec3d hitVec = new Vec3d(blockPos.getX() , blockPos.getY(), blockPos.getZ());
        Hand hand = Hand.MAIN_HAND;
        BlockHitResult hitResult = new BlockHitResult(hitVec, Direction.NORTH, blockPos, false);
        mc.interactionManager.interactBlock(mc.player, hand, hitResult);
        nanotimeMap.put(blockPos.asLong(),System.nanoTime());
    }
    private static int getCarpetItem(){
	    Inventory inventory = mc.player.getInventory();
		for (int i = 0; i < inventory.size(); i++){
			ItemStack stack = inventory.getStack(i);
			for (TagKey<Item> predicates : spawnProofItems){
				if (stack.isIn(predicates)){
					return i;
				}
			}
			for (Item extras : extraItemList){
				if (stack.getItem() == extras){
					return i;
				}
			}
		}
        return -1;
    }
    public static boolean playerInventorySwitch(Item itemName){
        ItemStack itemStack = itemName.getDefaultStack();
        PlayerInventory playerInventory = mc.player.getInventory();
        int i = playerInventory.getSlotWithStack(itemStack);
        if (i != -1) {
            if (PlayerInventory.isValidHotbarIndex(i)) {
                playerInventory.selectedSlot = i;
            } else {
                mc.interactionManager.pickFromInventory(i);
            }
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(playerInventory.selectedSlot));
            return true;
        }
        return false;
    }
}