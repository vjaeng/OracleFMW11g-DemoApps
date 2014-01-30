package dataprovider.dto;

public class Item {
    public Item() {
        super();
    }
    private String _product;
    private Integer _quantity;
    private Double _unitPrice;

    public void setProduct(String _product) {
        this._product = _product;
    }

    public String getProduct() {
        return _product;
    }

    public void setQuantity(Integer _quantity) {
        this._quantity = _quantity;
    }

    public Integer getQuantity() {
        return _quantity;
    }

    public void setUnitPrice(Double _unitPrice) {
        this._unitPrice = _unitPrice;
    }

    public Double getUnitPrice() {
        return _unitPrice;
    }
}
