/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.thmarx.lucene.utils.search;

import java.io.IOException;
import java.util.stream.Stream;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherFactory;

public final class MultiSearcherManager extends ReferenceManager<IndexSearcher> {

	private final SearcherFactory searcherFactory;

	private final IndexReader[] indexReaders;

	public MultiSearcherManager(IndexReader[] indexReaders) throws IOException {
		this(indexReaders, null);
	}
			
	public MultiSearcherManager(IndexReader[] indexReaders, SearcherFactory searcherFactory)
			throws IOException {
		if (searcherFactory == null) {
			searcherFactory = new SearcherFactory();
		}
		this.indexReaders = indexReaders;
		this.searcherFactory = searcherFactory;
		this.current = getSearcher(searcherFactory, new MultiReader(indexReaders), null);
	}

	@Override
	protected void decRef(IndexSearcher reference) throws IOException {
		reference.getIndexReader().decRef();
	}

	@Override
	protected IndexSearcher refreshIfNeeded(IndexSearcher referenceToRefresh) throws IOException {
		final IndexReader prevReader = referenceToRefresh.getIndexReader();
		
		if (areReadersCurrent()) {
			return null;
		} else {
			for (int i = 0; i < indexReaders.length; i++) {
				if (!((DirectoryReader)indexReaders[i]).isCurrent()) {
					indexReaders[i] = DirectoryReader.openIfChanged((DirectoryReader)indexReaders[i]);
				}
			}
			return getSearcher(searcherFactory, new MultiReader(indexReaders), prevReader);
		}
	}

	private boolean areReadersCurrent() {
		return Stream.of(indexReaders).map(DirectoryReader.class::cast).noneMatch(dr -> {
			try {
				return !dr.isCurrent();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});
	}

	@Override
	protected boolean tryIncRef(IndexSearcher reference) {
		return reference.getIndexReader().tryIncRef();
	}

	@Override
	protected int getRefCount(IndexSearcher reference) {
		return reference.getIndexReader().getRefCount();
	}

	/**
	 * Returns <code>true</code> if no changes have occurred since this searcher ie. reader was opened, otherwise
	 * <code>false</code>.
	 *
	 * @see DirectoryReader#isCurrent()
	 */
	public boolean isSearcherCurrent() throws IOException {
		return areReadersCurrent();
	}

	/**
	 * Expert: creates a searcher from the provided {@link IndexReader} using the provided {@link
	 * SearcherFactory}. NOTE: this decRefs incoming reader on throwing an exception.
	 */
	public static IndexSearcher getSearcher(
			SearcherFactory searcherFactory, IndexReader reader, IndexReader previousReader)
			throws IOException {
		boolean success = false;
		final IndexSearcher searcher;
		try {
			searcher = searcherFactory.newSearcher(reader, previousReader);
			if (searcher.getIndexReader() != reader) {
				throw new IllegalStateException(
						"SearcherFactory must wrap exactly the provided reader (got "
						+ searcher.getIndexReader()
						+ " but expected "
						+ reader
						+ ")");
			}
			success = true;
		} finally {
			if (!success) {
				reader.decRef();
			}
		}
		return searcher;
	}
}
