package com.bloodforge.bloodworks.Registry.DataGen;

import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

public class BloodworksLangProvidor extends LanguageProvider
{

    public BloodworksLangProvidor(DataGenerator gen)
    {
        super(gen, Globals.MODID, "EN_us");
    }

    @Override
    protected void addTranslations()
    {
        addItems();
    }

    private void addItems()
    {
        for (RegistryObject<Block> entry : BlockRegistry.BLOCKS.getEntries())
        {
            System.out.println("TESTING = " + entry.getKey().location());
        }
    }
}