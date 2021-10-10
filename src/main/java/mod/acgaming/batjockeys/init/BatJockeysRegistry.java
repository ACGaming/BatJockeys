package mod.acgaming.batjockeys.init;

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

import mod.acgaming.batjockeys.BatJockeys;
import mod.acgaming.batjockeys.entity.LargeBat;

@Mod.EventBusSubscriber(modid = BatJockeys.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BatJockeysRegistry
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, BatJockeys.MOD_ID);
    public static final RegistryObject<EntityType<LargeBat>> LARGE_BAT = ENTITIES.register("large_bat", () ->
        EntityType.Builder.of(LargeBat::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(1).build(new ResourceLocation(BatJockeys.MOD_ID, "large_bat").toString())
    );
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BatJockeys.MOD_ID);
    public static final RegistryObject<ForgeSpawnEggItem> LARGE_BAT_EGG = ITEMS.register("large_bat_spawn_egg", () ->
        new ForgeSpawnEggItem(LARGE_BAT, 4996656, 986895, new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event)
    {
        SpawnPlacements.register(LARGE_BAT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LargeBat::checkLargeBatSpawnRules);
    }

    @SubscribeEvent
    public static void registerAttributes(final EntityAttributeCreationEvent event)
    {
        event.put(LARGE_BAT.get(), LargeBat.createAttributes().build());
    }
}