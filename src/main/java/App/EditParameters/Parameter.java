package App.EditParameters;

import java.util.Vector;

public class Parameter {
    public ParameterType type;
    public String name;
    public String description;
    public Object value;

    public Parameter(ParameterType type, String name, String description, Object value) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public Object[] getTableEntries() {
        return new Object[] {type.name().toLowerCase(), name, description, value};
    }
}
