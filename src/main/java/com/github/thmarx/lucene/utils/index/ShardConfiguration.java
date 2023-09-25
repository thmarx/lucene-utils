/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.thmarx.lucene.utils.index;

import lombok.Builder;
import lombok.Getter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriterConfig;

/**
 *
 * @author t.marx
 */
@Builder
@Getter
public class ShardConfiguration {
	private Analyzer analyzer;
	@Builder.Default
	private IndexWriterConfig.OpenMode openMode = IndexWriterConfig.OpenMode.CREATE_OR_APPEND;
}
