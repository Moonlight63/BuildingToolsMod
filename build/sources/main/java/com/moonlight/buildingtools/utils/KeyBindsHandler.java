package com.moonlight.buildingtools.utils;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KeyBindsHandler
{
    public static ETKeyBinding keyToolIncrease = new ETKeyBinding("Increase Tool Radius", Keyboard.KEY_PRIOR, "key.buildingTools.tools", Key.KeyCode.TOOL_INCREASE);
    public static ETKeyBinding keyToolDecrease = new ETKeyBinding("Decrease Tool Radius", Keyboard.KEY_NEXT, "key.buildingTools.tools", Key.KeyCode.TOOL_DECREASE);
    public static ETKeyBinding[] keyBindings = new ETKeyBinding[] {keyToolIncrease, keyToolDecrease};
    public static Map<String, Key.KeyCode> keyCodeMap = new HashMap<String, Key.KeyCode>();

    public static void init()
    {
        for (ETKeyBinding keyBinding : keyBindings)
        {
            ClientRegistry.registerKeyBinding(keyBinding.getMinecraftKeyBinding());
            keyCodeMap.put(keyBinding.getMinecraftKeyBinding().getKeyDescription(), keyBinding.getKeyCode());
        }
    }

    public static Key.KeyCode whichKeyPressed()
    {
        for (ETKeyBinding keyBinding : keyBindings)
        {
            if (keyBinding.getMinecraftKeyBinding().isKeyDown())
            {
                return keyCodeMap.get(keyBinding.getMinecraftKeyBinding().getKeyDescription());
            }
        }

        return Key.KeyCode.UNKNOWN;
    }

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event)
    {
        //LogHelper.info("KeyDown");
        if (FMLClientHandler.instance().getClient().inGameHasFocus)
        {
            //LogHelper.info("End&Focus");
            EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
            if (player != null)
            {
                //LogHelper.info("PlayerNotNull");
                ItemStack equippedItem = player.getHeldItemMainhand();
                
                //Key.KeyCode keyCode = whichKeyPressed();
                //((BlockChangerBase)player.getCurrentEquippedItem().getItem()).handleKey(player, equippedItem, keyCode);

                if (equippedItem != null && equippedItem.getItem() instanceof IKeyHandler)
                {
                	Key.KeyCode keyCode = whichKeyPressed();
                	Set<Key.KeyCode> handledKeyCodes = ((IKeyHandler) equippedItem.getItem()).getHandledKeys();
                	if (!handledKeyCodes.contains(keyCode)) return;
                	((IKeyHandler) player.getHeldItemMainhand().getItem()).handleKey(player, equippedItem, keyCode);

                    //((BlockChangerBase)equippedItem.getItem()).setTargetRadius(equippedItem, radius);
                	
                    //Key.KeyCode keyCode = whichKeyPressed();
                    //Set<Key.KeyCode> handledKeyCodes = ((IKeyHandler) equippedItem.getItem()).getHandledKeys();

                    //if (!handledKeyCodes.contains(keyCode)) return;

                    //if (player.worldObj.isRemote)
                    //{
                    //	System.out.print("Client sent " + keyCode.toString() + " to the server");
                        //LogHelper.debug("Remote, sent " + keyCode.toString() + " to server");
                        //new PacketKeyPressed().sendKeyPressedPacket(keyCode);
                    //    ((IKeyHandler) player.getCurrentEquippedItem().getItem()).handleKey(player, equippedItem, keyCode);
                    //} else
                    //{
                        //LogHelper.debug("Client, handling key press: " + keyCode.toString());
                        //((BlockChangerBase) player.getCurrentEquippedItem().getItem()).handleKey(player, equippedItem, keyCode);
                    //}
                }
            }
        }
    }

    private static class ETKeyBinding
    {
        private KeyBinding keyBinding;
        private Key.KeyCode keyCode;

        public ETKeyBinding(String description, int keyboardCode, String category, Key.KeyCode keyCode)
        {
            this.keyBinding = new KeyBinding(description, keyboardCode, category);
            this.keyCode = keyCode;
        }

        public Key.KeyCode getKeyCode()
        {
            return this.keyCode;
        }

        public KeyBinding getMinecraftKeyBinding()
        {
            return this.keyBinding;
        }
    }
}
