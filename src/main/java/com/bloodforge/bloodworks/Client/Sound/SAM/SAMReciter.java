package com.bloodforge.bloodworks.Client.Sound.SAM;

import com.bloodforge.bloodworks.Client.Sound.SoundHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.io.*;

public class SAMReciter
{
    public static void Speak(Vec3 position, String rawMessage)
    {
        Speak(position, rawMessage, 72, 64, 128, 128);
    }

    public static void Speak(Vec3 position, String rawMessage, int speed, int pitch, int throat, int mouth)
    {
        try
        {
            String message = rawMessage.toUpperCase().strip();
            //destroy non-allowed characters
            message = message.replaceAll("[^ A-Z0-9\\!\\?\\.\\,]","");
            //limit to 2 punctuations
            message = message.replaceAll("!!+","!!");
            message = message.replaceAll("\\.\\.+","..");
            message = message.replaceAll("\\?\\?+","??");
            message = message.replaceAll("[\\!\\?\\.\\,][\\!\\?\\.\\,][\\!\\?\\.\\,]+",".");
            //deduplicate spaces
            //message = message.replaceAll(" +"," ");
            //length limit to help prevent stack overflow
            if(message.length() > 120) message = message.substring(0, 120);
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            PrintStream p = new PrintStream (ba);
            SamClass.xmain(p, new String[]{
                    "-speed", Integer.toString(Mth.clamp(speed, 0, 255)),
                    "-pitch", Integer.toString(Mth.clamp(pitch, 0, 255)),
                    "-throat", Integer.toString(Mth.clamp(throat, 0, 255)),
                    "-mouth", Integer.toString(Mth.clamp(mouth, 0, 255)),
                    "-stdout","dummy",message});
            InputStream inputstream = new BufferedInputStream(new ByteArrayInputStream(ba.toByteArray()));
            inputstream.skip(32);
            SoundHelper.SAMSoundSource.stop();
            SoundHelper.SAMSoundSource.push(inputstream.readAllBytes());
            SoundHelper.SAMSoundSource.play(new BlockPos(position).toShortString(), (float)position.x, (float)position.y, (float)position.z);
        }
        catch (StackOverflowError e)
        {
            //this just happens sometimes; the original SAM C translation also has this issue. not ideal, but OK
            //i added some "runaway detection" to SamClass.java, so it stops before it affects anything noticeably and just doesn't make any sound.
            //e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //SoundHelper.playSound(instance);
        //Minecraft.getInstance().getSoundManager().play(instance);
    }
}
