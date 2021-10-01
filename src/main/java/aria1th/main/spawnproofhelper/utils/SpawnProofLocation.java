package aria1th.main.spawnproofhelper.utils;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.text.Text;
import net.minecraft.item.Items;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.world.World;
import net.minecraft.world.SpawnHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class SpawnProofLocation {
    private final static int reachDistance = 3;
    private final static int lightLevel = 11;
    private final static int maxInteractionPerTick = 10;
    private final static MinecraftClient mc = MinecraftClient.getInstance();
    private final static ClientWorld clientWorld = mc.world;
    private static boolean enabled = false;
    private static String previousMessage = null;
    private static LinkedHashMap<Long, Long> nanotimeMap = new LinkedHashMap<>();
    private final static ArrayList<Item> CarpetTypes = new ArrayList<>(Arrays.asList(Items.WHITE_CARPET, Items.RED_CARPET, Items.ORANGE_CARPET, Items.YELLOW_CARPET,
            Items.LIME_CARPET, Items.GREEN_CARPET, Items.LIGHT_BLUE_CARPET, Items.BLUE_CARPET, Items.PURPLE_CARPET, Items.MOSS_CARPET, Items.MAGENTA_CARPET, Items.PINK_CARPET,
            Items.GRAY_CARPET, Items.LIGHT_GRAY_CARPET, Items.BLACK_CARPET, Items.BROWN_CARPET, Items.CYAN_CARPET));
    public static void tick(){
        if(enabled){
            doSpawnProofing();
        }
    }
    public static void switchOnOff(){
        enabled = !enabled;
        printMessageToChat(Text.of("Spawnproofing Turned "+ "%s".format(enabled ? "ON" : "OFF")));
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
        return world.isAir(blockPos) || world.getBlockState(blockPos).getMaterial().isReplaceable() && world.isAir(blockPos.up());
    }
    public static void doSpawnProofing(){
        BlockPos playerPos = MinecraftClient.getInstance().player.getBlockPos();
        BlockPos.streamOutwards(playerPos, reachDistance, reachDistance, reachDistance).
                filter(a-> isSpawnableBlock(a.asLong())).
                filter(a-> playerPos.getSquaredDistance(a,true)<reachDistance * reachDistance).
                filter(a-> !playerPos.isWithinDistance(a, 1)).
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
        mc.interactionManager.interactBlock(mc.player, mc.world, hand, hitResult);
        nanotimeMap.put(blockPos.asLong(),System.nanoTime());
    }
    private static int getCarpetItem(){
        int slotNum = -1;
        for (Item i : CarpetTypes){
            slotNum = mc.player.getInventory().getSlotWithStack(i.getDefaultStack());
            if (slotNum != -1){
                return slotNum;
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