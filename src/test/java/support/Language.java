package support;

public class Language {

    private boolean FunctionaL;
    private String name;

    public Language(boolean functional, String name) {
        this.FunctionaL = functional;
        this.name = name;
    }

    public boolean isFunctional() {
        return FunctionaL;
    }

    public void setFunctional(boolean functional) {
        this.FunctionaL = functional;
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

        if (FunctionaL != language.FunctionaL) return false;
        if (name != null ? !name.equals(language.name) : language.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (FunctionaL ? 1 : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
