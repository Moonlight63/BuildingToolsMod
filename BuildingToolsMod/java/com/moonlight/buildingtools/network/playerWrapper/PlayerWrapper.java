package com.moonlight.buildingtools.network.playerWrapper;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.ref.WeakReference;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

import com.google.common.base.Optional;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

public class PlayerWrapper {
	
	private static Queue<BlockChangeBase> pendingChanges = new LinkedList<BlockChangeBase>();
	
	public BlockChangeQueue pendingChangeQueue = null;
	
	public Set<ChangeBlockToThis> currentCopyClipboard = new LinkedHashSet<ChangeBlockToThis>();
	
	public Set<Entity> currentClipboardEntities = new LinkedHashSet<Entity>();
	
	public BlockPos clipboardMaxPos;
	
	private final WeakReference<EntityPlayer> reference;
	
	public Set<ChangeBlockToThis> tempUndoList = new LinkedHashSet<ChangeBlockToThis>();
	public Deque<Set<ChangeBlockToThis>> undolist = new LinkedList<Set<ChangeBlockToThis>>();
	
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
        return Optional.fromNullable(pendingChanges.peek());
    }
	
	public void addPending(BlockChangeBase queue)
    {
		//System.out.println("PlayerWrapper :  Adding Pending");
        checkNotNull(queue, "ChangeQueue cannot be null");
        pendingChanges.add(queue);
    }
	
	public void clearNextPending()
    {
        if (!pendingChanges.isEmpty() && pendingChanges.peek().isFinished())
        {
        	//System.out.println(pendingChanges);
            pendingChanges.remove();
        }
    }

}
