package org.hibernate.integrator.api.integrator;

import com.example.account.core.migration.HibernateInfoHolder;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class Integrator implements org.hibernate.integrator.spi.Integrator {

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        HibernateInfoHolder.setMetadata(metadata);
        HibernateInfoHolder.setSessionFactory(sessionFactory);
        HibernateInfoHolder.setServiceRegistry(serviceRegistry);
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
    }
}
