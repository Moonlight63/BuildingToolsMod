package com.moonlight.buildingtools.network.playerWrapper;

import com.google.common.base.Optional;
import com.moonlight.buildingtools.utils.Pair;

public interface PlayerRegistryProvider {

	Optional<Pair<String, PlayerWrapper>> get(String name);

}
