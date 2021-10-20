package aria1th.main.spawnproofhelper;

import aria1th.main.spawnproofhelper.config.Configs;
import net.fabricmc.api.ModInitializer;

import java.io.IOException;

public class SpawnProofHelperMod implements ModInitializer {

    public static final String MOD_ID = "SpawnProofHelper";

    @Override
    public void onInitialize() {
        try {
            new Configs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
