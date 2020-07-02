package io.github.giantnuker.fabric.informedload;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

/**
 * @author Indigo Amann
 */
@me.sargunvohra.mcmods.autoconfig1u.annotation.Config(name = InformedLoadUtils.MODID)
public class Config implements ConfigData {
    public boolean logDebugs = false;
    public enum SplitType {
        SPLIT, IN_ORDER, SINGLE
    }
    @ConfigEntry.Category("splash")
    @Comment("Display entrypoint loading")
    public boolean entrypointDisplay = true;
    @ConfigEntry.Category("splash")
    @Comment("If informed load has conflicts, run this to figure out what entrypoints go to what mods, so you can disable them")
    public boolean printEntrypoints = false;
    @ConfigEntry.Category("splash")
    @Comment("Model add progress bars")
    public SplitType splash_splitProgressBars = SplitType.SPLIT;
    @ConfigEntry.Category("splash")
    @Comment("Use 1 to keep the status on the main bar.")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 3)
    public int splash_maxProgressBarRows = 3;
    @ConfigEntry.Category("splash")
    @Comment("Keep same progress bars even if splash tewakers are installed.")
    public boolean splash_forceVanillaProgressBars = false;

    //@Comment("If you like red, Turn me off")
    //public boolean multicoloredProgressBars = true;
    public static class HateDisplay {
        @Comment("Don't like the new world load thingy?")
        public boolean progressBarDisplay = false;
    }

    public static class LoveDisplay {
        @Comment("Its really cool trust me")
        public boolean worldmap = true;
        @Comment("The smaller this number, the beefier your computer needs to be; 1-4 or something bad will happen")
        public int worldmap_quality = 2;
        @Comment("Bigger is better!")
        public boolean bigChunkViewer = true;
        @Comment("Do you like progress bars!!!")
        public boolean showProgressBar = true;
    }
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("worldload")
    public LoveDisplay worldload_loveDisplay = new LoveDisplay();
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("worldload")
    public HateDisplay worldload_hateDisplay = new HateDisplay();

    public SplashThemeConfig theme = new SplashThemeConfig();
}
