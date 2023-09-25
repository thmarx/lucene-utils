/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/EmptyTestNGTest.java to edit this template
 */
package com.github.thmarx.lucene.utils.search;

import com.github.thmarx.lucene.utils.search.MultiSearcherManager;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.store.FSDirectory;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author t.marx
 */
public class MultiSearcherManagerNGTest {
	
	
	@Test
	public void testSomeMethod() throws Exception {
		
		var index1 = Path.of("target/index1");
		var index2 = Path.of("target/index2");
		
		Files.createDirectories(index1);
		Files.createDirectories(index2);
		
		var dir1 = FSDirectory.open(index1);
		var dir2 = FSDirectory.open(index2);
		
		var conf1 = new IndexWriterConfig(new StandardAnalyzer());
		conf1.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		var writer1 = new IndexWriter(dir1, conf1);
		
		var conf2 = new IndexWriterConfig(new StandardAnalyzer());
		conf2.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		var writer2 = new IndexWriter(dir2, conf2);
		
		Document doc = new Document();
		doc.add(new StringField("name", "thorsten", Field.Store.YES));
		
		writer1.addDocument(doc);
		writer1.commit();
		writer2.addDocument(doc);
		writer2.commit();
		
		List<IndexReader> readers = List.of(
				DirectoryReader.open(dir1),
				DirectoryReader.open(dir2)
		);
		MultiSearcherManager sm = new MultiSearcherManager(readers.toArray(IndexReader[]::new));
		
		IndexSearcher searcher = sm.acquire();
		try {
			int count = searcher.count(new MatchAllDocsQuery());
			System.out.println(count);
		} finally {
			sm.release(searcher);
		}
		
		writer2.addDocument(doc);
		writer2.commit();
		
		sm.maybeRefreshBlocking();
		
		searcher = sm.acquire();
		try {
			int count = searcher.count(new MatchAllDocsQuery());
			System.out.println(count);
		} finally {
			sm.release(searcher);
		}
	}
	
}
