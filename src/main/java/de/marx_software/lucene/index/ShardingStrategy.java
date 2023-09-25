/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.marx_software.lucene.index;

import java.util.List;
import org.apache.lucene.document.Document;

/**
 *
 * @author t.marx
 */
public interface ShardingStrategy {

	public Shard select(Document document, List<Shard> shards);
	
	public long getShard(String key);
}
