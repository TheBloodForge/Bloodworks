package com.wiggle1000.bloodworks.Registry;

import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_BloodTank;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_InfusionChamber;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_Intestine;
import com.wiggle1000.bloodworks.Globals;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegistry
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Globals.MODID);

    public static final RegistryObject<BlockEntityType<BE_InfusionChamber>> BE_INFUSION_CHAMBER = BLOCK_ENTITIES.register("be_infusion_chamber",
            () -> BlockEntityType.Builder.of(
                    BE_InfusionChamber::new,
                    BlockRegistry.BLOCK_INFUSION_CHAMBER.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<BE_Intestine>> BE_INTESTINE = BLOCK_ENTITIES.register("be_intestine",
            () -> BlockEntityType.Builder.of(
                    BE_Intestine::new,
                    BlockRegistry.BLOCK_INTESTINE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<BE_BloodTank>> BE_BLOOD_TANK = BLOCK_ENTITIES.register("be_blood_tank",
            () -> BlockEntityType.Builder.of(
                    BE_BloodTank::new,
                    BlockRegistry.BLOCK_BLOOD_TANK.get()
            ).build(null));
}