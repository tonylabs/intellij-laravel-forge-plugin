package com.tonylabs.forge.ui

import com.intellij.openapi.project.Project
import com.tonylabs.forge.model.Server
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.*

class ServerCellRenderer : DefaultListCellRenderer() {
    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        if (value is Server) {
            text = value.name
        }
        return this
    }
}

class ServerListContent(project: Project, servers: List<Server>) : JPanel(BorderLayout()) {
    init {
        val serverListModel = DefaultListModel<Server>()
        servers.forEach { server ->
            serverListModel.addElement(server)
        }
        val serverList = JList(serverListModel).apply {
            cellRenderer = ServerCellRenderer()
        }
        val scrollPane = JScrollPane(serverList)
        this.add(scrollPane, BorderLayout.CENTER) // Add the scrollPane to the panel
    }
}
