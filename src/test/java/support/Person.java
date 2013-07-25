package support;

public class Person {

    private String firstName;
    private int salary;
    private int age;

    public Person(String firstName, int salary, int age) {
        this.firstName = firstName;
        this.salary = salary;
        this.age = age;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        if (age != person.age) return false;
        if (salary != person.salary) return false;
        if (firstName != null ? !firstName.equals(person.firstName) : person.firstName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + salary;
        result = 31 * result + age;
        return result;
    }
}
