package io.bootify.reserva.events;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class BeforeDeleteUser {

    private Long idUser;

}
