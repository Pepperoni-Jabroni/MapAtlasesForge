package pepjebs.mapatlases.recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import pepjebs.mapatlases.MapAtlasesMod;
import pepjebs.mapatlases.utils.MapAtlasesAccessUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapAtlasCreateRecipe extends SpecialRecipe {

    public MapAtlasCreateRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        ItemStack filledMap = ItemStack.EMPTY;
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                itemStacks.add(inv.getStackInSlot(i));
                if (inv.getStackInSlot(i).getItem() == Items.FILLED_MAP) {
                    filledMap = inv.getStackInSlot(i);
                }
            }
        }
        if (itemStacks.size() == 3) {
            List<Item> items = itemStacks.stream().map(ItemStack::getItem).collect(Collectors.toList());
            boolean hasAllCrafting =
                    items.containsAll(Arrays.asList(Items.FILLED_MAP, Items.SLIME_BALL, Items.BOOK)) ||
                            items.containsAll(Arrays.asList(Items.FILLED_MAP, Items.HONEY_BOTTLE, Items.BOOK));
            if (hasAllCrafting && !filledMap.isEmpty()) {
                MapData state = FilledMapItem.getMapData(filledMap, world);
                if (state == null) return false;
                return state.dimension == World.OVERWORLD;
            }
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack mapItemStack = null;
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i).isItemEqual(new ItemStack(Items.FILLED_MAP))) {
                mapItemStack = inv.getStackInSlot(i);
            }
        }
        if (mapItemStack == null || Minecraft.getInstance().world == null) {
            return ItemStack.EMPTY;
        }
        MapData mapState = FilledMapItem.getData(mapItemStack, Minecraft.getInstance().world);
        if (mapState == null) return ItemStack.EMPTY;
        Item mapAtlasItem = Registry.ITEM.getOrDefault(new ResourceLocation(MapAtlasesMod.MOD_ID, "atlas"));
        CompoundNBT compoundTag = new CompoundNBT();
        compoundTag.putIntArray("maps", new int[]{MapAtlasesAccessUtils.getMapIntFromState(mapState)});
        ItemStack atlasItemStack = new ItemStack(mapAtlasItem);
        atlasItemStack.setTag(compoundTag);
        return atlasItemStack;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return MapAtlasesMod.MAP_ATLAS_CREATE_RECIPE.get();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 3;
    }
}
