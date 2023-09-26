
# MultiSearcherManager

Utility class IndexSearcher instances across multiple threads. It uses MultiReader, so it's possible to access
multiple lucene directories.

## Usage

```java
List<IndexReader> readers = List.of(
	DirectoryReader.open(dir1),
	DirectoryReader.open(dir2)
);
MultiSearcherManager sm = new MultiSearcherManager(readers.toArray(IndexReader[]::new));

// add, remove or update documents

sm.maybeRefreshBlocking();	
	
IndexSearcher searcher = sm.acquire();
try {
	int count = searcher.count(new MatchAllDocsQuery());
	System.out.println(count);
} finally {
	sm.release(searcher);
}

sm.close();
```

# ShardedIndex

Whenever your local lucene index hits the document limit (~2 Billion documents) and you need to scale horizontally, 
ShardedIndex may be a solution for you.

```java
ShardedIndex shardedIndex = ShardedIndexConfiguration.builder()
	.numberOfShards(5)														// the number of shards
	.strategy(new ShardSizeShardingStrategy())								// the sharding strategy
	.shardsDirectory(Path.of("index-directory"))							// base directory where all shards will be stored
	.shardConfiguration(ShardConfiguration.builder()						// configuration for the shards
			.analyzer(new StandardAnalyzer())
			.build()
	).build();

shardedIndex = new ShardedIndex(config);
shardedIndex.open();

// make use

// use MultiSearchManager to access the shards
IndexSearcher searcher = shardedIndex.getSearcherManager().acquire();
try {
	// search as usual
} finally {
	shardedIndex.getSearcherManager().release(searcher);
}

shardedIndex.close();
```

# Usage

The artifacts are deployed to [github packages](https://github.com/thmarx/lucene-utils/packages/1953386).
