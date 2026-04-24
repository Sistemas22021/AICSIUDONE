package holamundo.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaController {

    @GetMapping("/saludo")
    public String decirHola() {
        return "¡Felicidades! Tu primer proyecto en Spring Boot está vivo. 🚀";
    }
}