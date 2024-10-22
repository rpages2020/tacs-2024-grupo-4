package tacs.grupo_4.configuration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping("/api/docs")
    public String docs() {
        return "Documentation available";
    }
}