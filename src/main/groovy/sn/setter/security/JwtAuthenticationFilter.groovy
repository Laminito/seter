package sn.setter.security

import groovy.util.logging.Slf4j
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import sn.setter.services.UtilisateurService

import java.util.stream.Collectors

@Component
@RequiredArgsConstructor
@Slf4j
class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private UtilisateurService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // Récupérer le token sans le "Bearer "
            username = jwtUtil.extractUsername(token);
        }

        if (username != null) {
            // Effectuer une requête pour récupérer les rôles de l'utilisateur
            List<SimpleGrantedAuthority> authorities = userService.getRolesByUsername(username)
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(role.getIntitule()))
                    .collect(Collectors.toList());

            // Créer l'objet d'authentification avec les rôles obtenus
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities)

            // Enregistrer l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        if (authorizationHeader == null && request.requestURI != "/api/seter/auth/login" && request.requestURI != "/api/seter/auth/signup") {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            return;
        }

        // Passer la requête à la chaîne de filtres
        filterChain.doFilter(request, response);
    }


}