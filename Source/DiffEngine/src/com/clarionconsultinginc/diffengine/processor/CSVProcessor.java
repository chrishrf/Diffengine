package com.clarionconsultinginc.diffengine.processor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

import com.clarionconsultinginc.diffengine.dao.ProductDao;
import com.clarionconsultinginc.diffengine.dao.SellerDao;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

public class CSVProcessor {
	private ProductDao productDao = new ProductDao();
	private SellerDao sellerDao = new SellerDao();

	public boolean processCSV(String data, String vendor) throws IOException {

		String line = "";
		String[] keys = null;
		String dateScrapped = null;
		ArrayList<Entity> products = new ArrayList<Entity>();
		Key vendorKey = retrieveVendorEntity(vendor);
		Key parentKey = null;
		Entity productEntity = null;
		String productUrl = "";

		try (ByteArrayInputStream inStream = new ByteArrayInputStream(data.getBytes());
				BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));) {

			// Retrieving the Headers
			if ((line = reader.readLine()) != null) {
				keys = retrieveKeysFromHeaderLine(line);
			}

			// Retrieving the scrapped date from the first row
			if ((line = reader.readLine()) != null) {
				Entity tmpEntity = new Entity(vendor);
				processRow(keys, line, tmpEntity);
				dateScrapped = ((Text) tmpEntity.getProperty("DOWNLOAD_TIME")).getValue().split(" ")[0];
				productUrl = ((Text) tmpEntity.getProperty("URL")).getValue();

				// Adding the product URL hash as a key for searching
				tmpEntity.setProperty("PRODUCT_ID", Objects.hash(productUrl));

				// Saving the uploadDateEntity as it is the parent of all
				// products in this CSV file
				if ((parentKey = saveUploadDateEntity(vendorKey, dateScrapped, vendor, keys)) == null) {
					return false;
				}

				// Now creating a new Product entity for the first row with the
				// correct parent entity key
				productEntity = new Entity(vendor, parentKey);
				productEntity.setPropertiesFrom(tmpEntity);

				products.add(productEntity);
			}

			while ((line = reader.readLine()) != null) {
				productEntity = new Entity(vendor, parentKey);
				processRow(keys, line, productEntity);

				productUrl = ((Text) productEntity.getProperty("URL")).getValue();

				// Adding the product URL hash as a key for searching
				productEntity.setProperty("PRODUCT_ID", Objects.hash(productUrl));

				products.add(productEntity);
			}

		}

		if (products.size() > 0) {
			saveProducts(products);
		}
		
		return true;
	}

	private String[] retrieveKeysFromHeaderLine(String headerLine) {

		String[] keys = headerLine.split(",\"");
		keys[0] = keys[0].substring(1);
		for (int i = 0; i < keys.length; i++) {
			keys[i] = keys[i].substring(0, keys[i].length() - 1).trim().toUpperCase();
		}

		return keys;
	}

	private void processRow(String[] keys, String row, Entity productEntity) {

		String[] rowData = row.split(",\"");

		rowData[0] = rowData[0].substring(1);

		for (int i = 0; i < rowData.length; i++) {
			productEntity.setProperty(keys[i], new Text((rowData[i].substring(0, rowData[i].length() - 1).trim())));
		}

		if (keys.length > rowData.length) {
			for (int i = rowData.length; i < keys.length; i++) {
				productEntity.setProperty(keys[i], new Text(""));
			}

		}
	}

	private Key retrieveVendorEntity(String vendor) {
		// This will create a new Vendor entity if it does not exist
		Entity vendorEntity = null;
		Key vendorKey = null;
		if ((vendorEntity = sellerDao.retrieveSeller(vendor)) == null) {
			vendorKey = sellerDao.save(vendor);
		} else {
			vendorKey = vendorEntity.getKey();
		}

		return vendorKey;
	}

	private void saveProducts(ArrayList<Entity> productEntities) {

		productDao.saveProducts(productEntities);
	}

	private Key saveUploadDateEntity(Key vendorKey, String uploadDate, String vendorName, String[] keys) {
		Key uploadDateKey = null;

		String delimitedKeys = "";
		for (String string : keys) {
			delimitedKeys = delimitedKeys + string + ";";
		}

		delimitedKeys = delimitedKeys.substring(0, delimitedKeys.length() - 1);

		if (productDao.retrieveProductParentForGivenUploadDate(vendorKey, uploadDate) == null) {
			uploadDateKey = productDao.saveUploadDateEntity(vendorKey, vendorName, delimitedKeys, uploadDate);
		} 

		return uploadDateKey;
	}
}
