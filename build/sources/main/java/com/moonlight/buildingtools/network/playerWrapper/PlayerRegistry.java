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

public class PlayerRegistry {
	
	Map<String, PlayerWrapper> registry;
	
	private PlayerRegistryProvider provider;
	
	
	public PlayerRegistry(PlayerRegistryProvider provider){
		this.provider = provider;
	}
	
	public void init(){
		this.registry = new MapMaker().weakKeys().makeMap();
	}
	
	protected void destroy()
    {
        this.registry = null;
    }
	
	public Optional<PlayerWrapper> getPlayer(String name)
    {
        return this.get(name);
    }
	
	public Optional<PlayerWrapper> getPlayer(EntityPlayer player)
    {
		//System.out.println("GettingPlayer:  " + player);
        return this.get(player.getName());
    }
	
	public Iterable<PlayerWrapper> getPlayers()
    {
        List<PlayerWrapper> players = Lists.newArrayList();
        for (Map.Entry<String, PlayerWrapper> e : this.getRegisteredValues())
        {
            players.add(e.getValue());
        }
        return players;
    }
	
	public void invalidate(String name)
    {
        this.remove(name);
    }	
	
	
	public void register(String key, PlayerWrapper value)
    {
        checkNotNull(key);
        checkNotNull(value);
        this.registry.put(key, value);
    }
	
	public Optional<PlayerWrapper> getValueFromKey(String key)
    {
        checkNotNull(key);
        return Optional.fromNullable(this.registry.get(key));
    }
	
	public Optional<String> getNameForValue(PlayerWrapper value)
    {
		String key = null;
        for (Map.Entry<String, PlayerWrapper> entry : this.registry.entrySet())
        {
            if (entry.getValue() == value)
            {
                key = entry.getKey();
                break;
            }
        }
        if (key != null)
        {
        	return Optional.of(key);
        }
        return Optional.absent();
    }
	
	public Set<Entry<String, PlayerWrapper>> getRegisteredValues()
    {
		if(this.registry != null)
			return this.registry.entrySet();
		else{
			this.init();
			return this.registry.entrySet();
		}
    }
	
	public void remove(String key)
    {
        this.registry.remove(key);
    }
	
	
	
	public Optional<PlayerWrapper> get(String name)
    {
        Optional<PlayerWrapper> value = this.getValueFromKey(name);
        if (!value.isPresent())
        {
            Optional<Pair<String, PlayerWrapper>> v = this.provider.get(name);
            if (v.isPresent())
            {
                register(v.get().getKey(), v.get().getValue());
                return Optional.of(v.get().getValue());
            }
            return Optional.absent();
        }
        return value;
    }

}
