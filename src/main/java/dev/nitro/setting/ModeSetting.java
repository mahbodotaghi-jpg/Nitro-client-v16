package dev.nitro.setting;

public class ModeSetting extends Setting<String> {
    private String[] modes;
    private int idx;

    public ModeSetting(String name, String... modes) {
        super(name, modes[0]);
        this.modes = modes;
    }

    public void cycle() {
        idx = (idx + 1) % modes.length;
        value = modes[idx];
    }

    /** Replace the option list (keeps the current value if it still exists). */
    public void setModes(String... newModes) {
        String cur = value;
        modes = newModes;
        idx = 0;
        for (int i = 0; i < modes.length; i++) if (modes[i].equals(cur)) idx = i;
        value = modes[idx];
    }
}
