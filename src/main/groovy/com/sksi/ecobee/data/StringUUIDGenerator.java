package com.sksi.ecobee.data;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

public class StringUUIDGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        Serializable id = session
            .getEntityPersister(null, object)
            .getClassMetadata()
            .getIdentifier(object, session);
        boolean validId = false;
        try {
            if (id != null) {
                UUID.fromString(id.toString());
                validId = true;
            }
        } catch (Exception e) {
            validId = false;
        }
        return validId ? id : UUID.randomUUID().toString();
    }
}
