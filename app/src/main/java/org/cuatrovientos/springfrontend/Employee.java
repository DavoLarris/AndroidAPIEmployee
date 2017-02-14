package org.cuatrovientos.springfrontend;

import java.util.Date;

/**
 * Created by David on 14/02/2017.
 */

public class Employee {
    private Integer id;
    private String name;
    private Date birthDate;
    private String telephone;


    public Employee(){

    }

    /**
     * @param name
     * @param birthDate
     * @param telephone
     */
    public Employee(String name, Date birthDate, String telephone) {
        this.name = name;
        this.birthDate = birthDate;
        this.telephone = telephone;
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

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
