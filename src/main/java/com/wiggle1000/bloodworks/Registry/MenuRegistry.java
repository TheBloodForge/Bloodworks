package com.wiggle1000.bloodworks.Registry;

import com.wiggle1000.bloodworks.Globals;
import com.wiggle1000.bloodworks.Server.Menus.InfusionChamberMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuRegistry
{
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
            Globals.MODID);

    public static final RegistryObject<MenuType<InfusionChamberMenu>> INFUSION_CHAMBER =
            registerMenuType(InfusionChamberMenu::new, "infusion_chamber");

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory,
                                                                                                  String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }
}