package org.gtf.valorantineup.security.implementation;


import org.gtf.valorantineup.models.User;
import org.gtf.valorantineup.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    //Service to check whether user does exist or not, and check whether the origin IP has reached it's login attempt limit or not.

    private final UserRepository userRepository;
    private HttpServletRequest request;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, HttpServletRequest request) {
        this.userRepository = userRepository;
        this.request = request;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Internal Login: If username does not contain '@' login by username
        if (username.indexOf('@')==-1) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
            return UserDetailsImpl.build(user);
        }
        //External Login: Otherwise find user by email
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + username));
        if (!user.isEnabled()) {
            throw new DisabledException("Error: User disabled!");
        }
        return UserDetailsImpl.build(user);
    }


}
