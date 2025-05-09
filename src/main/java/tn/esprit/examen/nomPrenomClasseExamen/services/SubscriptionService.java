package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Subscription;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SubscriptionRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class SubscriptionService implements ISubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SimpleUserService simpleUserService;

    @Override
    public Subscription createSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    @Override
    public Optional<Subscription> getSubscriptionById(Integer id) {
        return subscriptionRepository.findById(id);
    }

    @Override
    public Subscription updateSubscription(Integer id, Subscription subscription) {
        if (subscriptionRepository.existsById(id)) {
            subscription.setSubscriptionId(id);
            return subscriptionRepository.save(subscription);
        }
        return null; // You could throw an exception if needed
    }

    @Override
    public boolean deleteSubscription(Integer id) {
        if (subscriptionRepository.existsById(id)) {
            subscriptionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Method to check subscriptions and unsubscribe users if the duration has expired
    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void checkAndUnsubscribeUsers() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        LocalDate currentDate = LocalDate.now();

        for (Subscription subscription : subscriptions) {
            // Check each user’s subscription start date and expiry
            for (SimpleUser user : subscription.getSimpleUsers()) {
                if (user.getSubscriptionStartDate() != null) {
                    // Calculate the expiration date by adding the subscription duration to the user's start date
                    LocalDate expirationDate = user.getSubscriptionStartDate().plusMonths(subscription.getDurationInMonths());

                    // Check if the current date is after the expiration date
                    if (currentDate.isAfter(expirationDate)) {
                        // Unsubscribe the user
                        user.setSubscription(null); // Remove the subscription from the user
                        simpleUserService.save(user); // Save the updated user to the database
                        log.info("User ID " + user.getUserId() + " unsubscribed due to expiration.");
                    }
                }
            }
        }
    }


    // Method to fetch statistics for subscriptions
    public Map<String, Integer> getSubscriptionStatistics() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        Map<String, Integer> statistics = new HashMap<>();

        for (Subscription subscription : subscriptions) {
            statistics.put(subscription.getSubscriptionType().toString(), subscription.getSimpleUsers().size());
        }
        return statistics;
    }

    public Subscription subscribeUserWithDiscount(Integer userId, Integer subscriptionId) {
        Optional<SimpleUser> user = simpleUserService.getUserById(userId);
        Optional<Subscription> subscription = subscriptionRepository.findById(subscriptionId);

        if (user.isPresent() && subscription.isPresent()) {
            SimpleUser existingUser = user.get();
            Subscription selectedSubscription = subscription.get();

            // Get the original price (unchanged for all users)
            float originalPrice = selectedSubscription.getSubscriptionPrice();

            // Calculate discount based on user's activity level
            double discount = simpleUserService.calculateDiscountBasedOnActivity(existingUser);
            float discountedPrice = (float) (originalPrice * (1 - discount));

            // Set the discounted price just for the active user; don't change the subscription price for everyone
            existingUser.setDiscountedPrice(discountedPrice);

            // Save the user with the updated discounted price
            simpleUserService.save(existingUser);

            // Link the subscription to the user
            simpleUserService.addSubscriptionToUser(userId, subscriptionId);

            // Prepare the response with both original and discounted prices
            Map<String, Object> response = new HashMap<>();
            response.put("subscription", selectedSubscription);   // Include subscription details
            response.put("originalPrice", originalPrice);        // Include original price
            response.put("discountedPrice", discountedPrice);    // Include discounted price

            return selectedSubscription;  // Return the subscription with price details
        }

        return null;  // Handle when user or subscription is not found
    }


    public boolean unsubscribeUser(Integer userId) {
        Optional<SimpleUser> user = simpleUserService.getUserById(userId);
        if (user.isPresent()) {
            SimpleUser existingUser = user.get();
            existingUser.setSubscription(null);  // Remove subscription
            simpleUserService.save(existingUser);  // Save the user with the updated subscription field
            return true;
        }
        return false;
    }


}