package support;

public class Language {

    private boolean functional;
    private String name;

    public Language(boolean functional, String name) {
        this.functional = functional;
        this.name = name;
    }

    public boolean isFunctional() {
        return functional;
    }

    public void setFunctional(boolean functional) {
        this.functional = functional;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Language)) return false;

        Language language = (Language) o;

        if (functional != language.functional) return false;
        if (name != null ? !name.equals(language.name) : language.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (functional ? 1 : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
