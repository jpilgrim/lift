package lift;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import lift.boundary.Doors;
import lift.boundary.Requests;
import lift.logic.IDoors;
import lift.logic.IRequests;


public class Starter {

	
	
	public static Injector createInjector() {
		Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				bind(IDoors.class).to(Doors.class);
				bind(IRequests.class).to(Requests.class);
			}
		});
		return injector;
	}
	
}
