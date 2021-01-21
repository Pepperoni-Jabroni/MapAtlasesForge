package pepjebs.mapatlases.utils;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import pepjebs.mapatlases.MapAtlasesMod;

import java.util.*;
import java.util.stream.Collectors;

public class MapAtlasesAccessUtils {

    public static MapData previousMapState = null;

    public static boolean areMapsSameScale(MapData testAgainst, List<MapData> newMaps) {
        return newMaps.stream().filter(m -> m.scale == testAgainst.scale).count() == newMaps.size();
    }

    public static boolean areMapsSameDimension(MapData testAgainst, List<MapData> newMaps) {
        return newMaps.stream().filter(m -> m.dimension == testAgainst.dimension).count() == newMaps.size();
    }

    public static MapData getRandomMapStateFromAtlas(World world, ItemStack atlas) {
        if (atlas.getTag() == null) return null;
        int[] mapIds = Arrays.stream(atlas.getTag().getIntArray("maps")).toArray();
        ItemStack map = createMapItemStackFromId(mapIds[0]);
        return FilledMapItem.getData(map, world);
    }

    public static ItemStack createMapItemStackFromId(int id) {
        ItemStack map = new ItemStack(Items.FILLED_MAP);
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("map", id);
        map.setTag(tag);
        return map;
    }

    public static ItemStack createMapItemStackFromStrId(String id) {
        ItemStack map = new ItemStack(Items.FILLED_MAP);
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("map", Integer.parseInt(id.substring(4)));
        map.setTag(tag);
        return map;
    }

    public static List<MapData> getAllMapStatesFromAtlas(World world, ItemStack atlas) {
        if (atlas.getTag() == null) return new ArrayList<>();
        int[] mapIds = Arrays.stream(atlas.getTag().getIntArray("maps")).toArray();
        List<MapData> mapStates = new ArrayList<>();
        for (int mapId : mapIds) {
            MapData state = world.getMapData(FilledMapItem.getMapName(mapId));
            if (state == null && world instanceof ServerWorld) {
                ItemStack map = createMapItemStackFromId(mapId);
                state = FilledMapItem.getMapData(map, world);
            }
            if (state != null) {
                mapStates.add(state);
            }
        }
        return mapStates;
    }

    public static ItemStack getAtlasFromItemStacks(List<ItemStack> itemStacks) {
        Optional<ItemStack> item =  itemStacks.stream()
                .filter(i -> i.isItemEqual(new ItemStack(MapAtlasesMod.getAtlasItem()))).findFirst();
        return item.orElse(ItemStack.EMPTY).copy();
    }

    public static List<MapData> getMapStatesFromItemStacks(World world, List<ItemStack> itemStacks) {
        return itemStacks.stream()
                .filter(i -> i.isItemEqual(new ItemStack(Items.FILLED_MAP)))
                .map(m -> FilledMapItem.getMapData(m, world))
                .collect(Collectors.toList());
    }

    public static Set<Integer> getMapIdsFromItemStacks(ClientWorld world, List<ItemStack> itemStacks) {
        return getMapStatesFromItemStacks(world, itemStacks).stream()
                .map(MapAtlasesAccessUtils::getMapIntFromState).collect(Collectors.toSet());
    }

    public static List<ItemStack> getItemStacksFromGrid(CraftingInventory inv) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                itemStacks.add(inv.getStackInSlot(i));
            }
        }
        return itemStacks;
    }

    public static boolean isListOnylIngredients(List<ItemStack> itemStacks) {
        return itemStacks.stream().filter(is -> is.isItemEqual(new ItemStack(MapAtlasesMod.getAtlasItem()))
                || is.isItemEqual(new ItemStack(Items.MAP))
                || is.isItemEqual(new ItemStack(Items.FILLED_MAP))).count() == itemStacks.size();
    }

    public static int getMapIntFromState(MapData mapState) {
        String mapId = mapState.getName();
        return Integer.parseInt(mapId.substring(4));
    }

    public static MapData getActiveAtlasMapState(World world, ItemStack atlas) {
        List<MapData> mapStates = getAllMapStatesFromAtlas(world, atlas);
        for (MapData state : mapStates) {
            for (Map.Entry<String, MapDecoration> entry : state.mapDecorations.entrySet()) {
                if (entry.getValue().getType() == MapDecoration.Type.PLAYER) {
                    previousMapState = state;
                    return state;
                }
            }
        }
        if (previousMapState != null) return previousMapState;
        for (MapData state : mapStates) {
            for (Map.Entry<String, MapDecoration> entry : state.mapDecorations.entrySet()) {
                if (entry.getValue().getType() == MapDecoration.Type.PLAYER_OFF_MAP) {
                    previousMapState = state;
                    return state;
                }
            }
        }
        return null;
    }

    public static int getEmptyMapCountFromItemStack(ItemStack atlas) {
        CompoundNBT tag = atlas.getTag();
        return tag != null && tag.contains("empty") ? tag.getInt("empty") : 0;
    }

    public static int getMapCountFromItemStack(ItemStack atlas) {
        CompoundNBT tag = atlas.getTag();
        return tag != null && tag.contains("maps") ? tag.getIntArray("maps").length : 0;
    }
}
