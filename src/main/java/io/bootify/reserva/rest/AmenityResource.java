package io.bootify.reserva.rest;

import io.bootify.reserva.model.AmenityDTO;
import io.bootify.reserva.service.AmenityService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/amenities", produces = MediaType.APPLICATION_JSON_VALUE)
public class AmenityResource {

    private final AmenityService amenityService;

    public AmenityResource(final AmenityService amenityService) {
        this.amenityService = amenityService;
    }

    @GetMapping
    public ResponseEntity<List<AmenityDTO>> getAllAmenities() {
        return ResponseEntity.ok(amenityService.findAll());
    }

    @GetMapping("/{idAmenity}")
    public ResponseEntity<AmenityDTO> getAmenity(
            @PathVariable(name = "idAmenity") final Long idAmenity) {
        return ResponseEntity.ok(amenityService.get(idAmenity));
    }

    @PostMapping("/createAmenity")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createAmenity(@RequestBody @Valid final AmenityDTO amenityDTO) {
        final Long createdIdAmenity = amenityService.create(amenityDTO);
        return new ResponseEntity<>(createdIdAmenity, HttpStatus.CREATED);
    }

    @PutMapping("/{idAmenity}")
    public ResponseEntity<Long> updateAmenity(
            @PathVariable(name = "idAmenity") final Long idAmenity,
            @RequestBody @Valid final AmenityDTO amenityDTO) {
        amenityService.update(idAmenity, amenityDTO);
        return ResponseEntity.ok(idAmenity);
    }

    @DeleteMapping("/{idAmenity}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteAmenity(
            @PathVariable(name = "idAmenity") final Long idAmenity) {
        amenityService.delete(idAmenity);
        return ResponseEntity.noContent().build();
    }
}
