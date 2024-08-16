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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NRTCachingDirectory;

/**
 *
 * @author t.marx
 */
@RequiredArgsConstructor
public class Shard implements AutoCloseable {
	@Getter
	private final String name;
	
	private Directory directory;
	private IndexWriter indexWriter;
	private NRTCachingDirectory cachedFSDir;
	
	public int size () {
		return indexWriter.getDocStats().numDocs;
	}
	
	protected Directory getDirectory () {
		return cachedFSDir;
	}
	protected IndexWriter getIndexWriter () {
		return indexWriter;
	}
	
	public void open (final ShardConfiguration configuration, final Path path) throws IOException {
		directory = FSDirectory.open(path);
		cachedFSDir = new NRTCachingDirectory(this.directory, 5.0, 60.0);
		IndexWriterConfig config = new IndexWriterConfig(configuration.getAnalyzer());
		config.setOpenMode(config.getOpenMode());
		
		indexWriter = new IndexWriter(cachedFSDir, config);
	}
	
	public void addDocument (Iterable<? extends IndexableField> document) throws IOException {
		indexWriter.addDocument(document);
	}
	public void addDocuments (Iterable<? extends Iterable<? extends IndexableField>> documents) throws IOException {
		indexWriter.addDocuments(documents);
	}
	
	public void deleteDocuments (Query... queries) throws IOException {
		indexWriter.deleteDocuments(queries);
	}
	public void deleteDocuments (Term... terms) throws IOException {
		indexWriter.deleteDocuments(terms);
	}
	
	@Override
	public void close () throws IOException {
		indexWriter.commit();
		indexWriter.close();
		directory.close();
	}
}
