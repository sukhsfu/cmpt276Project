package ca.cmpt276.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Singleton class manages the list of Restaurants
 */


class SortRestaurant implements Comparator<Restaurant>
{
    public int compare(Restaurant a, Restaurant b){
        return (a.getName().compareTo(b.getName()));
    }
}

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

    public int getIndex(Restaurant restaurant){
        return restaurants.indexOf(restaurant);
    }

    public void addRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
    }

    public void clearRestaurants() {
        restaurants.clear();
    }

    public void sortRestaurantList(){
        Collections.sort(restaurants,new SortRestaurant());
    }



    @Override
    public Iterator<Restaurant> iterator(){
        return restaurants.iterator();
    }


}
