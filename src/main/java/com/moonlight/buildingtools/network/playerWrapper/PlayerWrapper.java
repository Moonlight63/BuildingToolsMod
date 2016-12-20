package com.moonlight.buildingtools.network.playerWrapper;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.ref.WeakReference;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.undoTool.BlockInfoContainer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class PlayerWrapper {
	
	private static Queue<BlockChangeBase> pendingChanges = new LinkedList<BlockChangeBase>();
	
	public Queue<BlockChangeQueue> pendingChangeQueue = new LinkedList<BlockChangeQueue>();
	
	public Set<BlockInfoContainer> currentCopyClipboard = new LinkedHashSet<BlockInfoContainer>();
	
	public Set<Entity> currentClipboardEntities = new LinkedHashSet<Entity>();
	
	public BlockPos clipboardMaxPos;
	
	private final WeakReference<EntityPlayer> reference;
	
	public List<BlockInfoContainer> tempUndoList = new LinkedList<BlockInfoContainer>();
	public Deque<List<BlockInfoContainer>> undolist = new LinkedList<List<BlockInfoContainer>>();
	public boolean UndoIsSaved = false;
	
	public PlayerWrapper(EntityPlayer player){
		this.reference = new WeakReference<EntityPlayer>(player);
	}
	
	public EntityPlayer getThis()
    {
        return this.reference.get();
    }
	
	public String getName()
    {
        return getThis().getName();
    }
	
	public static void removeFromQueue(BlockChangeBase queue){
		pendingChanges.remove(queue);
	}
	
	public boolean hasPendingChanges()
    {
		//System.out.println(pendingChanges);
        return pendingChanges.size() != 0;
    }
	
	public Optional<BlockChangeBase> getNextPendingChange()
    {
        return Optional.ofNullable(pendingChanges.peek());
    }
	
	public void addPending(BlockChangeBase queue)
    {
		//System.out.println("PlayerWrapper :  Adding Pending");
        checkNotNull(queue, "ChangeQueue cannot be null");
        pendingChanges.add(queue);
    }
	
	public void clearNextPending(boolean force)
    {
        if (!pendingChanges.isEmpty() && (pendingChanges.peek().isFinished() || force))
            pendingChanges.remove();
    }

}
