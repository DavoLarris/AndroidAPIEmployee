package org.cuatrovientos.springfrontend.Interface;

import org.cuatrovientos.springfrontend.Model.Employee;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by David on 19/02/2017.
 */

public class EmployeeManager {
    private static final String URLAPI = "myemployee.ddns.net:8080/springEmployDepart";

    private EmployeeAPIClient employeeAPIClient;

    /**
     * Constructor, inits employeeApiClient
     */
    public EmployeeManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLAPI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        employeeAPIClient = retrofit.create(EmployeeAPIClient.class);
    }

    /**
     * uses retrofit API client to get employees
     */
    public List<Employee> getEmployees() {
        Call<List<Employee>> employeesApiCall = employeeAPIClient.employees();
        List<Employee> employees = null;

        try {
            employees = employeesApiCall.execute().body();

        } catch (IOException e) {
            System.err.println("Error calling employees API");
            e.printStackTrace();
        }catch (Exception e) {
            System.err.println("Error " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }



    /**
     * uses retrofit API client to get one employee by id
     * @param id
     * @return
     */
    public Employee getEmployee(int id) {
        Call<Employee> employeeApiCall = employeeAPIClient.employee(id);
        Employee employee = null;

        try {
            employee = employeeApiCall.execute().body();
        } catch (IOException e) {
            System.err.println("Error calling employee API");
            e.printStackTrace();
        }

        return employee;
    }

    /**
     * uses retrofit API client to create a new employee
     * @param employee
     * @return
     */
    public Integer createEmployee(Employee employee) {
        Call<Void> employeeApiCall = employeeAPIClient.create(employee);
        Integer result = null;

        try {
            result = employeeApiCall.execute().body();
        } catch (IOException e) {
            System.err.println("Error calling employee API");
            e.printStackTrace();
        }

        return result.intValue();
    }

    /**
     * uses retrofit API client to update an employee
     * @param employee
     * @return
     */
    public boolean updateEmployee(Employee employee, Long id) {
        Call<Void> employeeApiCall = employeeAPIClient.update(employee, id);
        boolean result = false;

        try {
            result = employeeApiCall.execute().isSuccessful();
        } catch (IOException e) {
            System.err.println("Error calling employee API");
            e.printStackTrace();
        }

        return result;
    }

    /**
     * uses retrofit API client to delete employee by id
     * @param id
     * @return
     */
    public boolean deleteEmployee(int id) {
        Call<Void> employeeApiCall = employeeAPIClient.delete(id);
        boolean result = false;

        try {
            result = employeeApiCall.execute().isSuccessful();
        } catch (IOException e) {
            System.err.println("Error calling employee API");
            e.printStackTrace();
        }

        return result;
    }
}
