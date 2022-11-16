package se77.springgraphqldemo;

import java.util.Collection;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.stereotype.Service;

import graphql.schema.idl.RuntimeWiring.Builder;

@SpringBootApplication
public class SpringGraphqlDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGraphqlDemoApplication.class, args);
	}

	@Bean
	RuntimeWiringConfigurer runtimeWiringConf(CrmService service) {
		return new RuntimeWiringConfigurer() {

			@Override
			public void configure(Builder builder) {
				
				builder.type("Customer", wiring ->
					wiring.dataFetcher("profile", e -> service.getProfileFor(e.getSource())));

				builder.type("Query", wiring -> 
					wiring.dataFetcher("customersById", env -> service.getCustomerById(env.getArgument("id")))
						.dataFetcher("customers", env -> service.getCustomers()));
			}
		};
	}

}

record Customer(Integer id, String name) {
};

record Profile (Integer id, Integer CustomerId) {};

@Service
class CrmService {
	
	public Profile getProfileFor(Customer customer) {
		return new Profile(customer.id(), customer.id());
	}

	public Customer getCustomerById(Integer id) {
		return new Customer(id, "Ernie");
	}
	
	public Collection<Customer> getCustomers() {
		return List.of(new Customer(1, "Ernie"), new Customer(1, "Bert"));
	}
}
