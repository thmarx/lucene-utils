/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/EmptyTestNGTest.java to edit this template
 */
package de.marx_software.lucene.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author t.marx
 */
public class IDHashShardingStrategyNGTest {
	
	public IDHashShardingStrategyNGTest() {
	}

	@Test
	public void testSomeMethod() {
		
		IDHashShardingStrategy strategy = new IDHashShardingStrategy();
		
		var doc = document("eins");
		long shard = strategy.getShard(doc);
		System.out.println(shard);
	}
	
	private Document document(final String id) {
		var doc = new Document();
		doc.add(new StringField("_id", id, Field.Store.YES));
		return doc;
	}
}
