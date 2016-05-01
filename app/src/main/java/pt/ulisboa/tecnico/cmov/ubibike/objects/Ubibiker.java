package pt.ulisboa.tecnico.cmov.ubibike.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by diogo on 30-04-2016.
 */
public class Ubibiker implements Serializable {
    private String name;
    private String email;
    private Integer points;
    private ArrayList<Trajectory> trajectories;

    public Ubibiker (String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public ArrayList<Trajectory> getTrajectories() {
        return trajectories;
    }

    public void setTrajectories(ArrayList<Trajectory> trajectories) {
        this.trajectories = trajectories;
    }
}
