package com.bloodforge.bloodworks.Crafting;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_InfusionChamber;
import com.bloodforge.bloodworks.Registry.ItemRegistry;
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

    public boolean matches(BE_InfusionChamber container, Level level)
    {
        if (level != null && level.isClientSide()) return false;
        boolean hasIngredient = ingredient.test(container.getItem(1));
        boolean coagulatorCheck = (coagulatedBlood || container.getStackInSlot(0).is(ItemRegistry.ITEM_STABILIZER.get()));
        return hasIngredient && coagulatorCheck && container.getFluidInTank(0).getAmount() >= bloodRequired;
    }

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