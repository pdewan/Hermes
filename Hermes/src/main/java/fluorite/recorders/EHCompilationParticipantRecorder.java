package fluorite.recorders;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.compiler.ReconcileContext;

import fluorite.commands.EHCompilationCommand;


public class EHCompilationParticipantRecorder extends CompilationParticipant {

	private final int AST_LEVEL_THREE = 3;
	private final int AST_LEVEL_FOUR = 4;
	public static final int MAX_COMPILE_ERRORS = 15; // do not overwhelm the system for large workspaces
	public static final int MAX_COMPILE_WARNINGS = 10; // do not overwhelm the system for large workspaces

	private static EHCompilationRecorder compilationRecorder = EHCompilationRecorder.getInstance();
	
	@Override
	public void reconcile(ReconcileContext context) 
	{
		IProblem[] problems= null;
		switch(context.getASTLevel())
		{

		case AST_LEVEL_THREE:
			try 
			{
				problems = context.getAST3().getProblems();
			} catch (JavaModelException e) 
			{
				e.printStackTrace();
			}
			break;
		case AST_LEVEL_FOUR:
			 try
			 {
				 problems = context.getAST4().getProblems();
			 } catch (JavaModelException e)
			 {
				 e.printStackTrace();
			 }
			 break;
			default:
				try 
				{
					problems = context.getAST3().getProblems();
				} catch (JavaModelException e) 
				{
					e.printStackTrace();
				}
				break;
		}
		
		if(problems != null)
		{
			if(problems.length > 0)
			{
				int numWarnings = 0;
				int numErrors = 0;
				for(int i = 0; i < problems.length && (numWarnings < MAX_COMPILE_WARNINGS || numErrors < MAX_COMPILE_ERRORS) ; i++)
				{
					IProblem problem = problems[i];
					if (problem.isWarning()) {
						numWarnings++;
						if (numWarnings > MAX_COMPILE_WARNINGS) {
							continue;
						}
					} else if (problem.isError()) {
						numErrors++;
						if (numErrors > MAX_COMPILE_ERRORS) {
							continue;
						}
					}
					EHCompilationCommand command = createCommand(problems[i]);
					compilationRecorder.record(command);
				}
			}
		}
	}
	
	private EHCompilationCommand createCommand (IProblem problem)
	{
		EHCompilationCommand command = null;
		String fileName = new String(problem.getOriginatingFileName());
		if(problem.isError())
		{
			command = new EHCompilationCommand(false, problem.getMessage(),String.valueOf(problem.getID()), String.valueOf(problem.getSourceLineNumber()),
					String.valueOf(problem.getSourceStart()), String.valueOf(problem.getSourceEnd()), fileName);
		}
		else if(problem.isWarning())
		{
			command = new EHCompilationCommand(true, problem.getMessage(),String.valueOf(problem.getID()), String.valueOf(problem.getSourceLineNumber()),
					String.valueOf(problem.getSourceStart()), String.valueOf(problem.getSourceEnd()), fileName);
		}

		System.out.println("Message: " + problem.getMessage());
		System.out.println("Source Line #: " + problem.getSourceLineNumber());
		System.out.println("Source Start: " + problem.getSourceStart());
		System.out.println("Source End: " + problem.getSourceEnd());
		System.out.println("File name: " + problem.getOriginatingFileName().toString());
		System.out.println("Problem Id: " + problem.getID());
		return command;
	}
	
	@Override
	public boolean isActive(IJavaProject project)
	{
		return true;
	}
	
	@Override
	public int aboutToBuild(IJavaProject project) 
	{
		return CompilationParticipant.READY_FOR_BUILD;
	}
	
	@Override
	public void buildFinished(IJavaProject project) 
	{
		//upload files
//		 ProjectUploader projectUploader = new ProjectUploader();
//		 new Thread(projectUploader).start();
	}

}
