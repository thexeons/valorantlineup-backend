package org.gtf.valorantineup.services;

import org.gtf.valorantineup.enums.ERole;
import org.gtf.valorantineup.models.Role;
import org.gtf.valorantineup.repositories.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Initialization {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    RoleRepository roleRepository;

    public Initialization(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void init(){
        //Sync role from Enums
        syncRole();
    }

    public void syncRole()
    {
        LOGGER.info("Initiating role synchronization");
        for (ERole s : ERole.values()) {
            if(!roleRepository.existsByName(s))
            {
                Role x = new Role();
                x.setName(s);
                roleRepository.saveAndFlush(x);
            }
        }
        LOGGER.info("Role synchronization completed");
    }

}
