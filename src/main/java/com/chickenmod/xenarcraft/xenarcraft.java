package com.chickenmod.xenarcraft;

import com.chickenmod.xenarcraft.blocks.FirstBlock;
import com.chickenmod.xenarcraft.blocks.FirstBlockContainer;
import com.chickenmod.xenarcraft.blocks.FirstBlockTile;
import com.chickenmod.xenarcraft.blocks.ModBlocks;
import com.chickenmod.xenarcraft.items.FirstItem;
import com.chickenmod.xenarcraft.setup.ModSetup;
import com.chickenmod.xenarcraft.setup.ServerProxy;
import com.chickenmod.xenarcraft.setup.ClientProxy;
import com.chickenmod.xenarcraft.setup.IProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("xenarcraft")
public class xenarcraft {

    public static final String MODID = "xenarcraft";

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    public static ModSetup setup = new ModSetup();

    public xenarcraft() {
        // Register the com.example.examplemod.setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        setup.init();
        proxy.init();
    }


    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("xenarcraft", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlockRegistry(final RegistryEvent.Register<Block> event) {
            event.getRegistry().register(new FirstBlock());
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> event) {
            Item.Properties properties = new Item.Properties() //This registers the item properties and itemgroup
                .group(setup.itemGroup);
            event.getRegistry().register(new BlockItem(ModBlocks.block_master1, properties).setRegistryName("block_master1"));//This registers the block as in item
            event.getRegistry().register(new FirstItem());
        }

        // https://www.youtube.com/watch?v=GsV_pKkE1mo

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event){ //This registers the block as a tile entity
            event.getRegistry().register(TileEntityType.Builder.create(FirstBlockTile::new, ModBlocks.block_master1).build(null).setRegistryName("block_master1"));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event){
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new FirstBlockContainer(windowId, xenarcraft.proxy.getClientWorld(), pos, inv, xenarcraft.proxy.getClientPlayer());
            }).setRegistryName("block_master1"));
        }
    }
}
