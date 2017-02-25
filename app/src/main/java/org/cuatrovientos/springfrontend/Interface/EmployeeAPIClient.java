package org.cuatrovientos.springfrontend.Interface;

import org.cuatrovientos.springfrontend.Model.Employee;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by David on 18/02/2017.
 */

public interface EmployeeAPIClient {

    @Headers("Accept: application/json")
    @GET("/springEmployDepart/api")
    Call<List<Employee>> employees ();

    @Headers("Accept: application/json")
    @GET("/springEmployDepart/api/{id}")
    Call<Employee> employee (
            @Path("id") int id);

    @Headers("Accept: application/json")
    @POST("/springEmployDepart/api/new")
    Call<Integer> create (@Body Employee employee);

    @PUT("/springEmployDepart/api/update/{id}") //Fix this on eclipse
    Call<Void> update (@Body Employee employee, @Path("id") Integer id);

    @DELETE("/springEmployDepart/api/delete/{id}")
    Call<Void> delete (
            @Path("id") int id);


}
