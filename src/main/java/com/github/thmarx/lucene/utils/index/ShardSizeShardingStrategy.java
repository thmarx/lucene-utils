/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.thmarx.lucene.utils.index;

import java.util.List;
import org.apache.lucene.document.Document;

/**
 *
 * @author t.marx
 */
public class ShardSizeShardingStrategy implements ShardingStrategy {

	public ShardSizeShardingStrategy() {
		
	}

	@Override
	public Shard select(List<Shard> shards) {
		shards.sort((s1, s2) -> Integer.compare(s1.size(), s2.size()));
		
		return shards.getFirst();
	}
}
