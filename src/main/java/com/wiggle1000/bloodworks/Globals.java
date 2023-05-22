package com.wiggle1000.bloodworks;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Globals
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "bloodworks";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void LogInfo(String toLog)
    {
        LOGGER.info("[" + MODID + "] " + toLog);
    }
}