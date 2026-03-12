package com.casy.casyaiagent.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TerminalOperationToolTest {

    @Test
    public void testExecuteTerminalCommand() {
        TerminalOperationTool tool = new TerminalOperationTool();
        String command = "dir";
        String result = tool.executeTerminalCommand(command);
        assertNotNull(result);
    }
}
