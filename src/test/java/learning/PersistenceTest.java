package learning;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * Created by John Smith on 30/05/15.
 */
public class PersistenceTest {

    SessionFactory factory;

    @BeforeSuite
    public void setup() {
        Configuration configuration = new Configuration();
        configuration.configure();
        ServiceRegistryBuilder srBuilder = new ServiceRegistryBuilder();
        srBuilder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = srBuilder.buildServiceRegistry();
        factory = configuration.buildSessionFactory(serviceRegistry);
    }

    @Test
    public void saveMessage() {
        Message message = new Message("test message");
        //message.setBody("test message");
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        session.persist(message);
        tx.commit();
        session.close();
    }

    @Test
    public void readAllMessages() {
        Session session = factory.openSession();
        Query query = session.createQuery("from Message m");
        query.list()
                .stream()
                .forEach(System.out::println);
        session.close();
    }
}
