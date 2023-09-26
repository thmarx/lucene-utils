/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/EmptyTestNGTest.java to edit this template
 */
package com.github.thmarx.lucene.utils.index;

/*-
 * #%L
 * lucene-utils
 * %%
 * Copyright (C) 2023 Marx-Software
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
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
