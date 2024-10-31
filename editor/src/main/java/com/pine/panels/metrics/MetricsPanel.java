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
    private static final long MEDIUM_COST = 500000;
    private static final long HIGH_COST = 1000000;

    @Override
    public void render() {
        Map<String, Long> metrics = MetricCollector.getMetrics();
        MetricCollector.shouldCollect = true;
        if (ImGui.beginTable("##metrics" + imguiId, 2, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Time executing", ImGuiTableColumnFlags.WidthFixed, 120f);
            ImGui.tableHeadersRow();

            for (Map.Entry<String, Long> entry : metrics.entrySet()) {
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.text(entry.getKey());
                ImGui.tableNextColumn();
                String cost;

                if(entry.getValue() >= 1_000_000){
                    cost = (entry.getValue() / 1_000_000)+ "ms";
                }else{
                     cost = entry.getValue() + "ns";
                }

                if (entry.getValue() >= HIGH_COST) {
                    ImGui.textColored(HIGH_COST_COLORED, cost);
                } else if (entry.getValue() >= MEDIUM_COST) {
                    ImGui.textColored(MEDIUM_COST_COLORED, cost);
                } else {
                    ImGui.text(cost);
                }
            }
            ImGui.endTable();
        }
    }
}

