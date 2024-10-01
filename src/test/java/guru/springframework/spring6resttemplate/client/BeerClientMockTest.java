package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static guru.springframework.spring6resttemplate.client.BeerClientImpl.BEER_PATH;
import static guru.springframework.spring6resttemplate.client.BeerClientImpl.BEER_PATH_BY_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(BeerClientImpl.class)
public class BeerClientMockTest {

    @Autowired
    BeerClient beerClient;

    @Autowired
    MockRestServiceServer mockServer;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testListBeers() throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(getPage());

        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(BEER_PATH))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        Page<BeerDTO> beerDTOS = beerClient.listBeers();
        assertThat(beerDTOS.getContent().size()).isGreaterThan(0);
        assertThat(beerDTOS.getContent().getFirst().getBeerName()).isEqualTo("Test beer");
    }

    @Test
    void testGetBeerById() throws JsonProcessingException {
        UUID id = UUID.randomUUID();
        String payload = objectMapper.writeValueAsString(BeerDTO.builder()
                                                                .beerName("Test Get Beer")
                                                                .id(id)
                                                                .beerStyle(BeerStyle.ALE)
                                                                .build());
        mockGetRequest(id, payload);

        BeerDTO beerById = beerClient.getBeerById(id);
        assertThat(beerById.getBeerName()).isEqualTo("Test Get Beer");
        assertThat(beerById.getId()).isEqualTo(id);
    }

    @Test
    void testCreateBeer() throws JsonProcessingException {
        UUID id = UUID.randomUUID();
        String location = BEER_PATH + "/" + id;
        BeerDTO testCreateBeer = BeerDTO.builder()
                .beerName("Test Create Beer")
                .id(id)
                .beerStyle(BeerStyle.ALE)
                .build();
        String payload = objectMapper.writeValueAsString(testCreateBeer);
        mockServer.expect(method(HttpMethod.POST))
                .andExpect(requestTo(BEER_PATH))
                .andExpect(content().json(payload))
                .andRespond(withCreatedEntity(URI.create(location)));
        mockGetRequest(id, payload);

        BeerDTO createdBeer = beerClient.createBeer(testCreateBeer);
        assertThat(createdBeer.getBeerName()).isEqualTo("Test Create Beer");
        assertThat(createdBeer.getId()).isEqualTo(id);
    }

    private void mockGetRequest(UUID id, String payload) {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestToUriTemplate(BEER_PATH_BY_ID, id))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdateBeer() throws JsonProcessingException {
        UUID id = UUID.randomUUID();
        BeerDTO testUpdateBeer = BeerDTO.builder()
                .beerName("Test Update Beer")
                .id(id)
                .beerStyle(BeerStyle.ALE)
                .build();
        String payload = objectMapper.writeValueAsString(testUpdateBeer);
        mockServer.expect(method(HttpMethod.PUT))
                .andExpect(requestToUriTemplate(BEER_PATH_BY_ID, id))
                .andExpect(content().json(payload))
                .andRespond(withNoContent());
        mockGetRequest(id, payload);

        BeerDTO beerDTO = beerClient.updateBeer(testUpdateBeer);
        assertThat(beerDTO.getId()).isEqualTo(id);

    }

    @Test
    void testDeleteBeer() {
        UUID id = UUID.randomUUID();
        mockServer.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(BEER_PATH_BY_ID, id))
                .andRespond(withNoContent());
        beerClient.deleteBeer(id);
        mockServer.verify();
    }

    @Test
    void testDeleteBeerNotFound() {
        UUID id = UUID.randomUUID();
        mockServer.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(BEER_PATH_BY_ID, id))
                .andRespond(withResourceNotFound());

        assertThrows(HttpClientErrorException.class, () -> beerClient.deleteBeer(id));
        mockServer.verify();

    }

    @Test
    void testListBeerQueryParam() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(getPage());
        URI uri = UriComponentsBuilder.fromPath(BEER_PATH).queryParam("beerStyle", BeerStyle.PALE_ALE).build().toUri();
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(uri))
                .andExpect(queryParam("beerStyle", BeerStyle.PALE_ALE.name()))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        Page<BeerDTO> beerDTOS = beerClient.listBeers(null, BeerStyle.PALE_ALE, null, null, null);
        assertThat(beerDTOS.getContent().size()).isEqualTo(1);

    }

    BeerPageImpl getPage() {
        return new BeerPageImpl(Arrays.asList(getBeerDTO()), 1, 25, 1);
    }

    private BeerDTO getBeerDTO() {
        return BeerDTO.builder()
                .beerName("Test beer")
                .price(new BigDecimal("23.99"))
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12342")
                .quantityOnHand(1142)
                .build();
    }


}
