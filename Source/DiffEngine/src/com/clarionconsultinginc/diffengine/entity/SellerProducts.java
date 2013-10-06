package com.clarionconsultinginc.diffengine.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class SellerProducts implements Serializable {

	private static final long serialVersionUID = 254130861354530549L;

	private String uploadDate;
	private String seller;

	private String[] columns;
	private ArrayList<HashMap<String, String>> products;

	public String getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(String date) {
		this.uploadDate = date;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public ArrayList<HashMap<String, String>> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<HashMap<String, String>> products) {
		this.products = products;
	}

}
