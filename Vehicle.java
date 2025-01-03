public class  Vehicle implements Comparable<Vehicle> {
    public String mark;
    public String model;
    public ItemCondition state;
    public double price;
    public int productionYear;
    public double mileage;
    public double capacity;
    public int amount;

    public Vehicle(String marka, String mod, ItemCondition st, double cena, int rok, double przebieg, double pojemnosc){
        this.mark = marka;
        this.model = mod;
        this.state = st;
        this.price = cena;
        this.productionYear = rok;
        this.mileage = przebieg;
        this.capacity = pojemnosc;
        this.amount = 1;
    }

    public Vehicle(String mod){
        this.model = mod;
    }

    public void print(){
        System.out.println("Samochód marki " + this.mark +", model " + this.model + ", stan "+this.state+", cena "+ this.price +", rok produkcji "+ this.productionYear + ", przebieg " +this.mileage+ ", pojemnosc "+ this.capacity + " ilosc " + this.amount);

    }

    public String getModel(){
        return this.model;
    }

    public int getAmount(){
        return this.amount;
    }

    @Override
    public int compareTo(Vehicle other){
        return this.model.compareTo(other.model);
    }
}
