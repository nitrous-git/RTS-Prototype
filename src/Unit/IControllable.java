package Unit;

import Command.CommandContext;
import Command.CommandType;

public interface IControllable {
    boolean isSelected();
    void setSelected(boolean selected);
    void issueCommand(CommandType command, CommandContext ctx);
}
