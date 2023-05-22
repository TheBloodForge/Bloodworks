package com.wiggle1000.bloodworks.Registry;

import com.wiggle1000.bloodworks.Globals;
import mezz.jei.api.recipe.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeTypeRegistry
{
    public static final DeferredRegister<RecipeType>  RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES.getRegistryName(), Globals.MODID);
}
