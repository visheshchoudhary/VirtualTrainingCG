import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class Program1 {

    public static void main(String[] args) {

        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("postgres");

        EntityManager em = emf.createEntityManager();

        EntityTransaction et = em.getTransaction();

        Customer customer1 = new Customer();
        customer1.setAcno("13579246");
        customer1.setName("Miller");
        customer1.setBalance(80000);
        customer1.setEmail("miller@gmail.com");

        Customer customer2 = new Customer();
        customer2.setAcno("87654321");
        customer2.setName("Adam");
        customer2.setEmail("adam@gmail.com");
        customer2.setBalance(70000);

        Bank bank = new Bank();
        bank.setName("ICICI");
        bank.setIfsc("ICICI1234");

        // Bidirectional Mapping
        customer1.setBank(bank);
        customer2.setBank(bank);

        bank.setCustomer(List.of(customer1, customer2));

        et.begin();

        em.persist(bank);

        et.commit();

        em.close();
        emf.close();

        System.out.println("Data Inserted Successfully");
    }
}
