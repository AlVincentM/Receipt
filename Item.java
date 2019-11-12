public class Item {
  
  private String itemName;
  private double  itemPrice;
  private int itemQty;
  private int itemCode;

  Item() {
    
  }

  Item(int itemCode, String itemName, double itemPrice) {
    this.itemCode = itemCode;
    this.itemName = itemName;
    this.itemPrice = itemPrice;
  }

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public double getItemPrice() {
    return itemPrice;
  }

  public void setItemPrice(double itemPrice) {
    this.itemPrice = itemPrice;
  }

  public int getItemQty() {
    return itemQty;
  }

  public void setItemQty(int itemQty) {
    this.itemQty = itemQty;
  }

  public int getItemCode() {
    return itemCode;
  }

  public void setItemCode(int itemCode) {
    this.itemCode = itemCode;
  }
}
