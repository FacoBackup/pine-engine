package com.pine.panels.metrics;

import com.pine.MetricCollector;
import com.pine.core.dock.AbstractDockPanel;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiTableColumnFlags;

import java.util.Map;

import static com.pine.panels.console.ConsolePanel.TABLE_FLAGS;


public class MetricsPanel extends AbstractDockPanel {

    private static final ImVec4 HIGH_COST_COLORED = new ImVec4(1.f, .1f, 0.f, 1);
    private static final ImVec4 MEDIUM_COST_COLORED = new ImVec4(.2f, 1f, 0f, 1);
    private static final long MEDIUM_COST = 5;
    private static final long HIGH_COST = 10;

    @Override
    public void render() {
        Map<String, Long> metrics = MetricCollector.getMetrics();

        if (ImGui.beginTable("##metrics" + imguiId, 2, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Time executing", ImGuiTableColumnFlags.WidthFixed, 120f);
            ImGui.tableHeadersRow();

            for (Map.Entry<String, Long> entry : metrics.entrySet()) {
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.text(entry.getKey());
                ImGui.tableNextColumn();
                if (entry.getValue() >= HIGH_COST) {
                    ImGui.textColored(HIGH_COST_COLORED, entry.getValue() + "ms");
                } else if (entry.getValue() >= MEDIUM_COST) {
                    ImGui.textColored(MEDIUM_COST_COLORED, entry.getValue() + "ms");
                } else {
                    ImGui.text(entry.getValue() + "ms");
                }
            }
            ImGui.endTable();
        }
    }
}

