package learning;

import learning.hibernate.xmlmapping.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by John Smith on 7/7/15.
 */
public class UserBatchTest {
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
    public void testCreateWithoutBatch() throws Exception {
        int numOfEntities = 10000;
        int batchSize = 20;
        long start = System.currentTimeMillis();

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        for (int i = 1; i <= numOfEntities; i++) {
            User user = new User();
            user.setName("user-" + i);
            user.setEmail(user.getName() + "@mail.com");
            session.save(user);

            if(i % batchSize == 0) {
                session.flush();
                session.clear();
            }

//            if (i % 10000 == 0) {
//                System.out.print("Processed " + i);
//                System.out.println("; memory:" + memory());
//            }
        }

        tx.commit();
        session.close();

        long end = System.currentTimeMillis();
        System.out.println("time:" + (end - start) );
    }

    long memory() {
        return Runtime.getRuntime().freeMemory() / (1024 * 1024);
    }
}
