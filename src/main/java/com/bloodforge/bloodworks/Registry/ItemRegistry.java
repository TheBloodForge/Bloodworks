package com.bloodforge.bloodworks.Registry;

import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Items.ItemGeneric;
import com.bloodforge.bloodworks.Items.ItemWithDurability;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class ItemRegistry
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Globals.MODID);

//    public static final RegistryObject<Item> BLOCK_NEURON                   = ITEMS.register("block_neuron", () -> new BlockItem(BlockRegistry.BLOCK_NEURON.get(), new Item.Properties().tab(CREATIVE_TAB)));

    public static final RegistryObject<Item> ITEM_STABILIZER = ITEMS.register("item_stabilizer", () -> new ItemWithDurability(1000));
    public static final RegistryObject<Item> ITEM_COAGULATED_BLOOD = ITEMS.register("item_coagulated_blood", ItemGeneric::new);
    public static final RegistryObject<Item> ITEM_SINEW = ITEMS.register("item_sinew", ItemGeneric::new);
    public static final RegistryObject<Item> ITEM_NEURAL_CATALYST = ITEMS.register("item_neural_catalyst", ItemGeneric::new);
}