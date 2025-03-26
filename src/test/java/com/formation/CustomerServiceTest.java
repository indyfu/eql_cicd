package com.formation;


import com.formation.data.entity.CustomerEntity;
import com.formation.data.repository.CustomerRepository;
import com.formation.service.CustomerService;
import com.formation.web.error.ConflictException;
import com.formation.web.error.NotFoundException;
import com.formation.web.model.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class) //1 Ajoute MockitoExtension
public class CustomerServiceTest {

    @InjectMocks //2 Injecte les mocks dans le service à tester (CustomerService)
    CustomerService customerService;

    @Mock // 3 Crée un mock pour le repository
    CustomerRepository customerRepository;

    @Test //4 Teste la méthode getAllCustomers
    void getAllCustomers(){
        // Given 5 : Configure le mock pour retourner une liste de 2 clients
        Mockito.doReturn(getMockCustomers(2)).when(customerRepository).findAll();

        // When 6 : Appelle la méthode à tester
        List<Customer> customers = customerService.getAllCustomers();
        // Then 7 : Vérifie que la méthode a retourné 2 clients
        Assertions.assertEquals(2, customers.size());
    }
        // 8 : La méthode qui retourne une liste de clients
    private Iterable<CustomerEntity> getMockCustomers(int size){
        List<CustomerEntity> customers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            customers.add(new CustomerEntity(UUID.randomUUID(),
                    "firstname" + i,
                    "lastname" +i,
                    "mail"+i,
                    "phone"+i,
                    "address"+i));
        }
        return customers;
    }

    @Test // 9 : Test la méthode getCustomer
    void getCustomer(){
        //10 : Crée un client mock
        CustomerEntity entity = getMockCustomerEntity();

        //11 : Crée un Optional contenant le client
        Optional<CustomerEntity> optional = Optional.of(entity);

        //12 : Configure le mock pour retourner l'Optional
        Mockito.doReturn(optional).when(customerRepository).findById(entity.getCustomerId());

        //13 : Appelle la méthode à tester
        Customer customer = customerService.getCustomer(entity.getCustomerId().toString());

        //14 : Vérifie que la méthode a retourné un client
        Assertions.assertNotNull(customer);

        //15 : Vérifie que le client retourné a le bon prénom
        Assertions.assertEquals("testFirst", customer.getFirstName());
    }

    private CustomerEntity getMockCustomerEntity() {
        return new CustomerEntity(UUID.randomUUID(),
                "testFirst",
                "testLast",
                "testemail@test.com",
                "555-515-1234",
                "1234 Test Street");
    }

    @Test //16 : Test la méthode getCustomer avec un client qui n'existe pas
    void getCustomer_noExists(){

        //17 : Crée un client mock
        CustomerEntity entity = getMockCustomerEntity();

        //18 : Crée un Optional vide
        Optional<CustomerEntity> optional = Optional.empty();

        //19 : Configure le mock pour retourner l'Optional
        Mockito.doReturn(optional).when(customerRepository).findById(entity.getCustomerId());

        //20 : Vérifie que la méthode lève une exception NotFoundException
        Assertions.assertThrows(NotFoundException.class,
                () -> customerService.getCustomer(entity.getCustomerId().toString()),
                "exception not throw as expected");
    }

    @Test //21 : Test la méthode findByEmailAddress
    void findByEmailAddress(){

        //22 : Crée un client mock
        CustomerEntity entity = getMockCustomerEntity();

        //23 : Configure le mock pour retourner le client
        Mockito.doReturn(entity).when(customerRepository).findByEmailAddress(entity.getEmailAddress());

        //24 : Appelle la méthode à tester
        Customer customer = customerService.findByEmailAddress(entity.getEmailAddress());

        //25 : Vérifie que la méthode a retourné un client
        Assertions.assertNotNull(customer);

        //26 : Vérifie que le client retourné a le bon prénom
        Assertions.assertEquals("testFirst", customer.getFirstName());

    }

    @Test //27 : Teste la méthode assCustomer
    void addCustomer(){
        //28 : Crée un client mock
        CustomerEntity entity = getMockCustomerEntity();

        //29 : Configure le mock pour retourner null quand on cherche un client par email
        Mockito.when(customerRepository.findByEmailAddress(entity.getEmailAddress())).thenReturn(null);

        //30 : Configure le mock pour retourner le client quand on sauvegarde
        Mockito.when(customerRepository.save(Mockito.any(CustomerEntity.class))).thenReturn(entity);

        //31 : Crée un client à ajouter (DTO)
        Customer customer = new Customer(entity.getCustomerId().toString(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmailAddress(),
                entity.getPhoneNumber(),
                entity.getAddress());

        //32 : Appelle la méthode à tester
        customer = customerService.addCustomer(customer);

        //33 : Vérifie que la méthode a retourner un client
        Assertions.assertNotNull(customer);

        //34 : Vérifie que le retourné a le bon prénom
        Assertions.assertEquals("testLast", customer.getLastName());
    }


    @Test // 35 : Test la méthode addCustomer avec un client qui existe déjà
    void aaddCustomer_existing(){

        //36 : Crée un client mock
        CustomerEntity entity = getMockCustomerEntity();

        //37 : Configure le mock pour retourner le client quand on cherche par mail
        Mockito.when(customerRepository.findByEmailAddress(entity.getEmailAddress())).thenReturn(entity);

        //38 : Crée un client à ajouter (DTO)
        Customer customer = new Customer(entity.getCustomerId().toString(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmailAddress(),
                entity.getPhoneNumber(),
                entity.getAddress());

        //39 : Vérifie que la méthode lève une exception ConflictException

        Assertions.assertThrows(ConflictException.class, () -> customerService.addCustomer(customer),
                "should have thrown conflict exception");
    }

    @Test // 40 Test la méthode updateCustomer
    void updateCustomer(){
        //41 : Crée un client mock
        CustomerEntity entity = getMockCustomerEntity();

        //42 : Configure le mock pour retourner le client quand on sauvegarde
        Mockito.when(customerRepository.save(Mockito.any(CustomerEntity.class))).thenReturn(entity);

        //43 : Crée un client à mettre à jour(DTO)
        Customer customer = new Customer(entity.getCustomerId().toString(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmailAddress(),
                entity.getPhoneNumber(),
                entity.getAddress());

        //44 : Appelle la méthode à tester
        customer = customerService.updateCustomer(customer);

        //45 : Vérifie que la méthode a retourner un client
        Assertions.assertNotNull(customer);

        //46 : Vérifie que le client retournée a le bon prénom
        Assertions.assertEquals("testLast", customer.getLastName());
    }

    @Test //47 : Test la méthode deleteCustomer
    void deleteCustomer(){
        //48 : Crée un UUID
        UUID id = UUID.randomUUID();

        //49 : Configure le mock pour ne rien faire quand on supprimer
        Mockito.doNothing().when(customerRepository).deleteById(id);

        //50 : Appelle la méthode à tester
        customerService.deleteCustomer(id.toString());
    }
}
