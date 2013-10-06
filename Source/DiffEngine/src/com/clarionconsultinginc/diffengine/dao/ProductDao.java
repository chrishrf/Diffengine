package com.clarionconsultinginc.diffengine.dao;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Text;

public class ProductDao {

	public Entity retrieveProductParentForGivenUploadDate(Key vendorKey, String uploadDate) {
		DatastoreService service = DatastoreServiceFactory.getDatastoreService();
		Entity child = null;
		Query query = new Query(vendorKey.getKind()).setAncestor(vendorKey);

		query.setFilter(new FilterPredicate("uploadDate", Query.FilterOperator.EQUAL, uploadDate));

		PreparedQuery pq = service.prepare(query);

		// This returns the last child for a given date
		for (Entity childEntity : pq.asIterable()) {
			child = childEntity;
		}
		return child;
	}

	public Key saveUploadDateEntity(Key vendorKey, String vendor, String delimitedKeys, String uploadDate) {
		DatastoreService service = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity(vendorKey.getKind(), vendorKey);
		entity.setProperty("uploadDate", uploadDate);
		entity.setProperty("vendorName", vendor);
		entity.setProperty("delimitedKeys", new Text(delimitedKeys));

		return service.put(entity);
	}

	public void saveProducts(List<Entity> productEntities) {
		DatastoreService service = DatastoreServiceFactory.getDatastoreService();

		service.put(productEntities);
	}

	public List<Entity> retrieveProducts(Key uploadDateEntityKey) {
		DatastoreService service = DatastoreServiceFactory.getDatastoreService();
		ArrayList<Entity> products = new ArrayList<Entity>();

		Query query = new Query(uploadDateEntityKey);
		for (Entity entity : service.prepare(query).asIterable()) {
			products.add(entity);
		}

		return products;
	}
}
