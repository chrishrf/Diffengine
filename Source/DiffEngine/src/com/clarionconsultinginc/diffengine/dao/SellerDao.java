package com.clarionconsultinginc.diffengine.dao;

import java.util.Date;
import java.util.Objects;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class SellerDao {

	public Entity retrieveSeller(String vendor) {
		Entity retEntity = null;
		DatastoreService service = DatastoreServiceFactory.getDatastoreService();

		// Creating the retrieve Query
		Query query = new Query(vendor);

		query.setFilter(new FilterPredicate("vendorHash", Query.FilterOperator.EQUAL, Objects.hash(vendor.toUpperCase())));

		PreparedQuery pq = service.prepare(query);

		// This would always return the last entity
		for (Entity entity : pq.asIterable()) {
			retEntity = entity;
		}

		return retEntity;
	}

	public Key retrieveSellerKey(String vendor) {
		return this.retrieveSeller(vendor).getKey();
	}

	public Key save(String vendor) {
		DatastoreService service = DatastoreServiceFactory.getDatastoreService();

		Entity vendorEntity = new Entity(vendor);
		vendorEntity.setProperty("vendorName", vendor);
		vendorEntity.setProperty("vendorHash", Objects.hash(vendor.toUpperCase()));
		vendorEntity.setProperty("dateCreated", new Date());

		return service.put(vendorEntity);
	}

}
