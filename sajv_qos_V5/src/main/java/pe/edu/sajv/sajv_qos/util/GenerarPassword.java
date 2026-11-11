package pe.edu.sajv.sajv_qos.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class GenerarPassword implements CommandLineRunner {
 
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String password = "Cata2028*";

        String hash = passwordEncoder.encode(password);

        System.out.println("--------------------------------");
        System.out.println("HASH BCrypt:");
        System.out.println(hash);
        System.out.println("--------------------------------");

        System.out.println(
            "Verificación: " +
            passwordEncoder.matches(password, hash)
        );
    }
}
