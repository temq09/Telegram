package org.telegram.messenger.upsidedown;

public class DoubleBottomImpl implements DoubleBottom {


    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isMasterSession() {
        return true;
    }
}
