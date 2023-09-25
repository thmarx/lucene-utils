/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.github.thmarx.lucene.utils.index;

import java.util.List;
import org.apache.lucene.document.Document;

/**
 *
 * @author t.marx
 */
public interface ShardingStrategy {
	public Shard select(List<Shard> shards);
}
