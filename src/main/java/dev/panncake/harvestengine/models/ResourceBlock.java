package dev.panncake.harvestengine.models;

import java.util.List;

public record ResourceBlock(String id, double hp, List<String> whitelist, List<LootEntry> loots, int xp) {}
