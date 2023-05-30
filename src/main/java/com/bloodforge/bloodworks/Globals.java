package com.bloodforge.bloodworks;

import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class Globals
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "bloodworks";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    //TODO: on world load, reseed this to world seed?
    public static final Random RAND = new Random(System.nanoTime());
    public static final SimplexNoise SIMPLEX_NOISE = new SimplexNoise(RandomSource.create());
    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab("bloodworks")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(BlockRegistry.BLOCK_INTESTINE.item().get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> stacksInInv)
        {
            List<Item> itemList = Registry.ITEM.stream().sorted(Comparator.comparing(Item::getDescriptionId)).toList();
            for (Item item : itemList)
            {
                item.fillItemCategory(this, stacksInInv);
            }
        }

    };
    public static final int DEFAULT_TANK_CAPACITY = 5000;
    public static MinecraftServer SERVER = null;

    public static void LogInfo(String toLog)
    {
        LOGGER.info("[" + MODID + "] " + toLog);
    }

    public static void LogError(String toLog)
    {
        LOGGER.error("[" + MODID + "] " + toLog);
    }
}