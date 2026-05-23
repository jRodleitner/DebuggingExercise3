
package deltadebugger;

public class Error {

    private final String errorMessage;

    public Error(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Error)) {
            return false;
        }

        Error other = (Error) obj;

        return errorMessage.equals(other.errorMessage);
    }

    @Override
    public String toString() {
        return errorMessage;
    }
}

