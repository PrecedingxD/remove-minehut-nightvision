package me.preceding.nightvision.manager;

public class MinehutManager {

    private boolean isOnMinehutLobby;

    public void handleTabUpdate(String header, String footer) {
        this.isOnMinehutLobby = header.contains("MINEHUT") && footer.contains("Create your Free Minecraft Server today!");
    }

    public boolean isOnMinehutLobby() {
        return isOnMinehutLobby;
    }

}
