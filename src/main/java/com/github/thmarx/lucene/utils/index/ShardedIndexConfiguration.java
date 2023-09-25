/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.thmarx.lucene.utils.index;

import java.nio.file.Path;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author t.marx
 */
@Builder
@Getter
public class ShardedIndexConfiguration {
	@Builder.Default
	private int numberOfShards = 1;
	private Path shardsDirectory;
	private ShardingStrategy strategy;
	private ShardConfiguration shardConfiguration;
}
