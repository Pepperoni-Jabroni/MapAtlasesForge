package pepjebs.mapatlases.recipe;

import com.google.common.primitives.Ints;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import pepjebs.mapatlases.MapAtlasesMod;
import pepjebs.mapatlases.item.MapAtlasItem;
import pepjebs.mapatlases.utils.MapAtlasesAccessUtils;

import java.util.*;

public class MapAtlasesAddRecipe extends SpecialRecipe {
    public MapAtlasesAddRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        List<ItemStack> itemStacks = MapAtlasesAccessUtils.getItemStacksFromGrid(inv);
        ItemStack atlas = MapAtlasesAccessUtils.getAtlasFromItemStacks(itemStacks);

        // Ensure there's an Atlas
        if (atlas.isEmpty()) return false;
        MapData sampleMap = MapAtlasesAccessUtils.getRandomMapStateFromAtlas(world, atlas);

        // Ensure only correct ingredients are present
        if (!(itemStacks.size() > 1 && MapAtlasesAccessUtils.isListOnylIngredients(itemStacks))) return false;
        List<MapData> mapStates = MapAtlasesAccessUtils.getMapStatesFromItemStacks(world, itemStacks);

        // Ensure we're not trying to add too many Maps
        int empties = MapAtlasesAccessUtils.getEmptyMapCountFromItemStack(atlas);
        int mapCount = MapAtlasesAccessUtils.getMapCountFromItemStack(atlas);
        if (empties + mapCount + itemStacks.size() - 1 > MapAtlasItem.getMaxMapCount()) return false;

        // Ensure Filled Maps are all same Scale & Dimension
        if(!(MapAtlasesAccessUtils.areMapsSameScale(sampleMap, mapStates) &&
                MapAtlasesAccessUtils.areMapsSameDimension(sampleMap, mapStates))) return false;

        // Ensure there's only one Atlas
        return itemStacks.stream().filter(i ->
                i.isItemEqual(new ItemStack(MapAtlasesMod.getAtlasItem()))).count() == 1;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ClientWorld world = Minecraft.getInstance().world;
        List<ItemStack> itemStacks = MapAtlasesAccessUtils.getItemStacksFromGrid(inv);
        // Grab the Atlas in the Grid
        ItemStack atlas = MapAtlasesAccessUtils.getAtlasFromItemStacks(itemStacks);
        // Get the Map Ids in the Grid
        Set<Integer> mapIds = MapAtlasesAccessUtils.getMapIdsFromItemStacks(world, itemStacks);
        // Set NBT Data
        int emptyMapCount = (int)itemStacks.stream().filter(i -> i.isItemEqual(new ItemStack(Items.MAP))).count();
        CompoundNBT compoundTag = atlas.getOrCreateTag();
        Set<Integer> existingMaps = new HashSet<>(Ints.asList(compoundTag.getIntArray("maps")));
        existingMaps.addAll(mapIds);
        compoundTag.putIntArray("maps", existingMaps.stream().mapToInt(i->i).toArray());
        compoundTag.putInt("empty", emptyMapCount + compoundTag.getInt("empty"));
        atlas.setTag(compoundTag);
        return atlas;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return MapAtlasesMod.MAP_ATLAS_ADD_RECIPE.get();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }
}
