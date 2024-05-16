package zork;

public class Item extends OpenableObject {
  private int weight;
  private String name;
  private String description;
  private boolean isOpenable;
  private Inventory inventory;
  

  public Item(int weight, String name, boolean isOpenable, String description) {
    this.weight = weight;
    this.name = name;
    this.isOpenable = isOpenable;
    this.description = description;
    if (isOpenable)
    inventory = new Inventory(100);
  }

  public void open() {
    if (!isOpenable)
      System.out.println("The " + name + " cannot be opened.");

  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isOpenable() {
    return isOpenable;
  }

  public void setOpenable(boolean isOpenable) {
    this.isOpenable = isOpenable;
  }

  public boolean addItem(Item item) {
    if (isOpenable)
      return inventory.addItem(item);
    else 
      return false;
}

public Item removeItem(Item item){
  if(isOpenable)
    return inventory.removeItem(item);
  else
    System.out.println("You cannot take the " + item.name + " from the " + name);
 
  return null;
}
}
