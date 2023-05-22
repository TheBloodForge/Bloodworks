package com.wiggle1000.bloodworks.Registry;

import com.wiggle1000.bloodworks.Globals;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Globals.MODID);

    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("block_coagulated_blood", () -> new BlockItem(BlockRegistry.COAGULATED_BLOOD.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
}