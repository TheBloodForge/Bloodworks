package com.wiggle1000.bloodworks.Crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class RecipeBloodInfusion implements Recipe<SimpleContainer>
{
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;

    public RecipeBloodInfusion(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems)
    {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level)
    {
        if (level.isClientSide()) return false;

        return recipeItems.get(0).test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer container)
    {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int posX, int posY)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem()
    {
        return null;
    }

    @Override
    public ResourceLocation getId()
    {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return null;
    }

    @Override
    public RecipeType<?> getType()
    {
        return null;
    }
}
