package org.nemwiz.jiracommitmessage.services

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Document
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.ui.CommitMessage
import com.intellij.openapi.vcs.ui.Refreshable
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.mockk.*
import org.nemwiz.jiracommitmessage.action.PluginAction
import org.nemwiz.jiracommitmessage.configuration.PluginSettingsState

class PluginActionTest : BasePlatformTestCase() {

    fun testAppendsJiraIssueToExistingCommitMessageWhenThisOptionIsEnabled() {

        val commitPanelMock = CommitMessage(project)
        val refreshablePanelMock = mockkClass(Refreshable::class)
        val dataContext = mockk<DataContext>()
        val actionEventMock = AnActionEvent.createFromAnAction(PluginAction(), null, "test", dataContext)
        val documentMock = mockkClass(Document::class)

        val existingCommitMessage = "This message was already shown in the panel"
        every { documentMock.text } returns existingCommitMessage

        every { Refreshable.PANEL_KEY.getData(actionEventMock.dataContext) } returns refreshablePanelMock
        every { VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(actionEventMock.dataContext) } returns commitPanelMock
        every { actionEventMock.dataContext.getData(VcsDataKeys.COMMIT_MESSAGE_DOCUMENT) } returns documentMock

        com.intellij.openapi.util.Disposer.register(testRootDisposable, commitPanelMock)

        PluginSettingsState.instance.state.isPrependJiraIssueOnActionClick = true

        val newCommitMessage = "(PROJ-456):"

        val pluginAction = PluginAction()

        pluginAction.setCommitMessage(actionEventMock, newCommitMessage)

        assertEquals(newCommitMessage + existingCommitMessage, commitPanelMock.comment)
    }

    fun testDoesNotAppendJiraIssueToExistingCommitMessageWhenThisOptionIsDisabled() {

        val commitPanelMock = CommitMessage(project)
        val refreshablePanelMock = mockkClass(Refreshable::class)
        val dataContext = mockk<DataContext>()
        val actionEventMock = AnActionEvent.createFromAnAction(PluginAction(), null, "test", dataContext)

        every { Refreshable.PANEL_KEY.getData(actionEventMock.dataContext) } returns refreshablePanelMock
        every { VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(actionEventMock.dataContext) } returns commitPanelMock

        com.intellij.openapi.util.Disposer.register(testRootDisposable, commitPanelMock)

        PluginSettingsState.instance.state.isPrependJiraIssueOnActionClick = false
        val newCommitMessage = "(PROJ-456):"

        val pluginAction = PluginAction()

        pluginAction.setCommitMessage(actionEventMock, newCommitMessage)

        assertEquals(newCommitMessage, commitPanelMock.comment)
    }

}