package com.moonlight.buildingtools.network.playerWrapper;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.moonlight.buildingtools.utils.Pair;

import net.minecraft.entity.player.EntityPlayer;
import static com.google.common.base.Preconditions.checkNotNull;

public class PlayerRegistry_Backup {
	
	Map<EntityPlayer, PlayerWrapper> registry;
	Map<String, EntityPlayer> nameRegistry;
	
	private PlayerRegistryProvider provider;
	
	
	public PlayerRegistry_Backup(PlayerRegistryProvider provider){
		this.provider = provider;
	}
	
	public void init(){
		this.registry = new MapMaker().weakKeys().makeMap();
        this.nameRegistry = new MapMaker().weakValues().makeMap();
	}
	
	protected void destroy()
    {
        this.registry = null;
        this.nameRegistry = null;
    }
	
	public Optional<PlayerWrapper> getPlayer(String name)
    {
        return this.get(name);
    }
	
	public Optional<PlayerWrapper> getPlayer(EntityPlayer player)
    {
		//System.out.println("GettingPlayer:  " + player);
        return this.getValueFromKey(player);
    }
	
	public Iterable<PlayerWrapper> getPlayers()
    {
        List<PlayerWrapper> players = Lists.newArrayList();
        for (Map.Entry<EntityPlayer, PlayerWrapper> e : this.getRegisteredValues())
        {
            players.add(e.getValue());
        }
        return players;
    }
	
	public void invalidate(String name)
    {
        this.remove(name);
    }
	
	public void invalidate(EntityPlayer player)
    {
        this.remove(player);
    }
	
	
	
	public void register(String name, EntityPlayer key, PlayerWrapper value)
    {
        checkNotNull(key);
        checkNotNull(value);
        this.registry.put(key, value);
        if (name != null)
        {
            this.nameRegistry.put(name, key);
        }
    }
	
	public Optional<PlayerWrapper> getValueFromKey(EntityPlayer key)
    {
        checkNotNull(key);
        return Optional.fromNullable(this.registry.get(key));
    }
	
	public Optional<PlayerWrapper> getValueFromName(String name)
    {
        if (!this.nameRegistry.containsKey(name))
        {
            return Optional.absent();
        }
        EntityPlayer key = this.nameRegistry.get(name);
        if (!this.registry.containsKey(key))
        {
            this.nameRegistry.remove(name);
            return Optional.absent();
        }
        return Optional.of(this.registry.get(key));
    }
	
	public Optional<String> getNameForValue(PlayerWrapper value)
    {
		EntityPlayer key = null;
        for (Map.Entry<EntityPlayer, PlayerWrapper> entry : this.registry.entrySet())
        {
            if (entry.getValue() == value)
            {
                key = entry.getKey();
                break;
            }
        }
        if (key != null)
        {
            for (Map.Entry<String, EntityPlayer> entry : this.nameRegistry.entrySet())
            {
                if (entry.getValue() == key)
                {
                    return Optional.of(entry.getKey());
                }
            }
        }
        return Optional.absent();
    }
	
	public Iterable<String> getRegisteredNames()
    {
        return this.nameRegistry.keySet();
    }
	
	public Set<Entry<EntityPlayer, PlayerWrapper>> getRegisteredValues()
    {
		if(this.registry != null)
			return this.registry.entrySet();
		else{
			this.init();
			return this.registry.entrySet();
		}
    }
	
	public void remove(String name)
    {
        this.nameRegistry.remove(name);
    }
	
	public void remove(EntityPlayer key)
    {
        this.registry.remove(key);
    }
	
	
	
	public Optional<PlayerWrapper> get(String name)
    {
        Optional<PlayerWrapper> value = this.getValueFromName(name);
        if (!value.isPresent())
        {
            Optional<Pair<EntityPlayer, PlayerWrapper>> v = this.provider.get(name);
            if (v.isPresent())
            {
                register(name, v.get().getKey(), v.get().getValue());
                return Optional.of(v.get().getValue());
            }
            return Optional.absent();
        }
        return value;
    }

}
