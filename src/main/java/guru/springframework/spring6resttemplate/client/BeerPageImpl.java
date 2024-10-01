package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@JsonIgnoreProperties(value = "pageable", ignoreUnknown = true)
public class BeerPageImpl extends PageImpl<BeerDTO> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BeerPageImpl(
            @JsonProperty("content") List<BeerDTO> content,
            @JsonProperty("number") int page,
            @JsonProperty("size") int size,
            @JsonProperty("totalElements") long total) {
        super(content, PageRequest.of(page, size), total);
    }

    public BeerPageImpl(List<BeerDTO> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public BeerPageImpl(List<BeerDTO> content) {
        super(content);
    }
}
