package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplate restTemplate;

    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_PATH_BY_ID = "/api/v1/beer/{beerId}";

    public BeerClientImpl(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }


    @Override
    public Page<BeerDTO> listBeers() {
        return this.listBeers(null,null,null,null,null);
    }

    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Integer pageNumber, Integer pageSize, Boolean showInventory) {
/*        ResponseEntity<String> stringResponse = restTemplate.getForEntity(
                BASE_URL + BEER_PATH, String.class);
        System.out.println(stringResponse.getBody());

        ResponseEntity<Map> mapResponse = restTemplate.getForEntity(
                BASE_URL + BEER_PATH, Map.class);

        ResponseEntity<JsonNode> jsonResponse = restTemplate.getForEntity(
                BASE_URL + BEER_PATH, JsonNode.class);
        jsonResponse.getBody()
                .findPath("content")
                .elements()
                .forEachRemaining(node -> System.out.println(node.get("beerName").asText()));
 */

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(BEER_PATH);
        if(null != beerName) {
            uriComponentsBuilder.queryParam("beerName", beerName);
        }
        if(null != beerStyle) {
            uriComponentsBuilder.queryParam("beerStyle", beerStyle);
        }
        if(null != pageNumber) {
            uriComponentsBuilder.queryParam("pageNumber", pageNumber);
        }
        if(null != pageSize) {
            uriComponentsBuilder.queryParam("pageSize", pageSize);
        }
        if(null != showInventory) {
            uriComponentsBuilder.queryParam("showInventory", showInventory);
        }

        ResponseEntity<BeerPageImpl> responseEntity =
                restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerPageImpl.class);
        return responseEntity.getBody();
    }

    @Override
    public BeerDTO getBeerById(UUID beerId) {
        return restTemplate.getForObject(BEER_PATH_BY_ID, BeerDTO.class, beerId);
    }

    @Override
    public BeerDTO createBeer(BeerDTO beerDTO) {
        URI location = restTemplate.postForLocation(BEER_PATH, beerDTO);
        assert location != null;
        return restTemplate.getForObject(location.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO updateBeer(BeerDTO beerDTO) {
        restTemplate.put(BEER_PATH_BY_ID, beerDTO, beerDTO.getId());
        return getBeerById(beerDTO.getId());
    }

    @Override
    public void deleteBeer(UUID id) {
        restTemplate.delete(BEER_PATH_BY_ID, id);
    }
}
