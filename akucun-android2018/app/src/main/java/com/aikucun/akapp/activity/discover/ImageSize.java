package com.aikucun.akapp.activity.discover;

import java.io.Serializable;

public class ImageSize implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int width;
	public int height;
	public String name;
	
	
	public ImageSize(int width, int height, String name){
		this.width = width;
		this.height = height;
		this.name = name;
		
	}
}
