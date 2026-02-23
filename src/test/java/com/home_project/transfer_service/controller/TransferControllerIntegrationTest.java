package com.home_project.transfer_service.controller;

/* Couldn't find the properly working dependencies..
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TransferControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldProcessTransferEndToEnd() throws Exception {

        String requestJson = """
            {
              "fromAccountId": "123e4567-e89b-12d3-a456-426614174000",
              "toAccountId": "123e4567-e89b-12d3-a456-426614174001",
              "amount": 50000,
              "currency": "HUF",
              "description": "Test transfer"
            }
            """;

        mockMvc.perform(post("/api/v1/transfers")
                        .header("Authorization", "Bearer " + jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.amount").value(50000));
    }

    private String jwt() {
        return "test-jwt-token";
    }
}
*/
