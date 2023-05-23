package com.wiggle1000.bloodworks;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.Random;

public class Globals
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "bloodworks";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    //TODO: on world load, reseed this to world seed?
    public static final Random RAND = new Random(System.nanoTime());

    public static void LogInfo(String toLog)
    {
        LOGGER.info("[" + MODID + "] " + toLog);
    }

    public static void LogError(String toLog)
    {
        LOGGER.error("[" + MODID + "] " + toLog);
    }
}