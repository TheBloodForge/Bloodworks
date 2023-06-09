package com.bloodforge.bloodworks.Registry;

import com.bloodforge.bloodworks.Blocks.*;
import com.bloodforge.bloodworks.Blocks.BlockEntities.*;
import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Items.BloodworksBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class BlockRegistry
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Globals.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Globals.MODID);

    public record RegistryPair(RegistryObject<Block> block, RegistryObject<Item> item)
    {
    }

    public record BlockEntityRegister(RegistryObject<Block> block, RegistryObject<BlockEntityType<?>> blockEntity,
                                      RegistryObject<Item> item)
    {
    }

    public record BlockFamily(RegistryPair blockBase, RegistryPair blockStair, RegistryPair blockSlab,
                              RegistryPair blockWall)
    {
    }

    public static final BlockFamily BLOCK_COAGULATED = registerAllTypes("coagulated_blood");
    public static final BlockFamily BLOCK_FLESH = registerAllFlesh();

    public static final RegistryPair BLOCK_FLESH_LIGHT = createBlock("flesh_light", () -> new BlockFleshLight(false));
    public static final RegistryPair BLOCK_FLESH_LIGHT_LARGE = createBlock("flesh_light_large", () -> new BlockFleshLight(true));
    public static final RegistryPair BLOCK_FLESH_PORTHOLE = createBlock("flesh_porthole", BlockBloodyTransparentBase::new);

    public static final RegistryPair BLOCK_BRAINCASE = createBlock("braincase", () ->
            new GenericBlockBase(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 7f).sound(SoundType.METAL)));

    public static final RegistryPair BLOCK_BRAINCASE_WINDOW = createBlock("braincase_window", () ->
            new GenericBlockBase(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 4f).sound(SoundType.GLASS).noOcclusion().isSuffocating((a, b, c) -> false).isViewBlocking((a, b, c) -> false)).withGlasslikeProperties());

    public static final RegistryPair BLOCK_AIRLOCK_DRAIN = createBlock("braincase_airlock_drain", () ->
            new GenericBlockBase(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 7f).sound(SoundType.METAL)));

    public static final BlockEntityRegister BLOCK_BATTERY = createBlockEntity(
            "battery",
            BlockBattery::new,
            BE_Battery.class
    );

    public static final BlockEntityRegister BLOCK_BLOOD_DRAIN = createBlockEntity(
            "blood_drain",
            BlockBloodDrain::new,
            BE_BloodDrain.class
    );

    public static final BlockEntityRegister BLOCK_STIRLING_GENERATOR = createBlockEntity(
            "stirling_generator",
            BlockStirlingGenerator::new,
            BE_StirlingGenerator.class
    );

    public static final BlockEntityRegister BLOCK_AIRLOCK_DOOR = createBlockEntity(
            "braincase_airlock_door",
            BlockAirlockDoor::new,
            BE_AirlockDoor.class
    );

    public static final BlockEntityRegister BLOCK_INFUSION_CHAMBER = createBlockEntity(
            "infusion_chamber",
            BlockInfusionChamber::new,
            BE_InfusionChamber.class
    );

    public static final BlockEntityRegister BLOCK_BRAINCASE_CONTROLLER = createBlockEntity(
            "braincase_controller",
            BlockBraincaseController::new,
            BE_Braincase_Controller.class
    );

    public static final BlockEntityRegister BLOCK_INTESTINE = createBlockEntity(
            "intestine",
            BlockIntestine::new,
            BE_Intestine.class
    );

    public static final BlockEntityRegister BLOCK_BLOOD_TANK = createBlockEntity(
            "blood_tank",
            BlockBloodTank::new,
            BE_Tank.class
    );

    public static final BlockEntityRegister BLOCK_FLUID_PIPE = createBlockEntity(
            "fluid_pipe",
            BlockFluidPipe::new,
            BE_FluidPipe.class
    );

    public static final BlockEntityRegister BLOCK_ENERGY_PIPE = createBlockEntity(
            "energy_pipe",
            BlockEnergyPipe::new,
            BE_EnergyPipe.class
    );

    public static final BlockEntityRegister BLOCK_MULTI_PIPE = createBlockEntity(
            "multi_pipe",
            BlockMultiPipe::new,
            BE_MultiPipe.class
    );

    public static final BlockEntityRegister BLOCK_NEURON = createBlockEntity(
            "neuron",
            BlockNeuron::new,
            BE_Neuron.class
    );

    private static RegistryPair createBlock(String name, Supplier<Block> o)
    {
        RegistryObject<Block> bro = BLOCKS.register("block_" + name, o);
        RegistryObject<Item> iro = ItemRegistry.ITEMS.register("block_" + name, () -> new BloodworksBlockItem(bro.get()));
        return new RegistryPair(bro, iro);
    }

    private static BlockEntityRegister createBlockEntity(String name, Supplier<Block> o, Class<? extends BlockEntity> o2)
    {
        RegistryObject<Block> bro = BLOCKS.register("block_" + name, o);
        RegistryObject<Item> iro = ItemRegistry.ITEMS.register("block_" + name, () -> new BloodworksBlockItem(bro.get()));

        RegistryObject<BlockEntityType<?>> bero = BLOCK_ENTITIES.register("be_" + name,
                () -> BlockEntityType.Builder.of(
                        (BlockEntityType.BlockEntitySupplier<BlockEntity>) (pos, state) ->
                        {
                            try
                            {
                                return o2.getDeclaredConstructor(BlockPos.class, BlockState.class).newInstance(pos, state);
                            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                                     IllegalAccessException e)
                            {
                                throw new RuntimeException(e);
                            }
                        }
                        ,
                        bro.get()
                ).build(null));

        return new BlockEntityRegister(bro, bero, iro);
    }

    private static BlockFamily registerAllTypes(String name)
    {
        RegistryPair block = createBlock(name, BlockBloodyBase::new);
        RegistryPair blockStair = createBlock(name + "_stairs", BlockBloodyStairsBase::new);
        RegistryPair blockSlab = createBlock(name + "_slab", BlockBloodySlabBase::new);
        RegistryPair blockWall = createBlock(name + "_wall", BlockBloodyWallBase::new);
        return new BlockFamily(block, blockStair, blockSlab, blockWall);
    }
    private static BlockFamily registerAllFlesh()
    {
        RegistryPair block = createBlock("flesh", BlockFlesh::new);
        RegistryPair blockStair = createBlock("flesh_stairs", BlockBloodyStairsBase::new);
        RegistryPair blockSlab = createBlock("flesh_slab", BlockBloodySlabBase::new);
        RegistryPair blockWall = createBlock("flesh_wall", BlockBloodyWallBase::new);
        return new BlockFamily(block, blockStair, blockSlab, blockWall);
    }
}