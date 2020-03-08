package ca.cmpt276.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Singleton class manages the list of Restaurants
 */

public class RestaurantManager implements Iterable<Restaurant> {

    private List<Restaurant> restaurants = new ArrayList<>();

    private static RestaurantManager instance;

    private RestaurantManager(){

    }

    public static RestaurantManager getInstance(){
        if(instance == null){
            instance = new RestaurantManager();
        }
        return instance;
    }

    public Restaurant retrieve(int index){
        return restaurants.get(index);
    }

    public void addRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
    }

    @Override
    public Iterator<Restaurant> iterator(){
        return restaurants.iterator();
    }


}
