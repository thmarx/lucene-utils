/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.thmarx.lucene.utils.index;

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
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

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
