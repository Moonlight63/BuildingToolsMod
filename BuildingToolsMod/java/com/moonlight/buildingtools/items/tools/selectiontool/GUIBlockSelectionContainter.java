package com.moonlight.buildingtools.items.tools.selectiontool;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendGuiButtonPressedToItemMessage;
import com.moonlight.buildingtools.network.packethandleing.SendSimpleFillPacketToItemMessage;

@SideOnly(Side.CLIENT)
public class GUIBlockSelectionContainter extends GuiContainer{
	
    /** The location of the creative inventory tabs texture */
    private static final ResourceLocation creativeInventoryTabs = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    /** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
    private float currentScroll;
    /** True if the scrollbar is being dragged */
    private boolean isScrolling;
    /** True if the left mouse button was held down last time drawScreen was called. */
    private boolean wasClicking;
    private GuiTextField searchField;
    private boolean keyOrButtonClicked;
//    private ContainerBlockSelMenu container;
    private GuiCheckBox showMetaData = new GuiCheckBox(0, 0, 0, "Meta Data?", false);
    private GuiButton confirmFill = new GuiButton(1, guiLeft, guiTop, "Fill");
    
    public static List<ItemStack> blockList = Lists.<ItemStack>newArrayList();
    public static List<ItemStack> blockListMeta = Lists.<ItemStack>newArrayList();
    
    
    private int currID;
    private int currDATA;

    public GUIBlockSelectionContainter(EntityPlayer player){
        super(new ContainerBlockSelMenu());
        player.openContainer = this.inventorySlots;
//        container = (ContainerBlockSelMenu) this.inventorySlots;
        this.allowUserInput = true;
        this.ySize = 136;
        this.xSize = 195;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen(){
        //this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     * Click Type 1 = Shift Click
     * Click Type 2 = Hotbar key
     * Click Type 3 = Pick Block
     * Click Type 4 = Drop Key, Click outside of GUI
     * Click Type 5 =
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType){
        this.keyOrButtonClicked = true;
        boolean flag = clickType == 1;
        clickType = slotId == -999 && clickType == 0 ? 4 : clickType;
        
        //if(slotIn != null && slotIn.getStack() != null)
        	System.out.println("Button = " + clickedButton + "     Type = " + clickType);
        	//System.out.println(slotIn.getStack().getDisplayName());
        
        if(clickedButton == 0){
        	if(clickType == 0){
        		currID = Block.getIdFromBlock(Block.getBlockFromItem(slotIn.getStack().getItem()));
        		currDATA = slotIn.getStack().getItem().getMetadata(slotIn.getStack());
        		PacketDispatcher.sendToServer(new SendSimpleFillPacketToItemMessage(currID, currDATA));
        		this.mc.thePlayer.closeScreen();
        	}
        	else if(clickType == 1){
        		
        	}
        }
        else if(clickedButton == 1){
        	if(clickType == 0){
        		
        	}
        	else if(clickType == 1){
        		
        	}
        }

    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui(){
        super.initGui();
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.searchField = new GuiTextField(0, this.fontRendererObj, this.guiLeft + 81, this.guiTop + 6, 89, this.fontRendererObj.FONT_HEIGHT);
        this.searchField.setMaxStringLength(15);
        this.searchField.setEnableBackgroundDrawing(false);
        this.searchField.setVisible(true);
        this.searchField.setCanLoseFocus(true);
        this.searchField.setFocused(true);
        this.searchField.setText("");
        this.searchField.setTextColor(16777215);
        
        showMetaData.xPosition = this.guiLeft + 8;
        showMetaData.yPosition = this.guiTop + 4;
        this.buttonList.add(showMetaData);
        this.buttonList.add(confirmFill);
        
        for(Block b : GameData.getBlockRegistry()){
    		if(Item.getItemFromBlock(b) != null/* && Item.getItemFromBlock(b).getCreativeTab() != null*/)
    			blockList.add(new ItemStack(b));
    	}
        for(Block b : GameData.getBlockRegistry()){
    		if(Item.getItemFromBlock(b) != null/* && Item.getItemFromBlock(b).getCreativeTab() != null*/)
    			Item.getItemFromBlock(b).getSubItems(Item.getItemFromBlock(b), null, blockListMeta);
    	}
        
        this.updateCreativeSearch();        
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed(){
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException{
        if (!this.searchField.isFocused()){
            if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat)){
                this.searchField.setFocused(true);
            }
            else{
                super.keyTyped(typedChar, keyCode);
            }
        }
        else{
            if (this.keyOrButtonClicked){
                this.keyOrButtonClicked = false;
                this.searchField.setText("");
            }

            if (!this.checkHotbarKeys(keyCode)){
                if (this.searchField.textboxKeyTyped(typedChar, keyCode)){
                    this.updateCreativeSearch();
                }
                else{
                    super.keyTyped(typedChar, keyCode);
                }
            }
        }
    }

    private void updateCreativeSearch(){
    	
        ContainerBlockSelMenu.itemList.clear();
        
//    	if(showMetaData.isChecked()){
//            for(Block b : GameData.getBlockRegistry()){
//        		if(Item.getItemFromBlock(b) != null/* && Item.getItemFromBlock(b).getCreativeTab() != null*/)
//        			Item.getItemFromBlock(b).getSubItems(Item.getItemFromBlock(b), null, ContainerBlockSelMenu.itemList);
//        	}
//    	}
//    	else{
//    		for(Block b : GameData.getBlockRegistry()){
//	    		if(Item.getItemFromBlock(b) != null/* && Item.getItemFromBlock(b).getCreativeTab() != null*/)
//	    			ContainerBlockSelMenu.itemList.add(new ItemStack(b));
//	    	}
//    	}
        
        
        Iterator<ItemStack> iterator1 = showMetaData.isChecked() ? blockListMeta.iterator() : blockList.iterator();
        
        while (iterator1.hasNext()) {
			ContainerBlockSelMenu.itemList.add(iterator1.next());			
		}
    	
    	Iterator<ItemStack> iterator2 = ContainerBlockSelMenu.itemList.iterator();
        String s1 = this.searchField.getText().toLowerCase();

        while (iterator2.hasNext()){
            ItemStack itemstack = (ItemStack)iterator2.next();
            boolean flag = false;

            for (String s : itemstack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips)){
                if (EnumChatFormatting.getTextWithoutFormattingCodes(s).toLowerCase().contains(s1)){
                    flag = true;
                    break;
                }
            }

            if (!flag){
                iterator2.remove();
            }
        }

        this.currentScroll = 0.0F;
        ContainerBlockSelMenu.scrollTo(0.0F);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
//    {
//        CreativeTabs creativetabs = CreativeTabs.creativeTabArray[selectedTabIndex];
//
//        if (creativetabs != null && creativetabs.drawInForegroundOfTab())
//        {
//            GlStateManager.disableBlend();
//            this.fontRendererObj.drawString(I18n.format(creativetabs.getTranslatedTabLabel(), new Object[0]), 8, 6, 4210752);
//        }
//    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
        if (mouseButton == 0){
            int i = mouseX - this.guiLeft;
            int j = mouseY - this.guiTop;
            
//            if(i >= 82 && i <= 82+89 && j >= 5 && j <= 15){
//            	System.out.println("Clicked in search field");
//            }
        }
        
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    protected void mouseReleased(int mouseX, int mouseY, int state){
        if (state == 0){
            int i = mouseX - this.guiLeft;
            int j = mouseY - this.guiTop;

        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException{
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0){
            int j = ContainerBlockSelMenu.itemList.size() / 9 - 5;

            if (i > 0){
                i = 1;
            }

            if (i < 0){
                i = -1;
            }

            this.currentScroll = (float)((double)this.currentScroll - (double)i / (double)j);
            this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
            ContainerBlockSelMenu.scrollTo(this.currentScroll);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        boolean flag = Mouse.isButtonDown(0);
        int i = this.guiLeft;
        int j = this.guiTop;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 112;

        if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1){
            this.isScrolling = true;
        }

        if (!flag){
            this.isScrolling = false;
        }

        this.wasClicking = flag;

        if (this.isScrolling){
            this.currentScroll = ((float)(mouseY - l) - 7.5F) / ((float)(j1 - l) - 15.0F);
            this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
            ContainerBlockSelMenu.scrollTo(this.currentScroll);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
    }

    protected void renderToolTip(ItemStack stack, int x, int y){
            List<String> list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
            CreativeTabs creativetabs = stack.getItem().getCreativeTab();

            if (creativetabs != null){
                list.add(1, "" + EnumChatFormatting.BOLD + EnumChatFormatting.BLUE + I18n.format(creativetabs.getTranslatedTabLabel(), new Object[0]));
            }

            for (int i = 0; i < list.size(); ++i){
                if (i == 0){
                    list.set(i, stack.getRarity().rarityColor + (String)list.get(i));
                }
                else{
                    list.set(i, EnumChatFormatting.GRAY + (String)list.get(i));
                }
            }

            this.drawHoveringText(list, x, y);

            //super.renderToolTip(stack, x, y);
        
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();

        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_item_search.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.searchField.drawTextBox();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.guiLeft + 175;
        int j = this.guiTop + 18;
        int k = j + 112;
        this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
        this.drawTexturedModalRect(i, j + (int)((float)(k - j - 17) * this.currentScroll), 232, 0, 12, 15);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException{
        if (button.id == showMetaData.id){
        	this.updateCreativeSearch();
        }

        if (button.id == 1){
        }
    }


}