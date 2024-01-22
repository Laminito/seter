package sn.setter.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import sn.setter.models.Utilisateur
import sn.setter.repository.UtilisateursRepository

import java.util.stream.Collectors

@Service
class UserDetail implements UserDetailsService {
    @Autowired
    private UtilisateursRepository userRepo;
    @Override
    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException {
        Utilisateur user = userRepo.findByUsernameOrEmail(username, username);
        if(user==null){
            throw new UsernameNotFoundException("User not exists by Username");
        }

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map((role) -> new SimpleGrantedAuthority(role.getIntitule()))
                .collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(username,user.getPassword(),authorities);

    }
}