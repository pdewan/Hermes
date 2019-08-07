package difficultyPrediction.metrics;

import java.util.Set;

/**
 * As the name indicacted, we associate each command with a vatgeory
 * that seems dynamically settable
 * @author dewan
 *
 */
public interface CategorizedCommand {

	public abstract CommandName getCommand();


	public abstract Set<CommandCategory> getCategories();

	public abstract void setCategories(Set<CommandCategory> newVal);

}