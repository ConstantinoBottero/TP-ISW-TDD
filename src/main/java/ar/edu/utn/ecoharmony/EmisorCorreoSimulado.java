package ar.edu.utn.ecoharmony;

import java.util.ArrayList;
import java.util.List;

public class EmisorCorreoSimulado implements EmisorCorreo {
    private final List<MensajeConfirmacion> bandeja = new ArrayList<>();

    @Override
    public void enviar(MensajeConfirmacion mensaje) {
        bandeja.add(mensaje);
    }

    public List<MensajeConfirmacion> mensajes() {
        return List.copyOf(bandeja);
    }
}
