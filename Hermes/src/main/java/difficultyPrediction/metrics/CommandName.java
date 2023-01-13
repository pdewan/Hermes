package difficultyPrediction.metrics;

import fluorite.commands.LocalCheckRawCommand;

public enum CommandName {
	BreakPointCommand,
	ExceptionCommand,
	RunCommand,
	BuildStartEvent, // not mapped currently
	ConsoleOutput, // not currently mapped
	ConsoleInput, // not currently mapped
	CompileWarning,
	CompileError,
	CopyCommand,
	Insert,
	InsertStringCommand,
	PasteCommand,
	RedoCommand,
	SelectTextCommand,
	CutCommand,
	Replace,
	PauseCommand,
	LocalChecksRawCommand,
	FileOpenCommand,
	FindCommand,
	ShellCommand,
	Delete,
	UndoCommand,
	MoveCaretCommand,
	Exception,
	debug,
	edit,// this must be after delete and maybe even view, but Jason's scheme has it wrong, making order irrelevant
	delete,
	view,
	//For the google docs plugin
	DocumentInsertCommand, 
	DocumentLargeInsertCommand,
	DocumentLargeDeleteCommand,
	DocumentBoldCommand, 
	DocumentHighlightCommand, 
	DocumentItalicizeCommand, 
	DocumentUnderlineCommand, 
	DocumentDeleteCommand, 
	DocumentCollaborationCommand, 
	DocumentSpellcheckCommand, 
	DocumentUpdateURLCommand, 
	DocumentCreateNewTabCommand, 
	DocumentSwitchTabsCommand, 
	DocumentScrollCommand,
	DocumentWindowFocusCommand,
	ADocumentCursorMoveCommand
}
