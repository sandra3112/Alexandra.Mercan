package com.ecommerce.api.controller.order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.model.WebOrder;


@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithUserDetails("UserA")
    void userAAuthenticatedOrderList() throws Exception {
	testAuthenticatedListBelongsToUser("UserA");
    }

    @Test
    @WithUserDetails("UserB")
    void userBAuthenticatedOrderList() throws Exception {
	testAuthenticatedListBelongsToUser("UserB");
    }

    private void testAuthenticatedListBelongsToUser(String username) throws Exception {
	mvc.perform(get("/order")).andExpect(status().is(HttpStatus.OK.value()))
	.andExpect(result -> {
	    String json = result.getResponse().getContentAsString();
	    List<WebOrder> orders = new ObjectMapper().readValue(json, new TypeReference<List<WebOrder>>() {});
	    for (WebOrder order : orders) {
		assertEquals(username, order.getUser().getUsername(), "Order list should only be orders belonging to the user.");
	    }
	});
    }

    @Test
    void unauthenticatedOrderList() throws Exception {
	mvc.perform(get("/order")).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }
}
