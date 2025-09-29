package io.bootify.reserva.rest;

import io.bootify.reserva.model.ReservaDTO;
import io.bootify.reserva.service.ReservaService;
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
@RequestMapping(value = "/api/reservas", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReservaResource {

    private final ReservaService reservaService;

    public ReservaResource(final ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> getAllReservas() {
        return ResponseEntity.ok(reservaService.findAll());
    }

    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaDTO> getReserva(
            @PathVariable(name = "idReserva") final Long idReserva) {
        return ResponseEntity.ok(reservaService.get(idReserva));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createReserva(@RequestBody @Valid final ReservaDTO reservaDTO) {
        final Long createdIdReserva = reservaService.create(reservaDTO);
        return new ResponseEntity<>(createdIdReserva, HttpStatus.CREATED);
    }

    @PutMapping("/{idReserva}")
    public ResponseEntity<Long> updateReserva(
            @PathVariable(name = "idReserva") final Long idReserva,
            @RequestBody @Valid final ReservaDTO reservaDTO) {
        reservaService.update(idReserva, reservaDTO);
        return ResponseEntity.ok(idReserva);
    }

    @DeleteMapping("/{idReserva}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteReserva(
            @PathVariable(name = "idReserva") final Long idReserva) {
        reservaService.delete(idReserva);
        return ResponseEntity.noContent().build();
    }

}
