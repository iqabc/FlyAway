package com.example.examplemod;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@Mod(modid = ExampleMod.MODID,
        name = ExampleMod.MOD_NAME, version = ExampleMod.VERSION)
@SideOnly(Side.CLIENT)
public class ExampleMod
{
    public static final String MODID = "examplemod";
    public static final String MOD_NAME = "Iqabc";
    public static final String VERSION = "1.0";
    public static final KeyBinding key_S = new KeyBinding(Keyboard.getKeyName(Keyboard.KEY_S),Keyboard.KEY_S, "category");
    public static final KeyBinding key_W = new KeyBinding(Keyboard.getKeyName(Keyboard.KEY_W),Keyboard.KEY_W, "category");
    public static final KeyBinding key_A = new KeyBinding(Keyboard.getKeyName(Keyboard.KEY_A),Keyboard.KEY_A, "category");
    public static final KeyBinding key_D = new KeyBinding(Keyboard.getKeyName(Keyboard.KEY_D),Keyboard.KEY_D, "category");

    //MyMods!
    public static Item hookShot;

    @Mod.Instance(MODID)
    public static ExampleMod INSTANCE;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        //-----hookShot-----//
        hookShot = new HookShot();
        //sample.setRegistryName(MOD_ID,"myFood");
        GameRegistry.findRegistry(Item.class).register(hookShot);
        if (event.getSide().isClient()) {
            ModelLoader.setCustomModelResourceLocation(hookShot,0,new ModelResourceLocation(MODID + ":" + "bow","inventory"));
        }
        //-----hookShot-----//
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        //----------hookShot----------//
        GameRegistry.addShapedRecipe(new ResourceLocation("bow"), new ResourceLocation("recipes"), new ItemStack(hookShot, 1), new Object[]{"P  "," A ","  B", 'P', Item.getItemById(257),'A', Item.getItemById(262),'B', Item.getItemById(261)});
        //----------hookShot----------//

        //----------keyEvent----------//
        if (event.getSide() == Side.CLIENT)
        {
            ClientRegistry.registerKeyBinding(key_S);
            ClientRegistry.registerKeyBinding(key_W);
            ClientRegistry.registerKeyBinding(key_S);
            ClientRegistry.registerKeyBinding(key_D);
        }
        FMLCommonHandler.instance().bus().register(hookShot);
        //----------keyEvent----------//
    }
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
    }
    @GameRegistry.ObjectHolder(MODID)
    public static class Blocks {
      /*
          public static final MySpecialBlock mySpecialBlock = null; // placeholder for special block below
      */
    }
    @GameRegistry.ObjectHolder(MODID)
    public static class Items {
      /*
          public static final ItemBlock mySpecialBlock = null; // itemblock for the block above
          public static final MySpecialItem mySpecialItem = null; // placeholder for special item below
      */
    }
    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {
        /**
         * Listen for the register event for creating custom items
         */
        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
            //event.getRegistry().register(sample.setRegistryName(MOD_ID,"myFood"));
           /*
             event.getRegistry().register(new ItemBlock(Blocks.myBlock).setRegistryName(MOD_ID, "myBlock"));
             event.getRegistry().register(new MySpecialItem().setRegistryName(MOD_ID, "mySpecialItem"));
            */
        }

        /**
         * Listen for the register event for creating custom blocks
         */
        @SubscribeEvent
        public static void addBlocks(RegistryEvent.Register<Block> event) {
           /*
             event.getRegistry().register(new MySpecialBlock().setRegistryName(MOD_ID, "mySpecialBlock"));
            */
        }
    }
}
