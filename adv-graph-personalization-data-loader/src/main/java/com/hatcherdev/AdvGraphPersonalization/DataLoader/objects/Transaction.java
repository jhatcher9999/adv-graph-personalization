package com.hatcherdev.AdvGraphPersonalization.DataLoader.objects;

public class Transaction {

    private Long _chainID;
    private Long _companyID;
    private Long _brandID;
    private Double _productSize;
    private String _productMeasure;
    private Long _categoryID;
    private Long _departmentID;
    private Long _customerID;
    private Long _date;
    private Integer _quantity;
    private Double _amount;

    public Transaction(){

    }

    public Transaction(
            Long chainID,
            Long companyID,
            Long brandID,
            Double productSize,
            String productMeasure,
            Long categoryID,
            Long departmentID,
            Long customerID,
            Long date,
            Integer quantity,
            Double amount
    ){
        _chainID = chainID;
        _companyID = companyID;
        _brandID = brandID;
        _productSize = productSize;
        _productMeasure = productMeasure;
        _categoryID = categoryID;
        _departmentID = departmentID;
        _customerID = customerID;
        _date = date;
        _quantity = quantity;
        _amount = amount;
    }

    public Long getChainID() {
        return _chainID;
    }

    public Long getCompanyID() {
        return _companyID;
    }

    public Long getBrandID() {
        return _brandID;
    }

    public Double getProductSize() {
        return _productSize;
    }

    public String getProductMeasure() {
        return _productMeasure;
    }

    public Long getCategoryID() {
        return _categoryID;
    }

    public Long getDepartmentID() {
        return _departmentID;
    }

    public Long getCustomerID() {
        return _customerID;
    }

    public Long getDate() {
        return _date;
    }

    public Integer getQuantity() {
        return _quantity;
    }

    public Double getAmount() {
        return _amount;
    }

}
