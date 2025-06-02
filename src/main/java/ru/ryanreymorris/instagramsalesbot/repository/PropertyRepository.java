package ru.ryanreymorris.instagramsalesbot.repository;

import ru.ryanreymorris.instagramsalesbot.config.Properties;
import ru.ryanreymorris.instagramsalesbot.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Properties> {
}
