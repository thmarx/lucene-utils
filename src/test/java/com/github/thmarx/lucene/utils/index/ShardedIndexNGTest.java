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
import org.apache.lucene.search.TopDocs;
import org.testng.annotations.Test;

/**
 *
 * @author t.marx
 */
public class ShardedIndexNGTest {

	public ShardedIndexNGTest() {
	}

	@Test
	public void testSomeMethod() throws IOException {

		var config = ShardedIndexConfiguration.builder()
				.numberOfShards(5)
				.strategy(new ShardSizeShardingStrategy())
				.shardsDirectory(Path.of("target/index-" + System.currentTimeMillis()))
				.shardConfiguration(ShardConfiguration.builder()
						.analyzer(new StandardAnalyzer())
						.build()
				)
				.build();

		var shardedIndex = new ShardedIndex(config);
		shardedIndex.open();
		try {
			for (int i = 0; i < 10; i++) {
				Document doc = document(Map.of(
						"id", "" + i
				));
				shardedIndex.addDocument(doc);
			}

			shardedIndex.getSearcherManager().maybeRefreshBlocking();
			
			IndexSearcher searcher = shardedIndex.getSearcherManager().acquire();
			try {
				int count = searcher.count(new MatchAllDocsQuery());
				System.out.println(count);
			} finally {
				shardedIndex.getSearcherManager().release(searcher);
			}
		} finally {
			shardedIndex.close();
		}
	}
	
	@Test
	public void test_Get_document() throws IOException {

		var config = ShardedIndexConfiguration.builder()
				.numberOfShards(5)
				.strategy(new ShardSizeShardingStrategy())
				.shardsDirectory(Path.of("target/index-" + System.currentTimeMillis()))
				.shardConfiguration(ShardConfiguration.builder()
						.analyzer(new StandardAnalyzer())
						.build()
				)
				.build();

		var shardedIndex = new ShardedIndex(config);
		shardedIndex.open();
		try {
			for (int i = 0; i < 10; i++) {
				Document doc = document(Map.of(
						"id", "" + i
				));
				shardedIndex.addDocument(doc);
			}

			shardedIndex.getSearcherManager().maybeRefreshBlocking();
			
			IndexSearcher searcher = shardedIndex.getSearcherManager().acquire();
			try {
				
				TopDocs topDocs = searcher.search(new MatchAllDocsQuery(), 10);
				
				for (var scoreDoc : topDocs.scoreDocs) {
					var document = searcher.getIndexReader().storedFields().document(scoreDoc.doc);
					System.out.println("document: " + document);
				}
			} finally {
				shardedIndex.getSearcherManager().release(searcher);
			}
		} finally {
			shardedIndex.close();
		}
	}

	private Document document(final Map<String, String> fields) {
		var doc = new Document();
		fields.forEach((name, value) -> {
			doc.add(new StringField(name, value, Field.Store.YES));
		});
		return doc;
	}
}
