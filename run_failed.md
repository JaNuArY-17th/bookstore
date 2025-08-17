BUILD FAILED in 4s
PS D:\Study\Term6\DSA\Bookstore\bookstore> ./gradlew run    

> Task :app:compileJava
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\dao\OrderQueueDAO.java:3: error: package org.springframework.stereotype does not exist
import org.springframework.stereotype.Repository;
                                     ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\dao\OrderQueueDAO.java:18: error: cannot find symbol
@Repository
 ^
  symbol: class Repository
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\dao\OrderStatusHistoryDAO.java:3: error: package org.springframework.stereotype does not exist
import org.springframework.stereotype.Repository;
                                     ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\dao\OrderStatusHistoryDAO.java:17: error: cannot find symbol
@Repository
 ^
  symbol: class Repository
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\dao\QueueOrderDAO.java:3: error: package org.springframework.stereotype does not exist
import org.springframework.stereotype.Repository;
                                     ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\dao\QueueOrderDAO.java:17: error: cannot find symbol
@Repository
 ^
  symbol: class Repository
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\dao\UserDAO.java:7: error: package org.springframework.stereotype does not exist
import org.springframework.stereotype.Repository;
                                     ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\dao\UserDAO.java:16: error: cannot find symbol
@Repository
 ^
  symbol: class Repository
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\AdminQueueService.java:9: error: package org.springframework.beans.factory.annotation does not exist
import org.springframework.beans.factory.annotation.Autowired;
                                                   ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\AdminQueueService.java:10: error: package org.springframework.stereotype does not exist
import org.springframework.stereotype.Service;
                                     ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\AdminQueueService.java:21: error: cannot find symbol
@Service
 ^
  symbol: class Service
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\QueueManagementService.java:15: error: package org.springframework.stereotype does not exist
import org.springframework.stereotype.Service;
                                     ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\QueueManagementService.java:25: error: cannot find symbol
@Service
 ^
  symbol: class Service
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\AuthService.java:5: error: package com.bookstore.security does not exist
import com.bookstore.security.JwtTokenProvider;
                             ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\AuthService.java:6: error: package org.springframework.beans.factory.annotation does not exist
import org.springframework.beans.factory.annotation.Autowired;
                                                   ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\AuthService.java:7: error: package org.springframework.stereotype does not exist
import org.springframework.stereotype.Service;
                                     ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\AuthService.java:13: error: cannot find symbol
@Service
 ^
  symbol: class Service
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:6: error: package com.bookstore.security does not exist
import com.bookstore.security.UserPrincipal;
                             ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:7: error: package org.springframework.beans.factory.annotation does not exist
import org.springframework.beans.factory.annotation.Autowired;
                                                   ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:8: error: package org.springframework.security.core.userdetails does not exist
import org.springframework.security.core.userdetails.UserDetails;
                                                    ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:9: error: package org.springframework.security.core.userdetails does not exist
import org.springframework.security.core.userdetails.UserDetailsService;
                                                    ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:10: error: package org.springframework.security.core.userdetails does not exist
import org.springframework.security.core.userdetails.UsernameNotFoundException;
                                                    ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:11: error: package org.springframework.security.crypto.password does not exist
import org.springframework.security.crypto.password.PasswordEncoder;
                                                   ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:12: error: package org.springframework.stereotype does not exist
import org.springframework.stereotype.Service;
                                     ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:21: error: cannot find symbol
public class UserService implements UserDetailsService {
                                    ^
  symbol: class UserDetailsService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:20: error: cannot find symbol
@Service
 ^
  symbol: class Service
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\AuthService.java:20: error: cannot find symbol
    private JwtTokenProvider tokenProvider;
            ^
  symbol:   class JwtTokenProvider
  location: class AuthService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:27: error: cannot find symbol
    private PasswordEncoder passwordEncoder;
            ^
  symbol:   class PasswordEncoder
  location: class UserService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:30: error: cannot find symbol
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
           ^
  symbol:   class UserDetails
  location: class UserService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:30: error: cannot find symbol
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                                                                  ^
  symbol:   class UsernameNotFoundException
  location: class UserService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:44: error: cannot find symbol
    public UserDetails loadUserById(Integer userId) throws UsernameNotFoundException {
           ^
  symbol:   class UserDetails
  location: class UserService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:44: error: cannot find symbol
    public UserDetails loadUserById(Integer userId) throws UsernameNotFoundException {
                                                           ^
  symbol:   class UsernameNotFoundException
  location: class UserService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderHistoryService.java:11: error: package org.springframework.beans.factory.annotation does not exist
import org.springframework.beans.factory.annotation.Autowired;
                                                   ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderHistoryService.java:12: error: package org.springframework.stereotype does not exist
import org.springframework.stereotype.Service;
                                     ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderHistoryService.java:26: error: cannot find symbol
@Service
 ^
  symbol: class Service
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderTrackingService.java:11: error: package org.springframework.beans.factory.annotation does not exist
import org.springframework.beans.factory.annotation.Autowired;
                                                   ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderTrackingService.java:12: error: package org.springframework.stereotype does not exist
import org.springframework.stereotype.Service;
                                     ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderTrackingService.java:25: error: cannot find symbol
@Service
 ^
  symbol: class Service
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderSecurityService.java:5: error: package org.springframework.beans.factory.annotation does not exist
import org.springframework.beans.factory.annotation.Autowired;
                                                   ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderSecurityService.java:6: error: package org.springframework.stereotype does not exist
import org.springframework.stereotype.Service;
                                     ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderSecurityService.java:12: error: cannot find symbol
@Service
 ^
  symbol: class Service
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserQueueService.java:9: error: package org.springframework.beans.factory.annotation does not exist
import org.springframework.beans.factory.annotation.Autowired;
                                                   ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserQueueService.java:10: error: package org.springframework.stereotype does not exist
import org.springframework.stereotype.Service;
                                     ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserQueueService.java:19: error: cannot find symbol
@Service
 ^
  symbol: class Service
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\AdminQueueService.java:28: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class AdminQueueService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\AuthService.java:16: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class AuthService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\AuthService.java:19: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class AuthService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:23: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class UserService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:26: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class UserService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderHistoryService.java:29: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class OrderHistoryService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderHistoryService.java:32: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class OrderHistoryService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderHistoryService.java:35: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class OrderHistoryService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderHistoryService.java:38: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class OrderHistoryService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderTrackingService.java:28: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class OrderTrackingService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderTrackingService.java:31: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class OrderTrackingService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderTrackingService.java:34: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class OrderTrackingService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\OrderSecurityService.java:15: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class OrderSecurityService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserQueueService.java:25: error: cannot find symbol
    @Autowired
     ^
  symbol:   class Autowired
  location: class UserQueueService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\main\Main.java:362: error: no suitable constructor found for Customer(int,String,String,String,String,String)
        Customer customer = new Customer(0, firstName, lastName, email, phone, address);
                            ^
    constructor Customer.Customer() is not applicable
      (actual and formal argument lists differ in length)
    constructor Customer.Customer(int,String,String,String) is not applicable
      (actual and formal argument lists differ in length)
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\main\Main.java:388: error: cannot find symbol
                             truncate(customer.getFirstName(), 15),
                                              ^
  symbol:   method getFirstName()
  location: variable customer of type Customer
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\main\Main.java:389: error: cannot find symbol
                             truncate(customer.getLastName(), 15),
                                              ^
  symbol:   method getLastName()
  location: variable customer of type Customer
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\main\Main.java:391: error: cannot find symbol
                             truncate(customer.getPhone(), 15));
                                              ^
  symbol:   method getPhone()
  location: variable customer of type Customer
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\main\Main.java:421: error: cannot find symbol
                             customer.getFirstName(),
                                     ^
  symbol:   method getFirstName()
  location: variable customer of type Customer
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\main\Main.java:422: error: cannot find symbol
                             customer.getLastName(),
                                     ^
  symbol:   method getLastName()
  location: variable customer of type Customer
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\main\Main.java:528: error: cannot find symbol
            List<OrderItem> items = orderDAO.getOrderItemsByOrderId(orderId);
                                            ^
  symbol:   method getOrderItemsByOrderId(int)
  location: variable orderDAO of type OrderDAO
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\main\Main.java:590: error: incompatible types: List<Book> cannot be converted to List<OrderItem>
        SortingAlgorithms.quickSort(books, SortingAlgorithms.BOOK_TITLE_COMPARATOR);
                                    ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\main\Main.java:594: error: cannot find symbol
        SortingAlgorithms.quickSort(books, SortingAlgorithms.BOOK_PRICE_COMPARATOR);
                                                            ^
  symbol:   variable BOOK_PRICE_COMPARATOR
  location: class SortingAlgorithms
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:29: error: method does not override or implement a method from a supertype
    @Override
    ^
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:33: error: cannot find symbol
            throw new UsernameNotFoundException("User not found with username: " + username);
                      ^
  symbol:   class UsernameNotFoundException
  location: class UserService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:35: error: cannot find symbol
        return new UserPrincipal(user);
                   ^
  symbol:   class UserPrincipal
  location: class UserService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:47: error: cannot find symbol
            throw new UsernameNotFoundException("User not found with id: " + userId);
                      ^
  symbol:   class UsernameNotFoundException
  location: class UserService
D:\Study\Term6\DSA\Bookstore\bookstore\app\src\main\java\com\bookstore\service\UserService.java:49: error: cannot find symbol
        return new UserPrincipal(user);
                   ^
  symbol:   class UserPrincipal
  location: class UserService
Note: Some messages have been simplified; recompile with -Xdiags:verbose to get full output
72 errors

> Task :app:compileJava FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:compileJava'.
> Compilation failed; see the compiler error output for details.

* Try:
> Run with --info option to get more log output.
> Run with --scan to get full insights.

BUILD FAILED in 44s
1 actionable task: 1 executed