package asist.io.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class TestController {

    @GetMapping("/testeo")
    public ResponseEntity<?> metodoTest() {
        //TODO: process POST request
        
        return ResponseEntity.ok("Hola mundo");
    }
    
    @GetMapping("/testeoAuth")
    public ResponseEntity<?> metodoTest2() {
        //TODO: process POST request
        
        return ResponseEntity.ok("Hola mundo");
    }
}
