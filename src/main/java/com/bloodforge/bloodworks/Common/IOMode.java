package com.bloodforge.bloodworks.Common;

import net.minecraft.util.StringRepresentable;

public enum IOMode implements StringRepresentable
{
    INPUT("input"), OUTPUT("output"), NONE("none"), CONNECTED("connected"), INPUT_POWERED("input_powered"), OUTPUT_POWERED("output_powered");
    final String mode_id;
    IOMode(String id)
    {
        mode_id = id;
    }

    public static IOMode getNext(IOMode value)
    {
        return switch (value){
            case INPUT -> OUTPUT;
            case OUTPUT -> NONE;
            case NONE -> CONNECTED;
            case CONNECTED -> INPUT;
            case INPUT_POWERED -> OUTPUT_POWERED;
            case OUTPUT_POWERED -> INPUT_POWERED;
        };
    }

    public static IOMode getPowered(IOMode value)
    {
        if (value == INPUT) return INPUT_POWERED;
        if (value == OUTPUT) return OUTPUT_POWERED;
        if (value == INPUT_POWERED) return INPUT;
        if (value == OUTPUT_POWERED) return OUTPUT;
        return value;
    }

    @Override
    public String getSerializedName()
    {
        return mode_id;
    }
}