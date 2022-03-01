package dev.cuican.staypro.mixin.client;

public interface IPlayerControllerMP {
    /**
     * Used by MixinPlayerControllerMP,
     * invokes syncCurrentPlayItem.
     */
    void syncItem();

    /**
     * Returns the currentPlayerItem
     * to find out if items are synced.
     *
     * @return currentPlayerItem.
     */
    int getItem();

    int currentPlayerItem();
}
