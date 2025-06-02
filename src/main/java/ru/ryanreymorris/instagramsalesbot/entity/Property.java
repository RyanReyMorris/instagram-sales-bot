package ru.ryanreymorris.instagramsalesbot.entity;

import ru.ryanreymorris.instagramsalesbot.config.Properties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity(name = "Property")
@Table(name = "property")
public class Property {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "key")
    private Properties key;

    @Column(name = "value")
    private String value;

    public Property() {
    }

    public Property(Properties key, String value) {
        this.key = key;
        this.value = value;
    }

    public Properties getKey() {
        return key;
    }

    public void setKey(Properties key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return key == property.key && Objects.equals(value, property.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "Property{" +
                "key=" + key +
                ", value='" + value + '\'' +
                '}';
    }
}