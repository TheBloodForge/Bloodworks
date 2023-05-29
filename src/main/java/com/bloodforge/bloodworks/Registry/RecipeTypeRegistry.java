package com.bloodforge.bloodworks.Registry;

import com.bloodforge.bloodworks.Globals;
import mezz.jei.api.recipe.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeTypeRegistry
{
    public static final DeferredRegister<RecipeType> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES.getRegistryName(), Globals.MODID);
}
