/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.marx_software.lucene.index;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author t.marx
 */
@RequiredArgsConstructor
public class Shard {
	@Getter
	private final String name;
	
	int size = 0;
	
	public void incSize () {
		size++;
	}
	
	public int size () {
		return size;
	}
}
