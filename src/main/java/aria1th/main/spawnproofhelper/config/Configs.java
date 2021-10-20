package aria1th.main.spawnproofhelper.config;

import aria1th.main.spawnproofhelper.IOption;
import aria1th.main.spawnproofhelper.utils.SpawnProofLocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.GameOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Configs extends GameOptions {
    public static Configs instance;
    private final File configFile = new File(new File(MinecraftClient.getInstance().runDirectory, "config"), "spawnproofhelper");
    public CyclingOption<Boolean>[] allBooleanConfigs;

    private boolean spawnproofon = false;


    public Configs() throws IOException {
        super(MinecraftClient.getInstance(), new File(new File(MinecraftClient.getInstance().runDirectory, "config"), "spawnproofhelper"));
        instance = this;
        allBooleanConfigs = new CyclingOption[]{
                Config.SPAWNPROOF_ON
        };
        loadFromFile();
    }

    private void loadFromFile() throws IOException {
        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            Scanner reader = new Scanner(configFile);
            while (reader.hasNextLine()) {
                String[] configWord;
                String line = reader.nextLine();
                configWord = line.split(" ");
                if (configWord.length > 1) {
                    if ("spawnproofOn".equals(configWord[0])) {
                        spawnproofon = (configWord[1].equals("true"));
                    }
                    System.out.println("spawnproofHelper : Loaded " + configWord[0] + " as " + configWord[1]);
                } else {
                    System.out.println("spawnproofHelper : The config file is invalid");
                }
            }
        } else {
            System.out.println("spawnproofHelper : Couldn't find config file, or the file is invalid");
        }
        saveToFile();
    }

    public void saveToFile() throws IOException {
        configFile.delete();
        FileWriter fw = new FileWriter(configFile);
        for (CyclingOption<Boolean> config : allBooleanConfigs) {
            String configKey = ((IOption)config).getKey();
            if ("spawnproofOn".equals(configKey)) {
                fw.write(((IOption) config).getKey() + " " + Configs.getSpawnProofOn() + "\n");
            }
        }
        fw.close();
    }

    public static Configs getInstance() {
        return instance;
    }

    public static void setAll(boolean value){
        Configs.setSpawnProofOn(value);

    }

    public static void setSpawnProofOn(boolean value) {
        Configs.getInstance().spawnproofon = value;
        SpawnProofLocation.refreshInstance();
    }
    public static boolean getSpawnProofOn() {
        return Configs.getInstance().spawnproofon;
    }


}