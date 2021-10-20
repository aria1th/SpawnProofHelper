package aria1th.main.spawnproofhelper.compat.modmenu;

import aria1th.main.spawnproofhelper.gui.SpawnProofHelperModScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.minecraft.client.gui.screen.Screen;

public class SpawnProofHelperModScreenFactory implements ConfigScreenFactory {
    @Override
    public Screen create(Screen parent) {
        return new SpawnProofHelperModScreen(parent);
    }
}
