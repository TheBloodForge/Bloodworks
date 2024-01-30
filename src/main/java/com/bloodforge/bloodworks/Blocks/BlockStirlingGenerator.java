package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_StirlingGenerator;
import com.bloodforge.bloodworks.Client.Sound.SoundHelper;
import com.bloodforge.bloodworks.Common.Config.BloodworksCommonConfig;
import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Networking.MessageS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;

public class BlockStirlingGenerator extends BlockMachineBase
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


    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
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

    public static float GetTempDiffFromBlocks(BlockState blockBelow, BlockState blockN, BlockState blockE, BlockState blockS, BlockState blockW)
    {
        float tempDiff = TryGetTempForBlockState(blockBelow)*4f -
                (TryGetTempForBlockState(blockN) +
                        TryGetTempForBlockState(blockE) +
                        TryGetTempForBlockState(blockS) +
                        TryGetTempForBlockState(blockW));
        return tempDiff;
    }
    public static int GetGenerationFromTempDiff(float tempDiffIn)
    {
        float tempDiff = tempDiffIn;
        tempDiff /= BloodworksCommonConfig.STIRLING_GENERATOR_GENERATION_MOD.get(); //FE/t/C
        if(tempDiff < 0) tempDiff = 0;
        return (int)Math.floor(tempDiff);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block newBlock, @NotNull BlockPos neighbor, boolean p_60514_) {
        super.neighborChanged(state, level, pos, newBlock, neighbor, p_60514_);
        if(level.isClientSide()) return;
        if(level.getBlockEntity(pos) instanceof BE_StirlingGenerator stirlingGenerator)
        {
            stirlingGenerator.DoEnergyRecalculation();
        }
    }


    public ResourceLocation getIdleSoundResourceLocation()
    {
        return new ResourceLocation(Globals.MODID, "bloodworks.stirling.idle");
    }
    public boolean hasIdleSound()
    {
        return true;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        super.animateTick(blockState, level, blockPos, randomSource);
        if(level.getBlockEntity(blockPos) instanceof BE_StirlingGenerator stirlingGenerator)
        {
            SoundHelper.SetTileSoundPitch(stirlingGenerator.energyGenerationF * 0.03f, blockPos);
            SoundHelper.SetTileSoundVolume(Mth.clamp(stirlingGenerator.energyGenerationF * 0.1f, 0, 1), blockPos);
        }
    }
}