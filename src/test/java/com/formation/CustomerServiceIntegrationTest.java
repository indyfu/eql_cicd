package com.formation;

import com.formation.service.CustomerService;
import com.formation.web.error.ConflictException;
import com.formation.web.error.NotFoundException;
import com.formation.web.model.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CustomerServiceIntegrationTest {

    @Autowired
    CustomerService customerService;

    @Test
    void getAllCustomers(){
    List<Customer> customers = customerService.getAllCustomers();
        Assertions.assertEquals(5, customers.size());
}
    @Test
    void getCustomer(){
    // 6: On appelle le service pour récupérer un client avec l'identifiant "054b145c-ddbc-4136-a2bd-7bf45ed1bef7"
        Customer customer = customerService.getCustomer("054b145c-ddbc-4136-a2bd-7bf45ed1bef7");
    // 7: On vérifie que le client retourné n'est pas null
        Assertions.assertNotNull(customer);
    // 8: On vérifie que le prénom du client est bien "Cally"
        Assertions.assertEquals("Cally", customer.getFirstName());
    }

    @Test
    void getCustomer_NotFound(){
        //9 : On appelle le service pour récupérer un client avec un id qui n'existe pas
        Assertions.assertThrows(NotFoundException.class, () -> this.customerService.getCustomer("d972b30f-21cc-411f-b374-685ce23cd317"),
                "should have thrown an exception");
    }

    @Test
    void addCustomer(){
        //10 : On crée un nouveau client
        Customer customer = new Customer("", "John", "Doe", "jdoe@test.com", "555-515-123", "1234 Main Street; Anytown, KS 66110");

        //11: On appelle le service pour ajouter le client
        customer = customerService.addCustomer(customer);

        //12 : On vérifie que le client a bien été ajouté
        Assertions.assertTrue(StringUtils.isNotBlank(customer.getCustomerId()));

        //13 : On vérifie que le prénom du client est bien "John"
        Assertions.assertEquals("John", customer.getFirstName());

        //14 : On appelle le service pour supprimer le client
        this.customerService.deleteCustomer(customer.getCustomerId());
    }

    @Test
    void addCustomer_alreadyExists(){
        //15 : On crée un nouveau client avec une adresse email qui existe déjà
        Customer customer = new Customer("", "John", "Doe", "penatibus.et@lectusa.com", "555-515-123", "1234 Main Street; Anytown, KS 66110");

        //16 : On appelle le service pour ajouter le client
        Assertions.assertThrows(ConflictException.class, () -> customerService.addCustomer(customer));

        }
    @Test
    void updateCustomer() {
        // 17 : On crée un nouveau client
        Customer customer = new Customer("", "John", "Doe", "jdoe@test.com", "555-515-123", "1234 Main Street; Anytown, KS 66110");

        //18 : On appelle le service pour ajouter le client
        customer = customerService.addCustomer(customer);

        //19 : On modifie le prénom du client
        customer.setFirstName("Jane");

        //20 : On appelle le service pour mettre à jour le client
        customer = customerService.updateCustomer(customer);

        //21 On vérifie que le prénom du client a bien été modifié
        Assertions.assertEquals("Jane", customer.getFirstName());

        //22 : On appelle le service pour supprimer le client
        customerService.deleteCustomer(customer.getCustomerId());
    }
    }


