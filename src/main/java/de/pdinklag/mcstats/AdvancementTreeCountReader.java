package de.pdinklag.mcstats;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class AdvancementTreeCountReader extends NestedDataReader {
    private final Pattern[] patterns;

    public AdvancementTreeCountReader(String[] path, String[] patterns) {
        super(path);
        this.patterns = new Pattern[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            this.patterns[i] = Pattern.compile(patterns[i]);
        }
    }

    @Override
    protected DataValue getDefaultValue() {
        return new IntValue(0);
    }

    @Override
    protected DataValue read(JSONObject obj, String key) {
        JSONObject advancements = obj.optJSONObject(key);
        if (advancements == null) {
            return getDefaultValue();
        }

        int count = 0;

        for (String advancementId : advancements.keySet()) {
            boolean matches = false;

            for (Pattern pattern : patterns) {
                if (pattern.matcher(advancementId).matches()) {
                    matches = true;
                    break;
                }
            }

            if (!matches) {
                continue;
            }

            JSONObject advancement = advancements.optJSONObject(advancementId);
            if (advancement != null && advancement.optBoolean("done", false)) {
                count++;
            }
        }

        return new IntValue(count);
    }

    @Override
    public DataAggregator createDefaultAggregator() {
        return new IntSumAggregator();
    }
}