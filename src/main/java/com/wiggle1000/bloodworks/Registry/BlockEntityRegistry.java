package com.wiggle1000.bloodworks.Registry;

import com.wiggle1000.bloodworks.Blocks.BlockEntities.BlockEntityIntestine;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BlockEntityMachineInfusionChamber;
import com.wiggle1000.bloodworks.Globals;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegistry
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Globals.MODID);

    public static final RegistryObject<BlockEntityType<BlockEntityMachineInfusionChamber>> BLOCK_ENTITY_INFUSION_CHAMBER = BLOCK_ENTITIES.register("be_infusion_chamber",
            () -> BlockEntityType.Builder.of(
                    BlockEntityMachineInfusionChamber::new,
                    BlockRegistry.BLOCK_INFUSION_CHAMBER.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<BlockEntityIntestine>> BLOCK_ENTITY_INTESTINE = BLOCK_ENTITIES.register("be_intestine",
            () -> BlockEntityType.Builder.of(
                    BlockEntityIntestine::new,
                    BlockRegistry.BLOCK_INTESTINE.get()
            ).build(null));

}
