package me.neznamy.tab.shared.proxy.message.outgoing;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.AllArgsConstructor;
import me.neznamy.tab.shared.platform.Scoreboard;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class SetObjective implements OutgoingMessage {

    private String objectiveName;
    private Scoreboard.ObjectiveAction action;
    private String title;
    private Scoreboard.HealthDisplay display;
    private String numberFormat;

    public SetObjective(String objectiveName) {
        this.objectiveName = objectiveName;
        this.action = Scoreboard.ObjectiveAction.UNREGISTER;
    }

    @Override
    @NotNull
    public ByteArrayDataOutput write() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PacketPlayOutScoreboardObjective");
        out.writeUTF(objectiveName);
        out.writeInt(action.ordinal());
        if (action == Scoreboard.ObjectiveAction.REGISTER || action == Scoreboard.ObjectiveAction.UPDATE) {
            out.writeUTF(title);
            out.writeInt(display.ordinal());
            out.writeBoolean(numberFormat != null);
            if (numberFormat != null) out.writeUTF(numberFormat);
        }
        return out;
    }
}
