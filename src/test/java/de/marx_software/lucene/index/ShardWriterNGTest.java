/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/EmptyTestNGTest.java to edit this template
 */
package de.marx_software.lucene.index;

import java.util.UUID;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author t.marx
 */
public class ShardWriterNGTest {
	
	public ShardWriterNGTest() {
	}

	@Test
	public void testSomeMethod() {
		var shardWriter = new ShardedIndex(new IDHashShardingStrategy());
		shardWriter.addShard("eins");
		shardWriter.addShard("zwei");
		shardWriter.addShard("drei");
		
		System.out.println(shardWriter.getShard(document("thorsten")));
		System.out.println(shardWriter.getShard(document("thorsten")));
		System.out.println(shardWriter.getShard(document(UUID.randomUUID().toString())));
		System.out.println(shardWriter.getShard(document(UUID.randomUUID().toString())));
		System.out.println(shardWriter.getShard(document(UUID.randomUUID().toString())));
		System.out.println(shardWriter.getShard(document(UUID.randomUUID().toString())));
		System.out.println(shardWriter.getShard(document(UUID.randomUUID().toString())));
		System.out.println(shardWriter.getShard(document(UUID.randomUUID().toString())));
		System.out.println(shardWriter.getShard(document(UUID.randomUUID().toString())));
		System.out.println(shardWriter.getShard(document(UUID.randomUUID().toString())));
		System.out.println(shardWriter.getShard(document(UUID.randomUUID().toString())));
		System.out.println(shardWriter.getShard(document(UUID.randomUUID().toString())));
		System.out.println(shardWriter.getShard(document(UUID.randomUUID().toString())));
		System.out.println(shardWriter.getShard(document(UUID.randomUUID().toString())));
		System.out.println(shardWriter.getShard(document(UUID.randomUUID().toString())));
	}
	
	private Document document(final String id) {
		var doc = new Document();
		doc.add(new StringField("_id", id, Field.Store.YES));
		return doc;
	}
}
