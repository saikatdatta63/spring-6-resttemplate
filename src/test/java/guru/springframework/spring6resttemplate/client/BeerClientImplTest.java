package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClient beerClient;

    @Test
    void listBeers() {
        beerClient.listBeers("IPA", BeerStyle.IPA, 2, 5, true)
                .forEach(System.out::println);
    }

    @Test
    void getBeerById() {
        BeerDTO beerDTO = beerClient.listBeers().getContent().getFirst();
        assertNotNull(beerClient.getBeerById(beerDTO.getId()));
    }

    @Test
    void createBeer() {
        BeerDTO beerDTO = BeerDTO.builder()
                .beerName("Test Beer")
                .beerStyle(BeerStyle.IPA)
                .upc("13422")
                .quantityOnHand(12412)
                .price(new BigDecimal("24.99"))
                .build();

        BeerDTO savedBeer = beerClient.createBeer(beerDTO);
        assertNotNull(savedBeer);
        assertEquals(beerDTO.getBeerName(), savedBeer.getBeerName());
    }

    @Test
    void updateBeer() {
        BeerDTO beerDTO = beerClient.listBeers().getContent().getFirst();
        beerDTO.setBeerName("Test Beer Updated");
        BeerDTO updatedBeer = beerClient.updateBeer(beerDTO);
        assertEquals(beerDTO.getBeerName(), updatedBeer.getBeerName());
    }

    @Test
    void deleteBeer() {
        BeerDTO beerDTO = beerClient.listBeers().getContent().getFirst();
        beerClient.deleteBeer(beerDTO.getId());
        assertThrows(HttpClientErrorException.class, () -> beerClient.getBeerById(beerDTO.getId()));
    }
}