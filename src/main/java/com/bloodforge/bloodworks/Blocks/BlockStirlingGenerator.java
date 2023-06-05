package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_Neuron;
import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_StirlingGenerator;
import com.bloodforge.bloodworks.Networking.MessageS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Registry.FluidRegistry;
import com.bloodforge.bloodworks.Registry.ItemRegistry;
import com.ibm.icu.impl.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.tags.TagKey;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class BlockStirlingGenerator extends BaseEntityBlock
{
    protected VoxelShape BLOCK_SHAPE = box(1, 1, 1, 15, 15, 15);

    //temperatures in celsius
    public static final Hashtable<String, Integer> blockTemperatures = new Hashtable<>();
    static
    {
        //--------hot blocks, derees celsius
        //vanilla
        blockTemperatures.put("minecraft:lava", 1250);
        blockTemperatures.put("minecraft:lava_cauldron", 1250);
        blockTemperatures.put("minecraft:magma_block", 1000);
        blockTemperatures.put("#minecraft:fire", 700);
        blockTemperatures.put("#minecraft:campfires", 600);
        blockTemperatures.put("#minecraft:infiniburn_overworld", 200);
        //mod support
        blockTemperatures.put("create:lit_blaze_burner", 2000);
        blockTemperatures.put("create:blaze_burner", 1250);

        //--------cold blocks. temperatures represent the heat they "steal" per tick
        //vanilla
        blockTemperatures.put("minecraft:iron_block", -50);
        blockTemperatures.put("minecraft:copper_block", -150);
        blockTemperatures.put("minecraft:cut_copper", -150);
        blockTemperatures.put("minecraft:waxed_copper_block", -150);
        blockTemperatures.put("minecraft:waxed_cut_copper", -150);
        blockTemperatures.put("minecraft:water", -150);
        blockTemperatures.put("#minecraft:ice", -200);
        blockTemperatures.put("#minecraft:snow", -200);
        blockTemperatures.put("minecraft:blue_ice", -400);
        //mod support
        //...?

    }

    public BlockStirlingGenerator()
    {
        super(BlockBehaviour.Properties.of(Material.METAL).color(MaterialColor.METAL));
    }

    // -------- Block entity stuff --------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockRegistry.BLOCK_STIRLING_GENERATOR.blockEntity().get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if(level.isClientSide()) return null;
        return createTickerHelper(type, (BlockEntityType<BE_StirlingGenerator>) BlockRegistry.BLOCK_STIRLING_GENERATOR.blockEntity().get(), BE_StirlingGenerator::tickServer);
    }

    @Override
    public VoxelShape getShape(BlockState p_51104_, BlockGetter p_51105_, BlockPos p_51106_, CollisionContext p_51107_)
    {
        return BLOCK_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if (!level.isClientSide())
        {
            //todo: tell client gen stats
            if(level.getBlockEntity(pos) instanceof BE_StirlingGenerator stirlingGenerator)
            {
                PacketManager.sendToPlayer(new MessageS2CPacket(Component.literal(
                        stirlingGenerator.battery.getStored() + "/" + stirlingGenerator.battery.getCapacity() + " | +"+stirlingGenerator.energyGeneration+"FE/t"),false),
                        (ServerPlayer) player);
            }
            return super.use(cState, level, pos, player, interactionHand, blockHitResult);
        }

        return InteractionResult.sidedSuccess(!level.isClientSide());
    }

    public static int TryGetTempForBlockState(BlockState state)
    {
        String blockName = Registry.BLOCK.getKey(state.getBlock()).toString();
        System.out.println(blockName);
        //check for exact match
        if(blockTemperatures.containsKey(blockName))
        {
            return blockTemperatures.get(blockName);
        }
        //check by tags
        for (Object tagKeyO: state.getTags().toArray())
        {
            TagKey<Block> tagKey = (TagKey<Block>)tagKeyO;
            String cmpTag = "#" + tagKey.location().toString();
            if(blockTemperatures.containsKey(cmpTag))
            {
                return blockTemperatures.get(cmpTag);
            }
        }
        //give up
        return 20; //TODO: make this ambient temperature based on biome, height, etc
    }

    public static int GetGenerationFromBlocks(BlockState blockBelow, BlockState blockN, BlockState blockE, BlockState blockS, BlockState blockW)
    {
        float tempDiff = TryGetTempForBlockState(blockBelow)*4f -
            (TryGetTempForBlockState(blockN) +
             TryGetTempForBlockState(blockE) +
             TryGetTempForBlockState(blockS) +
             TryGetTempForBlockState(blockW));
        tempDiff /= 100.0; //FE/t/C
        if(tempDiff < 0) tempDiff = 0;
        System.out.println("Total temperature diff: " + tempDiff);
        return (int)Math.floor(tempDiff);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block newBlock, @NotNull BlockPos neighbor, boolean p_60514_) {
        super.neighborChanged(state, level, pos, newBlock, neighbor, p_60514_);
        if(level.isClientSide()) return;
        if(level.getBlockEntity(pos) instanceof BE_StirlingGenerator stirlingGenerator)
        {
            BlockState below = level.getBlockState(pos.below());
            BlockState n = level.getBlockState(pos.north());
            BlockState e = level.getBlockState(pos.east());
            BlockState s = level.getBlockState(pos.south());
            BlockState w = level.getBlockState(pos.west());
            stirlingGenerator.updateEnergyProduction(GetGenerationFromBlocks(below, n, e, s, w));
        }
    }
}