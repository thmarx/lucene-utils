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

import com.github.thmarx.lucene.utils.search.MultiSearcherManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

/**
 *
 * @author t.marx
 */
@RequiredArgsConstructor
public class ShardedIndex implements AutoCloseable {
	
	private final List<Shard> shards = new ArrayList<>();
	
	final ShardedIndexConfiguration configuration;
	
	private MultiSearcherManager searcherManager;
	
	public void open () throws IOException {
		
		List<IndexReader> indexReaders = new ArrayList<>();
		
		for (int i = 0; i < configuration.getNumberOfShards(); i++) {
			
			var shardName = "shard-" + (i+1);
			Path shardPath = configuration.getShardsDirectory().resolve(shardName);
			if (!Files.exists(shardPath)) {
				Files.createDirectories(shardPath);
			}
			
			Shard shard = new Shard("shard-" + (i+1));
			shard.open(configuration.getShardConfiguration(), shardPath);
			shards.add(shard);
			
			indexReaders.add(DirectoryReader.open(shard.getIndexWriter()));
		}
		searcherManager = new MultiSearcherManager(indexReaders.toArray(IndexReader[]::new));
	}
	
	public MultiSearcherManager getSearcherManager () {
		return searcherManager;
	}
	
	public void addDocument (Document document) throws IOException {
		getShard().addDocument(document);
	}
	public void addDocuments (Iterable<Document> documents) throws IOException {
		getShard().addDocuments(documents);
	}
	
	public void deleteDocuments (Query... queries) throws IOException {
		shards.forEach(shard -> {
			try {
				shard.deleteDocuments(queries);
			} catch (IOException ex) {
				Logger.getLogger(ShardedIndex.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
	}
	public void deleteDocuments (Term... terms) throws IOException {
		shards.forEach(shard -> {
			try {
				shard.deleteDocuments(terms);
			} catch (IOException ex) {
				Logger.getLogger(ShardedIndex.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
	}
	
	
	
	@Override
	public void close () throws IOException {
		searcherManager.close();
		shards.forEach(shard -> {
			try {
				shard.close();
			} catch (IOException ex) {
				Logger.getLogger(ShardedIndex.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
	}
	
	public Shard getShard () {
		var shard = configuration.getStrategy().select(shards);
        return shard;
	}
}
