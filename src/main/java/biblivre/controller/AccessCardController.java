package biblivre.controller;

import biblivre.administration.accesscards.AccessCardBO;
import biblivre.core.exceptions.ValidationException;
import biblivre.legacy.entity.AccessCard;
import biblivre.legacy.repository.AccessCardRepository;

import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AccessCardController {
    @Autowired private AccessCardBO accessCardBO;

    @Autowired private AccessCardRepository accessCardRepository;

    @GetMapping("/api/access_card/{id}")
    public AccessCard open(@PathVariable int id) {
        Optional<AccessCard> accessCard = accessCardBO.get(id);

        return accessCard.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/api/access_card")
    public Collection<AccessCard> getAll() {
        return accessCardRepository.findAll();
    }

    @DeleteMapping("/api/access_card/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
        	accessCardBO.removeCard(id);
        }
        catch (ValidationException validationException) {
        	return (ResponseEntity<?>) ResponseEntity.notFound();
        }

        return (ResponseEntity<?>) ResponseEntity.ok();
    }
}
