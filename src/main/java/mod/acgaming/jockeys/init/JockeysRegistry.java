package mod.acgaming.jockeys.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import mod.acgaming.jockeys.Jockeys;
import mod.acgaming.jockeys.entity.SkeletonBat;
import mod.acgaming.jockeys.entity.VexBat;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Jockeys.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JockeysRegistry
{
    // ENTITIES
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Jockeys.MOD_ID);
    public static final RegistryObject<EntityType<SkeletonBat>> SKELETON_BAT = ENTITIES.register("skeleton_bat", () ->
        EntityType.Builder.of(SkeletonBat::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(1).build(new ResourceLocation(Jockeys.MOD_ID, "skeleton_bat").toString())
    );
    public static final RegistryObject<EntityType<VexBat>> VEX_BAT = ENTITIES.register("vex_bat", () ->
        EntityType.Builder.of(VexBat::new, MobCategory.MONSTER).sized(0.5F, 0.9F).setTrackingRange(64).setUpdateInterval(1).build(new ResourceLocation(Jockeys.MOD_ID, "vex_bat").toString())
    );
    // ITEMS
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Jockeys.MOD_ID);
    public static final RegistryObject<ForgeSpawnEggItem> SKELETON_BAT_EGG = ITEMS.register("skeleton_bat_spawn_egg", () ->
        new ForgeSpawnEggItem(SKELETON_BAT, 4996656, 986895, new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );
    public static final RegistryObject<ForgeSpawnEggItem> VEX_BAT_EGG = ITEMS.register("vex_bat_spawn_egg", () ->
        new ForgeSpawnEggItem(VEX_BAT, 4996656, 986895, new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event)
    {
        SpawnPlacements.register(SKELETON_BAT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SkeletonBat::checkSkeletonBatSpawnRules);
        SpawnPlacements.register(VEX_BAT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VexBat::checkVexBatSpawnRules);
    }

    @SubscribeEvent
    public static void registerAttributes(final EntityAttributeCreationEvent event)
    {
        event.put(SKELETON_BAT.get(), SkeletonBat.createAttributes().build());
        event.put(VEX_BAT.get(), VexBat.createAttributes().build());
    }
}