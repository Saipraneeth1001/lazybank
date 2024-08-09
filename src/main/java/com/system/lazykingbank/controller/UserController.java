package com.system.lazykingbank.controller;

import com.system.lazykingbank.entity.Authority;
import com.system.lazykingbank.repository.AuthorityRepository;
import com.system.lazykingbank.repository.CustomerRepository;
import com.system.lazykingbank.entity.Customer;
import com.system.lazykingbank.request.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping(value = "/hello")
    public List<Customer> hello() {
        return repository.findAll();
    }

//    @PostMapping(value = "/createUsers")
//    public List<Customer> createCustomer() {
//        List<Customer> list = new ArrayList<>();
//        for (int i = 0;i < 5;i++) {
//            Customer customer = new Customer();
//            String email = "customer"+i;
//            String password = "password"+i;
//            customer.setEmail(email);
//            customer.setPassword(encoder.encode(password));
//            customer = repository.save(customer);
//            list.add(customer);
//        }
//        return list;
//    }

    @PostMapping("/register")
    public Customer register(@RequestBody User user) {
        Customer customer = new Customer();
        customer.setEmail(user.getName());
        customer.setPassword(encoder.encode(user.getPassword()));
        Authority authority = new Authority();
//        authority.setCustomer(customer);
        authority.setRole("admin");
        customer.setAuthorities(Set.of(authority));
        customer = repository.save(customer);
//        authorityRepository.save(authority);
        return customer;
    }

    @GetMapping(value = "/signin")
    public List<Customer> getCustomers() {
        List<Customer> customers = repository.findAll();
        return customers;
    }
}
