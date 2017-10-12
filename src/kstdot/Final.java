package kstdot;


public class Final {
    private String SKU;
    private String Tdot;
    private String Cost;
    private String Jobber;
    private String Stock;
    private String Price;
    private String MSRP;
    private String Inventory;

    public Final(){
        SKU=null;
        Tdot=null;
        Cost=null;
        Jobber=null;
        Stock=null;
        Price=null;
        MSRP=null;
        Inventory=null;
    }
    public void setSKU(String refSKU){
        SKU = refSKU;
    }
    public void setTdot(String refTdot){
        Tdot = refTdot;
    }
    public void setCost(String refCost){
        Cost = refCost;
    }
    public void setJobber(String refJobber){
        Jobber = refJobber;
    }
    public void setStock(String refStock){
        Stock = refStock;
    }
    public void setPrice(String refPrice){
        Price = refPrice;
    }
    public void setMSRP(String refMSRP){
        MSRP = refMSRP;
    }
    public void setInvet(String refInventory){
        Inventory = refInventory;
    }
    public boolean isNULL(String refStr){
        if(refStr.equals("SKU") && SKU == null || refStr.equals("Tdot") && Tdot == null || refStr.equals("Cost") && Cost == null || refStr.equals("Jobber") && Jobber == null 
                || refStr.equals("Stock") && Stock.equals("") || refStr.equals("Price") && Price == null || refStr.equals("MSRP") && MSRP == null)
            return true;
        else
            return false;     
    }
    public String getJobber(){
        return Jobber;
    }
    public String getCost(){
        return Cost;
    }
    public String getTdot(){
        return Tdot;
    }
    public String getPrice(){
        return Price;
    }
    public String getSKU(){
        return SKU;
    }
    public boolean checkStock() throws NullPointerException{
        if(Stock != null){
            if(Stock.contains("Special Order Item - Call to order") || Stock.contains("Discontinued") || Stock.contains("N/A"))
                return false;
            else
                return true;
        }
        else
            return false;
    }

    public String _dataText() {
        return SKU + ";" + Tdot + ";" + Cost + ";" + Jobber + ";" + Stock + ";" + Price + ";" + MSRP + ";" + Inventory;
    }
}
