package mod.acgaming.batjockeys.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import mod.acgaming.batjockeys.Reference;
import mod.acgaming.batjockeys.entity.LargeBatEntity;

public class BatJockeysRegistry
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MOD_ID);

    public static final RegistryObject<EntityType<LargeBatEntity>> LARGE_BAT = ENTITIES.register("large_bat",
        () -> register("large_bat", EntityType.Builder.create(LargeBatEntity::new, EntityClassification.MONSTER)
            .size(1.5F, 2.2F).trackingRange(5)));

    public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder, boolean sendVelocityUpdates)
    {
        return builder.setShouldReceiveVelocityUpdates(sendVelocityUpdates).build(id);
    }

    public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder)
    {
        return register(id, builder, true);
    }
}