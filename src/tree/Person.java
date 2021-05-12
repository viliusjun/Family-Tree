package tree;

import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Person {

    public int ID;
    public String name;
    public String surname;
    public LocalDate birthDate;
    public String birthPlace;
    private static int total = 0;

    // ArrayLists that store the IDs of the family members
    public ArrayList<Integer> Parents;
    public ArrayList<Integer> Children;
    public ArrayList<Integer> GrandChildren;
    public ArrayList<Integer> GreatGrandChildren;
    public ArrayList<Integer> Grandparents;
    public ArrayList<Integer> GreatGrandparents;
    public int Spouse; // only one spouse allowed

    public Person(String name, String surname, LocalDate birthDate, String birthPlace) {
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        total++;
        this.ID = total;

        Spouse = 0;
        Parents = new ArrayList<>();
        Children = new ArrayList<>();
        GrandChildren = new ArrayList<>();
        GreatGrandChildren = new ArrayList<>();
        Grandparents = new ArrayList<>();
        GreatGrandparents = new ArrayList<>();
    }


    public static void setTotal(int newTotal) {
        total = newTotal;
    }

    // method that adds an ID to the given arrayList
    public void addRelation(ArrayList<Integer> Relationship, int ID) {
        Relationship.add(ID);
    }

    abstract void editNode();

}
