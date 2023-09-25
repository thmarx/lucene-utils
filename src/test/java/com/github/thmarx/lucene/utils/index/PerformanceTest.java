/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/EmptyTestNGTest.java to edit this template
 */
package com.github.thmarx.lucene.utils.index;

import com.github.thmarx.lucene.utils.index.ShardedIndex;
import com.github.thmarx.lucene.utils.index.ShardConfiguration;
import com.github.thmarx.lucene.utils.index.ShardSizeShardingStrategy;
import com.github.thmarx.lucene.utils.index.ShardedIndexConfiguration;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author t.marx
 */
public class PerformanceTest {

	private ShardedIndex shardedIndex;

	@BeforeClass
	void setup() throws IOException {
		var config = ShardedIndexConfiguration.builder()
				.numberOfShards(5)
				.strategy(new ShardSizeShardingStrategy())
				.shardsDirectory(Path.of("target/index-" + System.currentTimeMillis()))
				.shardConfiguration(ShardConfiguration.builder()
						.analyzer(new StandardAnalyzer())
						.build()
				)
				.build();

		shardedIndex = new ShardedIndex(config);
		shardedIndex.open();
	}

	@AfterClass
	void shutdown() throws IOException {
		shardedIndex.close();
	}

	@Test
	public void test_performance() throws IOException {

		
		var monitor = new Monitor();
		
		monitor.reset();
		for (int i = 0; i < 10000000; i++) {
			Document doc = document(Map.of(
					"id", "" + i,
					"name", "name " + i
			));
			shardedIndex.addDocument(doc);
		}
		monitor.print("indexing");

		monitor.reset();
		shardedIndex.getSearcherManager().maybeRefreshBlocking();
		monitor.print("mayberefresh");

		IndexSearcher searcher = shardedIndex.getSearcherManager().acquire();
		try {
			monitor.reset();
			int count = searcher.count(new MatchAllDocsQuery());
			monitor.print("count");
			System.out.println(count);
		} finally {
			shardedIndex.getSearcherManager().release(searcher);
		}

	}

	private Document document(final Map<String, String> fields) {
		var doc = new Document();
		fields.forEach((name, value) -> {
			doc.add(new StringField(name, value, Field.Store.YES));
		});
		return doc;
	}
	
	class Monitor {
		long start = System.currentTimeMillis();
		
		public void reset () {
			start = System.currentTimeMillis();
		}
		
		public void print(final String name) {
			System.out.printf("%s = %d \r\n", name, (System.currentTimeMillis() - start));
		}
	}
}
