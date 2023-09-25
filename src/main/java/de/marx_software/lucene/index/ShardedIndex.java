/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.marx_software.lucene.index;

import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.lucene.document.Document;

/**
 *
 * @author t.marx
 */
public class ShardedIndex {
	
	private final SortedMap<Long, Shard> shards = new TreeMap<>();
	
	
	
	final ShardingStrategy strategy;
	
	public ShardedIndex (final ShardingStrategy strategy) {
		this.strategy = strategy;
	}
	
	public void addShard (final Shard shard) {
		var value = strategy.getShard(shard.getName());
		shards.put(value, shard);
	}
	
	public Shard getShard (final Document document) {
		long hashed = strategy.getShard(document, );
		SortedMap<Long, Shard> tailMap = shards.tailMap(hashed);
        long hashVal = tailMap.isEmpty() ? shards.firstKey() : tailMap.firstKey();
        return shards.get(hashVal);
	}
}
