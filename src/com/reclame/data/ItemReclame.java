package com.reclame.data;



public class ItemReclame {
	
	private long ID;
	private String name;
	private String description;
	private String url_picture;
	
	public boolean box;


	public long getID() {
		return ID;
	}


	public void setID(long iD) {
		ID = iD;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getUrl_picture() {
		return url_picture;
	}


	public void setUrl_picture(String url_picture) {
		this.url_picture = url_picture;
	}
	
	@Override
    public String toString() {
        return ID + " - " + description;
    }

}
