/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.marx_software.lucene.index;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.apache.lucene.document.Document;

/**
 *
 * @author t.marx
 */
public class ShardSizeShardingStrategy implements ShardingStrategy {

	private final MessageDigest instance;

	private List<Shard> shards;
	
	public ShardSizeShardingStrategy() {
		try {
			this.instance = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Shard select(Document document) {

		String id = document.get("_id");

		return getShard(id);
	}

	@Override
	public long getShard(String key) {

		String id = key;

		instance.reset();
		instance.update(id.getBytes());
		byte[] digest = instance.digest();

		// padding & clip
		long h = 0;
		for (int i = 0; i < 4; i++) {
			h <<= 8;
			h |= ((int) digest[i]) & 0xFF;
		}
		return h;
	}
	
}
