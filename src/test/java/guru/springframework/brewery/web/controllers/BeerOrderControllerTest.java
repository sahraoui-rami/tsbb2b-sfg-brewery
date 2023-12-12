package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.BeerOrderDto;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Rami SAHRAOUI on 12/12/2023
 */
@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {
    @MockBean
    BeerOrderService beerOrderService;

    @Autowired
    MockMvc mockMvc;

    UUID customerId;
    UUID orderId;

    BeerOrderDto beerOrder;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        beerOrder = BeerOrderDto.builder()
                .id(orderId)
                .customerId(customerId)
                .createdDate(OffsetDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
        reset(beerOrderService);
    }

    @Test
    void getOrder() throws Exception {
        given(beerOrderService.getOrderById(any(), any())).willReturn(beerOrder);

        mockMvc.perform(get("/api/v1/customers/" + customerId +
                "/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.customerId", is(customerId.toString())));
    }

    @DisplayName("List Ops - ")
    @Nested
    class ListOperations {
        @Captor
        ArgumentCaptor<UUID> customerIdCaptor;

        @Captor
        ArgumentCaptor<PageRequest> pageRequestCaptor;

        BeerOrderPagedList beerOrderPagedList;

        @BeforeEach
        void setUp() {
            List<BeerOrderDto> orders = new ArrayList<>();

            orders.add(beerOrder);
            orders.add(
                    BeerOrderDto.builder()
                    .id(UUID.randomUUID())
                    .customerId(customerId)
                    .createdDate(OffsetDateTime.now())
                    .build()
            );

            beerOrderPagedList = new BeerOrderPagedList(orders, PageRequest.of(1, 1), 2L);

            given(beerOrderService.listOrders(customerIdCaptor.capture(), pageRequestCaptor.capture()))
                    .willReturn(beerOrderPagedList);
        }

        @DisplayName("Test List Orders")
        @Test
        void listOrders() throws Exception {
            MvcResult result = mockMvc.perform(
                    get("/api/v1/customers/" + customerId + "/orders")
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].customerId", is(customerId.toString())))
                    .andExpect(jsonPath("$.content[0].id", is(orderId.toString())))
                    .andExpect(jsonPath("$.content[1].customerId", is(customerId.toString())))
                    .andReturn();

            assertThat(customerIdCaptor.getValue()).isEqualTo(customerId);

            System.out.println(result.getResponse().getContentAsString());
        }
    }
}
