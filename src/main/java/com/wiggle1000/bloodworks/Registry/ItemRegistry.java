package com.wiggle1000.bloodworks.Registry;

import com.wiggle1000.bloodworks.Globals;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.wiggle1000.bloodworks.Globals.CREATIVE_TAB;

public class ItemRegistry
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Globals.MODID);

    public static final RegistryObject<Item> BLOCK_COAGULATED_BLOOD = ITEMS.register("block_coagulated_blood", () -> new BlockItem(BlockRegistry.BLOCK_COAGULATED_BLOOD.get(), new Item.Properties().tab(CREATIVE_TAB)));
    public static final RegistryObject<Item> BLOCK_COAGULATED_BLOOD_SLAB = ITEMS.register("block_coagulated_blood_slab", () -> new BlockItem(BlockRegistry.BLOCK_COAGULATED_BLOOD_SLAB.get(), new Item.Properties().tab(CREATIVE_TAB)));
    public static final RegistryObject<Item> BLOCK_COAGULATED_BLOOD_STAIRS = ITEMS.register("block_coagulated_blood_stairs", () -> new BlockItem(BlockRegistry.BLOCK_COAGULATED_BLOOD_STAIRS.get(), new Item.Properties().tab(CREATIVE_TAB)));

    public static final RegistryObject<Item> BLOCK_INTESTINE = ITEMS.register("block_intestine", () -> new BlockItem(BlockRegistry.BLOCK_INTESTINE.get(), new Item.Properties().tab(CREATIVE_TAB)));
}