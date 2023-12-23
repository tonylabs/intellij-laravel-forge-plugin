package com.tonylabs.forge

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.tonylabs.forge.ui.TokenWindowContent

class TokenWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Use the non-deprecated method to get an instance of ContentFactory
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(TokenWindowContent(project), "", false)
        toolWindow.contentManager.addContent(content)
    }
}