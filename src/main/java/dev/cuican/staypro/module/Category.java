
package dev.cuican.staypro.module;

public enum Category {
    COMBAT("Combat", true, false,"b"),
    MISC("Misc", true, false,"7"),
    MOVEMENT("Movement", true, false,"e"),
    PLAYER("Player", true, false,"c"),
    RENDER("Render", true, false,"a"),
    CLIENT("Client", true, false,"d"),
    HUD("HUD", true, true,"D"),
    HIDDEN("Hidden", false, false,"8");



    public String categoryName;
    public boolean visible;
    public boolean isHUD;
    public String isIoce;

    private Category(String categoryName, boolean visible, boolean isHUD,String isIoce) {
        this.categoryName = categoryName;
        this.visible = visible;
        this.isHUD = isHUD;
        this.isIoce = isIoce;
    }
}
