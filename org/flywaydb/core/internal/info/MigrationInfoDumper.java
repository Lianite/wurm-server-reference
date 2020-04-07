// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.info;

import org.flywaydb.core.internal.util.DateUtils;
import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.api.MigrationInfo;

public class MigrationInfoDumper
{
    private static final String VERSION_TITLE = "Version";
    private static final String DESCRIPTION_TITLE = "Description";
    
    public static String dumpToAsciiTable(final MigrationInfo[] migrationInfos) {
        int versionWidth = "Version".length();
        int descriptionWidth = "Description".length();
        for (final MigrationInfo migrationInfo : migrationInfos) {
            versionWidth = Math.max(versionWidth, (migrationInfo.getVersion() == null) ? 0 : migrationInfo.getVersion().toString().length());
            descriptionWidth = Math.max(descriptionWidth, migrationInfo.getDescription().length());
        }
        final String ruler = "+-" + StringUtils.trimOrPad("", versionWidth, '-') + "-+-" + StringUtils.trimOrPad("", descriptionWidth, '-') + "-+---------------------+---------+\n";
        final StringBuilder table = new StringBuilder();
        table.append(ruler);
        table.append("| ").append(StringUtils.trimOrPad("Version", versionWidth, ' ')).append(" | ").append(StringUtils.trimOrPad("Description", descriptionWidth)).append(" | Installed on        | State   |\n");
        table.append(ruler);
        if (migrationInfos.length == 0) {
            table.append(StringUtils.trimOrPad("| No migrations found", ruler.length() - 2, ' ')).append("|\n");
        }
        else {
            for (final MigrationInfo migrationInfo2 : migrationInfos) {
                final String versionStr = (migrationInfo2.getVersion() == null) ? "" : migrationInfo2.getVersion().toString();
                table.append("| ").append(StringUtils.trimOrPad(versionStr, versionWidth));
                table.append(" | ").append(StringUtils.trimOrPad(migrationInfo2.getDescription(), descriptionWidth));
                table.append(" | ").append(StringUtils.trimOrPad(DateUtils.formatDateAsIsoString(migrationInfo2.getInstalledOn()), 19));
                table.append(" | ").append(StringUtils.trimOrPad(migrationInfo2.getState().getDisplayName(), 7));
                table.append(" |\n");
            }
        }
        table.append(ruler);
        return table.toString();
    }
}
