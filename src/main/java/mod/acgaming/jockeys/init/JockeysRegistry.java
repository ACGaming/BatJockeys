package mod.acgaming.jockeys.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
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
import mod.acgaming.jockeys.entity.SniperWitherSkeleton;
import mod.acgaming.jockeys.entity.VexBat;
import mod.acgaming.jockeys.entity.WitherSkeletonGhast;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Jockeys.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JockeysRegistry
{
    // ENTITIES
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Jockeys.MOD_ID);
    // Skeleton Bat
    public static final RegistryObject<EntityType<SkeletonBat>> SKELETON_BAT = ENTITIES.register("skeleton_bat", () ->
        EntityType.Builder.of(SkeletonBat::new, MobCategory.MONSTER)
            .sized(1.5F, 2.0F)
            .clientTrackingRange(5)
            .build(new ResourceLocation(Jockeys.MOD_ID, "skeleton_bat").toString())
    );
    // Vex Bat
    public static final RegistryObject<EntityType<VexBat>> VEX_BAT = ENTITIES.register("vex_bat", () ->
        EntityType.Builder.of(VexBat::new, MobCategory.MONSTER)
            .sized(0.5F, 0.9F)
            .clientTrackingRange(5)
            .build(new ResourceLocation(Jockeys.MOD_ID, "vex_bat").toString())
    );
    // Wither Skeleton Ghast
    public static final RegistryObject<EntityType<WitherSkeletonGhast>> WITHER_SKELETON_GHAST = ENTITIES.register("wither_skeleton_ghast", () ->
        EntityType.Builder.of(WitherSkeletonGhast::new, MobCategory.MONSTER)
            .fireImmune()
            .sized(2.5F, 2.5F)
            .clientTrackingRange(10)
            .build(new ResourceLocation(Jockeys.MOD_ID, "wither_skeleton_ghast").toString())
    );
    // Sniper Wither Skeleton
    public static final RegistryObject<EntityType<SniperWitherSkeleton>> SNIPER_WITHER_SKELETON = ENTITIES.register("sniper_wither_skeleton", () ->
        EntityType.Builder.of(SniperWitherSkeleton::new, MobCategory.MONSTER)
            .fireImmune()
            .immuneTo(Blocks.WITHER_ROSE)
            .sized(0.7F, 2.4F)
            .clientTrackingRange(8)
            .build(new ResourceLocation(Jockeys.MOD_ID, "sniper_wither_skeleton").toString())
    );
    // ITEMS
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Jockeys.MOD_ID);
    public static final RegistryObject<ForgeSpawnEggItem> SKELETON_BAT_EGG = ITEMS.register("skeleton_bat_spawn_egg", () ->
        new ForgeSpawnEggItem(SKELETON_BAT, 4996656, 986895, new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );
    public static final RegistryObject<ForgeSpawnEggItem> VEX_BAT_EGG = ITEMS.register("vex_bat_spawn_egg", () ->
        new ForgeSpawnEggItem(VEX_BAT, 8032420, 15265265, new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );
    public static final RegistryObject<ForgeSpawnEggItem> WITHER_SKELETON_GHAST_EGG = ITEMS.register("wither_skeleton_ghast_spawn_egg", () ->
        new ForgeSpawnEggItem(WITHER_SKELETON_GHAST, 16382457, 12369084, new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );
    public static final RegistryObject<ForgeSpawnEggItem> SNIPER_WITHER_SKELETON_EGG = ITEMS.register("sniper_wither_skeleton_spawn_egg", () ->
        new ForgeSpawnEggItem(SNIPER_WITHER_SKELETON, 1315860, 4672845, new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    @SubscribeEvent
    public static void registerAttributes(final EntityAttributeCreationEvent event)
    {
        event.put(SKELETON_BAT.get(), SkeletonBat.createAttributes().build());
        event.put(VEX_BAT.get(), VexBat.createAttributes().build());
        event.put(WITHER_SKELETON_GHAST.get(), WitherSkeletonGhast.createAttributes().build());
        event.put(SNIPER_WITHER_SKELETON.get(), SniperWitherSkeleton.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event)
    {
        SpawnPlacements.register(SKELETON_BAT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(VEX_BAT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(WITHER_SKELETON_GHAST.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WitherSkeletonGhast::checkSpawnRules);
    }

    public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder, boolean sendVelocityUpdates)
    {
        return builder.setShouldReceiveVelocityUpdates(sendVelocityUpdates).build(id);
    }

    public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder)
    {
        return register(id, builder, true);
    }
}