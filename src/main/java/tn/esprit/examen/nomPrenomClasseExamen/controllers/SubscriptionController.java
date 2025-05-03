package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Subscription;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SubscriptionRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.SimpleUserService;
import tn.esprit.examen.nomPrenomClasseExamen.services.SubscriptionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@RequestMapping("/subscription")
@RestController
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;

    // Create a subscription
    @PostMapping("/createSubscription")
    public ResponseEntity<Object> createSubscription(@Valid @RequestBody Subscription subscription, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Return errors as a response if validation fails
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return new ResponseEntity<>(subscriptionService.createSubscription(subscription), HttpStatus.CREATED);
    }

    @GetMapping("/getAllSubscriptions")
    public ResponseEntity<List<Map<String, Object>>> getAllSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();

        // Stream through the subscriptions and build a response map
        List<Map<String, Object>> responseList = subscriptions.stream()
                .map(subscription -> {
                    Map<String, Object> responseMap = new HashMap<>();
                    // Add subscription details
                    responseMap.put("subscriptionId", subscription.getSubscriptionId());
                    responseMap.put("subscriptionType", subscription.getSubscriptionType());
                    responseMap.put("subscriptionPrice", subscription.getSubscriptionPrice());

                    // Initialize discountedPrice to the original price
                    float discountedPrice = subscription.getSubscriptionPrice();  // Default to original price

                    // Check if there are users linked to this subscription
                    if (subscription.getSimpleUsers() != null && !subscription.getSimpleUsers().isEmpty()) {
                        // Fetch the first user (assuming only one user for simplicity; extend if needed)
                        SimpleUser user = subscription.getSimpleUsers().iterator().next();

                        // If the user has a discountedPrice, use it
                        if (user.getDiscountedPrice() != null) {
                            discountedPrice = user.getDiscountedPrice();  // Use the user's discounted price
                        }
                    }

                    // Add the discounted price to the response map
                    responseMap.put("discountedPrice", discountedPrice);

                    // Include additional subscription details
                    responseMap.put("subscriptionDescription", subscription.getSubscriptionDescription());
                    responseMap.put("durationInMonths", subscription.getDurationInMonths());

                    return responseMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);  // Return the list of subscriptions with original and discounted prices
    }



    @GetMapping("/getSubscription/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable Integer id) {
        Optional<Subscription> subscription = subscriptionService.getSubscriptionById(id);
        return subscription.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/updateSubscription/{id}")
    public ResponseEntity<Subscription> updateSubscription(@PathVariable Integer id, @RequestBody Subscription subscription) {
        Subscription updatedSubscription = subscriptionService.updateSubscription(id, subscription);
        return updatedSubscription != null ? ResponseEntity.ok(updatedSubscription) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/deleteSubscription/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Integer id) {
        return subscriptionService.deleteSubscription(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    // Inject the SimpleUserService to update the user subscription
    private final SimpleUserService simpleUserService;

    @PostMapping("/subscribeToSubscription/{userId}/{subscriptionId}")
    public ResponseEntity<?> subscribeUser(@PathVariable Integer userId, @PathVariable Integer subscriptionId){
        try {
            // Fetch user and subscription
            Optional<SimpleUser> user = simpleUserService.getUserById(userId);
            Optional<Subscription> subscription = subscriptionService.getSubscriptionById(subscriptionId);

            if (user.isPresent() && subscription.isPresent()) {
                // Update the user with the subscription
                simpleUserService.addSubscriptionToUser(userId, subscriptionId);
                return ResponseEntity.ok(Map.of("message", "User subscribed successfully!"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Subscription not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error subscribing user.");
        }
    }

    @PostMapping("/triggerUnsubscribe")
    public ResponseEntity<String> triggerUnsubscribe() {
        subscriptionService.checkAndUnsubscribeUsers();  // Manually trigger the scheduled task
        return ResponseEntity.ok("Unsubscription check completed!");
    }

    @GetMapping("/getSubscriptionForUser/{userId}")
    public ResponseEntity<Subscription> getSubscriptionForUser(@PathVariable Integer userId) {
        Optional<SimpleUser> user = simpleUserService.getUserById(userId);
        if (user.isPresent() && user.get().getSubscription() != null) {
            return ResponseEntity.ok(user.get().getSubscription());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/subscribeWithDiscount/{userId}/{subscriptionId}")
    public ResponseEntity<?> subscribeUserWithDiscount(@PathVariable Integer userId, @PathVariable Integer subscriptionId) {
        try {
            Optional<SimpleUser> user = simpleUserService.getUserById(userId);
            Optional<Subscription> subscription = subscriptionRepository.findById(subscriptionId);

            if (user.isPresent() && subscription.isPresent()) {
                // Apply discount and subscribe the user
                Subscription updatedSubscription = subscriptionService.subscribeUserWithDiscount(userId, subscriptionId);

                // Build response with the original and discounted price
                Map<String, Object> response = new HashMap<>();
                response.put("message", "User subscribed with discount");
                response.put("subscription", updatedSubscription);
                response.put("originalPrice", updatedSubscription.getSubscriptionPrice());
                response.put("discountedPrice", user.get().getDiscountedPrice());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Subscription not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error subscribing user with discount.");
        }
    }



    @PostMapping("/unsubscribe/{userId}")
    public ResponseEntity<?> unsubscribeUser(@PathVariable Integer userId) {
        try {
            boolean isUnsubscribed = subscriptionService.unsubscribeUser(userId);
            if (isUnsubscribed) {
                return ResponseEntity.ok(Map.of("message", "User unsubscribed successfully!"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error unsubscribing user.");
        }
    }


}