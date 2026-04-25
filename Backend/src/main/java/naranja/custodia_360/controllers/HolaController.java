package naranja.custodia_360.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaController {

    @GetMapping()
    public String decirHola() {
        return "¡Felicidades! Tu primer proyecto en Spring Boot está vivo. 🚀";
    }
}