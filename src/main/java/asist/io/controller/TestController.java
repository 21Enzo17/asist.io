package asist.io.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;



@RestController
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class TestController {

    @GetMapping("/testeo")
    public ResponseEntity<?> metodoTest(HttpServletRequest request) {
        printClientIps(request);
        return ResponseEntity.ok("Hola mundo");
    }
    
    @GetMapping("/testeoAuth")
    public ResponseEntity<?> metodoTest2() {
        //TODO: process POST request
        
        return ResponseEntity.ok("Hola mundo");
    }

    public void printClientIps(HttpServletRequest request) {
        if (request != null) {
            String cfConnectingIp = request.getHeader("CF-Connecting-IP");
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            String xRealIp = request.getHeader("X-Real-IP");
            String httpXForwardedFor = request.getHeader("HTTP_X_FORWARDED_FOR");
    
            System.out.println("CF-Connecting-IP: " + cfConnectingIp);
            System.out.println("X-Forwarded-For: " + xForwardedFor);
            System.out.println("X-Real-IP: " + xRealIp);
            System.out.println("HTTP_X_FORWARDED_FOR: " + httpXForwardedFor);
        }
    }
}

