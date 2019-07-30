package difficultyPrediction.metrics;

import java.util.List;

/**
 * As the name indicacted, we associate each command with a vatgeory
 * that seems dynamically settable
 * @author dewan
 *
 */
public interface CategorizedCommand {

	public abstract CommandName getCommand();


	public abstract List<CommandCategory> getCategories();

	public abstract void setCategories(List<CommandCategory> newVal);

}