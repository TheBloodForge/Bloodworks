package com.bloodforge.bloodworks.Registry;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_InfusionChamber;
import com.bloodforge.bloodworks.Crafting.RecipeBloodInfusion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class RecipeRegistry
{
    public static final List<RecipeBloodInfusion> BLOOD_INFUSION_RECIPES = new ArrayList<>();

    public static void init()
    {
        addInfusionRecipe(200, Ingredient.of(Items.ANDESITE), new ItemStack(BlockRegistry.BLOCK_COAGULATED.blockBase().item().get(), 1), true);
    }

    public static void addInfusionRecipe(int requiredBlood, Ingredient ingredient, ItemStack output, boolean usesCoagulatedBlood)
    {
        addInfusionRecipe(new RecipeBloodInfusion(requiredBlood, ingredient, output, usesCoagulatedBlood, 60));
    }

    public static void addInfusionRecipe(RecipeBloodInfusion recipe)
    {
        BLOOD_INFUSION_RECIPES.add(recipe);
    }

    public static RecipeBloodInfusion getInfusionRecipeFromInputs(BE_InfusionChamber container)
    {
        for (RecipeBloodInfusion bloodInfusionRecipe : BLOOD_INFUSION_RECIPES)
        {
            if (bloodInfusionRecipe.matches(container, null)) return bloodInfusionRecipe;
        }
        return null;
    }
}