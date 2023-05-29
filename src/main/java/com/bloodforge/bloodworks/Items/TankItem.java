package com.bloodforge.bloodworks.Items;

import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_BloodTank;
import com.bloodforge.bloodworks.Client.ItemRenderers.TankRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class TankItem extends BlockItem
{
    public final int tankCapacity = 10000;
    public TankItem(Block block)
    {
        super(block, new Item.Properties().tab(Globals.CREATIVE_TAB));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag advanced){
        FluidStack fluidStack = FluidStack.EMPTY;
        if(stack.getOrCreateTag().contains("tileData"))
            fluidStack = FluidStack.loadFluidStackFromNBT(stack.getOrCreateTag().getCompound("tileData").getCompound("fluid"));
        Component capacity = Component.literal(Integer.toString(this.tankCapacity)).withStyle(ChatFormatting.GOLD);
        if(fluidStack.isEmpty())
            list.add(Component.translatable("bloodworks.blood_tank.info.capacity", capacity).setStyle(Component.translatable("bloodworks.blood_tank.info.capacity", capacity).getStyle().applyFormat(ChatFormatting.GRAY)));
        else{
            Component fluidName = fluidStack.getDisplayName().copy().setStyle(fluidStack.getDisplayName().getStyle().applyFormat(ChatFormatting.GOLD));
            Component amount = Component.literal(Integer.toString(fluidStack.getAmount())).withStyle(ChatFormatting.GOLD);
            list.add(Component.translatable("bloodworks.blood_tank.info.storage", capacity).setStyle(Component.translatable("bloodworks.blood_tank.info.storage", capacity).getStyle().applyFormat(ChatFormatting.GRAY)));
        }
        super.appendHoverText(stack, world, list, advanced);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer){
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer(){
                return new TankRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher());
            }
        });
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt){
        return new ItemFluidHandler(stack);
    }

    public static class ItemFluidHandler implements ICapabilityProvider, IFluidHandlerItem {

        private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

        private final ItemStack stack;

        public ItemFluidHandler(ItemStack stack){
            this.stack = stack;
        }

        @Override
        public int getTanks(){
            return 1;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank){
            return this.getFluid().copy();
        }

        @Override
        public int getTankCapacity(int tank){
            return 10000;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack){
            return true;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action){
            if(resource == null || resource.isEmpty())
                return 0;
            FluidStack current = this.getFluid();
            if(!current.isEmpty() && !current.isFluidEqual(resource))
                return 0;
            int amount = Math.min(resource.getAmount(), this.getTankCapacity(0) - current.getAmount());
            if(action.execute()){
                FluidStack newStack = resource.copy();
                newStack.setAmount(current.getAmount() + amount);
                this.setFluid(newStack);
            }
            return amount;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action){
            if(resource == null || resource.isEmpty())
                return FluidStack.EMPTY;
            FluidStack current = this.getFluid();
            if(current.isEmpty() || !current.isFluidEqual(resource))
                return FluidStack.EMPTY;
            int amount = Math.min(current.getAmount(), resource.getAmount());
            if(action.execute()){
                FluidStack newStack = current.copy();
                newStack.shrink(amount);
                this.setFluid(newStack);
            }
            current.setAmount(amount);
            return current;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action){
            if(maxDrain == 0)
                return FluidStack.EMPTY;
            FluidStack current = this.getFluid();
            if(current.isEmpty())
                return FluidStack.EMPTY;
            int amount = Math.min(current.getAmount(), maxDrain);
            if(action.execute()){
                FluidStack newStack = current.copy();
                newStack.shrink(amount);
                this.setFluid(newStack);
            }
            current.setAmount(amount);
            return current;
        }

        @Nonnull
        @Override
        public ItemStack getContainer(){
            return this.stack;
        }

        @Override
        @Nonnull
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing){
            return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, this.holder);
        }

        private FluidStack getFluid(){
            CompoundTag compound = this.stack.getOrCreateTag().getCompound("tileData");
            return compound.contains("fluid") ? FluidStack.loadFluidStackFromNBT(compound.getCompound("fluid")) : FluidStack.EMPTY;
        }

        private void setFluid(FluidStack fluid){
            CompoundTag tileData = this.stack.getOrCreateTag().getCompound("tileData");
            tileData.put("fluid", fluid.writeToNBT(new CompoundTag()));
            this.stack.getOrCreateTag().put("tileData", tileData);
        }
    }
    public BE_BloodTank createTileEntity(BlockPos pos, BlockState state) {
        return new BE_BloodTank(this, pos, state);
    }
}