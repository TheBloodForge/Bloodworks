package com.wiggle1000.bloodworks.Registry;

import com.wiggle1000.bloodworks.Globals;
import com.wiggle1000.bloodworks.Items.ItemGeneric;
import com.wiggle1000.bloodworks.Items.TankItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.wiggle1000.bloodworks.Globals.CREATIVE_TAB;

@SuppressWarnings("unused")
public class ItemRegistry
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Globals.MODID);

    public static final RegistryObject<Item> BLOCK_COAGULATED_BLOOD         = ITEMS.register("block_coagulated_blood", () -> new BlockItem(BlockRegistry.BLOCK_COAGULATED_BLOOD.get(), new Item.Properties().tab(CREATIVE_TAB)));
    public static final RegistryObject<Item> BLOCK_COAGULATED_BLOOD_SLAB    = ITEMS.register("block_coagulated_blood_slab", () -> new BlockItem(BlockRegistry.BLOCK_COAGULATED_BLOOD_SLAB.get(), new Item.Properties().tab(CREATIVE_TAB)));
    public static final RegistryObject<Item> BLOCK_COAGULATED_BLOOD_STAIRS  = ITEMS.register("block_coagulated_blood_stairs", () -> new BlockItem(BlockRegistry.BLOCK_COAGULATED_BLOOD_STAIRS.get(), new Item.Properties().tab(CREATIVE_TAB)));

    public static final RegistryObject<Item> BLOCK_INFUSION_CHAMBER         = ITEMS.register("block_infusion_chamber", () -> new BlockItem(BlockRegistry.BLOCK_INFUSION_CHAMBER.get(), new Item.Properties().tab(CREATIVE_TAB)));
    public static final RegistryObject<Item> BLOCK_BLOOD_TANK               = ITEMS.register("block_blood_tank", () -> new TankItem(BlockRegistry.BLOCK_BLOOD_TANK.get()));
    public static final RegistryObject<Item> BLOCK_INTESTINE                = ITEMS.register("block_intestine", () -> new BlockItem(BlockRegistry.BLOCK_INTESTINE.get(), new Item.Properties().tab(CREATIVE_TAB)));

    public static final RegistryObject<Item> BLOCK_FLESH_LIGHT              = ITEMS.register("block_flesh_light", () -> new BlockItem(BlockRegistry.BLOCK_FLESH_LIGHT.get(), new Item.Properties().tab(CREATIVE_TAB)));
    public static final RegistryObject<Item> BLOCK_FLESH_LIGHT_LARGE        = ITEMS.register("block_flesh_light_large", () -> new BlockItem(BlockRegistry.BLOCK_FLESH_LIGHT_LARGE.get(), new Item.Properties().tab(CREATIVE_TAB)));

    public static final RegistryObject<Item> BLOCK_FLESH                    = ITEMS.register("block_flesh", () -> new BlockItem(BlockRegistry.BLOCK_FLESH.get(), new Item.Properties().tab(CREATIVE_TAB)));
    public static final RegistryObject<Item> BLOCK_FLESH_PORTHOLE           = ITEMS.register("block_flesh_porthole", () -> new BlockItem(BlockRegistry.BLOCK_FLESH_PORTHOLE.get(), new Item.Properties().tab(CREATIVE_TAB)));
    public static final RegistryObject<Item> ITEM_STABILIZER                = ITEMS.register("item_stabilizer", ItemGeneric::new);
    public static final RegistryObject<Item> ITEM_COAGULATED_BLOOD          = ITEMS.register("item_coagulated_blood", ItemGeneric::new);
    public static final RegistryObject<Item> ITEM_SINEW                     = ITEMS.register("item_sinew", ItemGeneric::new);
}