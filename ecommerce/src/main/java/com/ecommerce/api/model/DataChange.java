package com.ecommerce.api.model;

public class DataChange<T> {

// Enum care reprezinta tipul modificarii (INSERT, UPDATE, DELETE)
  private ChangeType changeType;
  
//Data asocitata cu schimbarea
  private T data;

//Constructor default
  public DataChange() {
  }

//Constructor cu parametri pentru a initializa tipul si data schimbarii
  public DataChange(ChangeType changeType, T data) {
    this.changeType = changeType;
    this.data = data;
  }

  //Getter pentru tipul schimbarii
  public ChangeType getChangeType() {
    return changeType;
  }

//Getter pentru data schimbarii
  public T getData() {
    return data;
  }

//Setter pentru setarea datei
  public void setData(T data) {
    this.data = data;
  }

//Enum cu variantele de schimbare
  public enum ChangeType {
    INSERT, //inserarea unei date
    UPDATE, //modificarea unei date existente
    DELETE  //stergerea unei date
  }

}
