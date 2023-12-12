package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.repositories.CustomerRepository;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Rami SAHRAOUI on 12/12/2023
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerOrderControllerIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    CustomerRepository customerRepository;

    UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = customerRepository.findAll().get(0).getId();
    }

    @Test
    void listOrders() {
        BeerOrderPagedList beerOrderPagedList = restTemplate.getForObject("/api/v1/customers/" + customerId + "/orders", BeerOrderPagedList.class);

        assertThat(beerOrderPagedList).hasSize(1);
    }
}
