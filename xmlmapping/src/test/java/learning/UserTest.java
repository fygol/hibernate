package learning;

import learning.hibernate.xmlmapping.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Created by John Smith on 06/07/2015.
 */
public class UserTest {
    static SessionFactory sessionFactory;

    @BeforeClass
    public static void setup() {
        Configuration configuration = new Configuration();
        configuration.configure();
        ServiceRegistryBuilder srBuilder = new ServiceRegistryBuilder();
        srBuilder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = srBuilder.buildServiceRegistry();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        sessionFactory.close();
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setName("John Smith");
        user.setEmail("jsmith@mail.com");

        Long userId = null;
        Session session = sessionFactory.openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            userId = user.getId();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }

        session = sessionFactory.openSession();
        User actualUser = (User)session.get(User.class, userId);
        session.close();

        assertEquals(user.getName(), actualUser.getName());
        assertEquals(user.getEmail(), actualUser.getEmail());
    }


}
