package sn.setter.utils

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
@Slf4j
class Tools {
    public static String encodePassword(String plainPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(Constantes.BCRYPT_STRENGTH);
        log.info("plainPassword : {} ",plainPassword)
        // Hacher le mot de passe
        String hashedPassword = passwordEncoder.encode(plainPassword);
        log.info("hashedPassword : {} ",hashedPassword)

        return hashedPassword;
    }

    // Méthode convertTokenToJson dans la classe Tools
    public static String convertTokenToJson(Map<String, Object> tokenMap) {
        String jsonToken = new ObjectMapper().writeValueAsString(tokenMap)
        log.info("Converted Token to JSON: {}", jsonToken)
        return jsonToken
    }
}
