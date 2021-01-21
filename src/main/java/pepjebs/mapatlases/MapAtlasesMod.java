package pepjebs.mapatlases;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pepjebs.mapatlases.item.MapAtlasItem;
import pepjebs.mapatlases.recipe.MapAtlasCreateRecipe;
import pepjebs.mapatlases.recipe.MapAtlasesAddRecipe;

@Mod(MapAtlasesMod.MOD_ID)
public class MapAtlasesMod {

    public static final String MOD_ID = "map_atlases";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static RegistryObject<IRecipeSerializer<MapAtlasCreateRecipe>> MAP_ATLAS_CREATE_RECIPE;
    public static RegistryObject<IRecipeSerializer<MapAtlasesAddRecipe>> MAP_ATLAS_ADD_RECIPE;

    public MapAtlasesMod() {
        DeferredRegister<Item> items = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
        RegistryObject<Item> atlas = items.register("atlas", () ->
                new MapAtlasItem(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1)));
        items.register(FMLJavaModLoadingContext.get().getModEventBus());

        DeferredRegister<IRecipeSerializer<?>> recipes =
                DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);
        recipes.register("crafting_atlas", () -> new SpecialRecipeSerializer<>(MapAtlasCreateRecipe::new));
        recipes.register("adding_atlas", () -> new SpecialRecipeSerializer<>(MapAtlasesAddRecipe::new));
        recipes.register(FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static Item getAtlasItem() {
        return RegistryObject.of(new ResourceLocation(MOD_ID, "atlas"), () -> Item.class).get();
    }
}
