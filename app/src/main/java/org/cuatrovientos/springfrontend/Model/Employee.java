package org.cuatrovientos.springfrontend.Model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by David on 14/02/2017.
 */


public class Employee implements Serializable{
    private Integer id;
    private String name;
    private String birthDate;
    private String telephone;
    private Integer idBackend;

    public Employee(){

    }

    /**
     * @param name
     * @param birthDate
     * @param telephone
     */
    public Employee(String name, String birthDate, String telephone, Integer idBackend) {
        this.name = name;
        this.birthDate = birthDate;
        this.telephone = telephone;
        this.idBackend = idBackend;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Integer getIdBackend() {
        return idBackend;
    }

    public void setIdBackend(Integer idBackend) {
        this.idBackend = idBackend;
    }
}
