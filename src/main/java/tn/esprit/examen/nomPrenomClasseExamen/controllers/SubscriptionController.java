package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Subscription;
import tn.esprit.examen.nomPrenomClasseExamen.services.SimpleUserService;
import tn.esprit.examen.nomPrenomClasseExamen.services.SubscriptionService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@RequestMapping("/subscription")
@RestController
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

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
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptions);  // This should return a valid JSON array
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



}
