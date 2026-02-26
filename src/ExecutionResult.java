import java.util.List;
import java.util.Objects;

public class ExecutionResult {

    private final List<ExecutedLine> executedLines;
    private final boolean bagIsEmpty;

    public ExecutionResult (List<ExecutedLine> executedLines, boolean bagIsEmpty) {
        this.executedLines = List.copyOf(Objects.requireNonNull(executedLines, "executedLines must not be null"));
        this.bagIsEmpty = bagIsEmpty;
    }

    public List<ExecutedLine> getExecutedLines() {
        return executedLines;
    }

    public boolean isBagEmpty() {
        return bagIsEmpty;
    }
}
