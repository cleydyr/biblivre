package biblivre.controller;

import biblivre.administration.accesscards.AccessCardBO;
import biblivre.legacy.entity.AccessCard;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AccessCardController {
    /*
     * search
     * save
     * delete
     * update
     */

    @Autowired private AccessCardBO accessCardBO;

    @GetMapping("/api/access_card/{id}")
    public AccessCard open(@PathVariable int id) {
        Optional<AccessCard> accessCard = accessCardBO.get(id);

        return accessCard.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
