package com.wiggle1000.bloodworks.Crafting;

import com.wiggle1000.bloodworks.Blocks.BlockEntities.BlockEntityMachineInfusionChamber;
import com.wiggle1000.bloodworks.Registry.ItemRegistry;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RecipeBloodInfusion
{
    @NotNull private final Ingredient ingredient;
    @NotNull private final ItemStack output;
    @NotNull private final int bloodRequired, requiredTicks;
    @NotNull private final boolean coagulatedBlood;

    public RecipeBloodInfusion(int bloodVolumeRequired, Ingredient ingredient, ItemStack output, boolean isCoagulated, int requiredTicks)
    {
        this.bloodRequired = bloodVolumeRequired;
        this.output = output;
        this.ingredient = ingredient;
        this.coagulatedBlood = isCoagulated;
        this.requiredTicks = requiredTicks;
    }

    public boolean matches(BlockEntityMachineInfusionChamber container, Level level)
    {
        if (level != null && level.isClientSide()) return false;

        return ingredient.test(container.getItem(0)) && (!coagulatedBlood && container.getStackInSlot(1).is(ItemRegistry.BLOCK_INTESTINE.get()));
    }

    public ItemStack assemble(SimpleContainer container)
    {
        return null;
    }

    public boolean canCraftInDimensions(int posX, int posY)
    { return true; }

    public ItemStack getResultItem()
    { return output; }

    public int getBloodRequired()
    { return bloodRequired; }

    public boolean isCoagulatedBlood()
    { return coagulatedBlood; }

    public int getTicksRequired()
    { return requiredTicks; }

    public Ingredient getIngredient()
    { return ingredient; }
}