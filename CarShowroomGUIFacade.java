import java.util.List;
import java.util.Map;
import java.util.Set;

public class CarShowroomGUIFacade {
    private CarShowroomContainer showroomContainer;

    public CarShowroomGUIFacade(CarShowroomContainer showroomContainer){
        this.showroomContainer = showroomContainer;
    }

    public Set<Map.Entry<String, CarShowroom>> getMap(){
        return this.showroomContainer.showrooms.entrySet();
    }

    public CarShowroom createCarShowroom(String name, int cap, String loc){
        return new CarShowroom(name, cap, loc);
    }

    public void addCenter(String name, CarShowroom carShowroom){
        this.showroomContainer.addCenter(name, carShowroom);
    }

    public void removeCenter(String selec){
        this.showroomContainer.removeCenter(selec);
    }

    public void addProduct(String center, String mark, String model, ItemCondition condition, double price, int productionYear, double mileage, double capacity){
        this.showroomContainer.showrooms.get(center).addProduct(new Vehicle(mark, model, condition, price, productionYear, mileage, capacity));
    }

    public void removeVehicle(String center, int index){
        Vehicle del = this.showroomContainer.showrooms.get(center).base.get(index);
        this.showroomContainer.showrooms.get(center).removeProduct(del);
    }

    public List<Map.Entry<String, CarShowroom>> getSortedMap(){
        return this.showroomContainer.sortCentersByLd();
    }

    public CarShowroom getCarShowroom(String Name){
        return this.showroomContainer.showrooms.get(Name);
    }
}
