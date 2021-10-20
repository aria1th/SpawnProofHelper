package aria1th.main.spawnproofhelper.config;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;

public abstract class Config extends Option {

    public static final CyclingOption<Boolean> SPAWNPROOF_ON;

    static {
        SPAWNPROOF_ON =  CyclingOption.create("spawnproofOn",
                config -> Configs.getSpawnProofOn(),
                (gameOptions,config,togglestate) -> Configs.setSpawnProofOn(togglestate)
        );
    }

    public Config(String key) {
        super(key);
    }

    @Override
    public ButtonWidget createButton(GameOptions options, int x, int y, int width) {
        return null;
    }
}