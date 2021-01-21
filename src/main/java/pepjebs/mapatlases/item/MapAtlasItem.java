package pepjebs.mapatlases.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import pepjebs.mapatlases.utils.MapAtlasesAccessUtils;

import javax.annotation.Nullable;
import java.util.List;

public class MapAtlasItem extends Item {

    public MapAtlasItem(Properties settings) {
        super(settings);
    }

    public static int getMaxMapCount() {
        return 128;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, world, tooltip, flagIn);

        if (world != null && world.isRemote) {
            MapData mapState = MapAtlasesAccessUtils.getRandomMapStateFromAtlas(world, stack);
            if (mapState == null) {
                tooltip.add(new TranslationTextComponent("item.map_atlases.atlas.tooltip_err")
                        .mergeStyle(TextFormatting.ITALIC).mergeStyle(TextFormatting.GRAY));
                return;
            }
            int mapSize = MapAtlasesAccessUtils.getMapCountFromItemStack(stack);
            int empties = MapAtlasesAccessUtils.getEmptyMapCountFromItemStack(stack);
            if (mapSize + empties >= getMaxMapCount()) {
                tooltip.add(new TranslationTextComponent("item.map_atlases.atlas.tooltip_full")
                        .mergeStyle(TextFormatting.ITALIC).mergeStyle(TextFormatting.GRAY));
            }
            tooltip.add(new TranslationTextComponent("item.map_atlases.atlas.tooltip_1", mapSize)
                    .mergeStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("item.map_atlases.atlas.tooltip_2", empties)
                    .mergeStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("item.map_atlases.atlas.tooltip_3", 1 << mapState.scale)
                    .mergeStyle(TextFormatting.GRAY));
        }
    }

//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
//        player.openHandledScreen(this);
//        world.playSound(player.getX(), player.getY(), player.getZ(), MapAtlasesMod.ATLAS_OPEN_SOUND_EVENT,
//                SoundCategory.PLAYERS, 1.0F, 1.0F, false);
//        return TypedActionResult.consume(player.getStackInHand(hand));
//    }

//    @Override
//    public Text getDisplayName() {
//        return new TranslationTextComponent(getTranslationKey());
//    }
//
//    @Nullable
//    @Override
//    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
//        ItemStack atlas = MapAtlasesAccessUtils.getAtlasFromItemStacks(inv.main);
//        Map<Integer, List<Integer>> idsToCenters = new HashMap<>();
//        List<MapState> mapStates = MapAtlasesAccessUtils.getAllMapStatesFromAtlas(player.world, atlas);
//        for (MapState state : mapStates) {
//            idsToCenters.put(MapAtlasesAccessUtils.getMapIntFromState(state), Arrays.asList(state.xCenter, state.zCenter));
//        }
//        return new MapAtlasesAtlasOverviewScreenHandler(syncId, inv, idsToCenters);
//    }
//
//    @Override
//    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
//        ItemStack atlas = MapAtlasesAccessUtils.getAtlasFromItemStacks(serverPlayerEntity.inventory.main);
//        if (atlas.isEmpty()) return;
//        List<MapState> mapStates =
//                MapAtlasesAccessUtils.getAllMapStatesFromAtlas(serverPlayerEntity.getServerWorld(), atlas);
//        if (mapStates.isEmpty()) return;
//        packetByteBuf.writeInt(mapStates.size());
//        for (MapState state : mapStates) {
//            packetByteBuf.writeInt(MapAtlasesAccessUtils.getMapIntFromState(state));
//            packetByteBuf.writeInt(state.xCenter);
//            packetByteBuf.writeInt(state.zCenter);
//        }
//    }
//
//    public ActionResult useOnBlock(ItemUsageContext context) {
//        BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
//        if (blockState.isIn(BlockTags.BANNERS)) {
//            if (!context.getWorld().isClient) {
//                MapState mapState =
//                        MapAtlasesAccessUtils.getActiveAtlasMapState(context.getWorld(), context.getStack());
//                if (mapState != null) {
//                    mapState.addBanner(context.getWorld(), context.getBlockPos());
//                }
//            }
//            return ActionResult.success(context.getWorld().isClient);
//        } else {
//            return super.useOnBlock(context);
//        }
//    }
}
