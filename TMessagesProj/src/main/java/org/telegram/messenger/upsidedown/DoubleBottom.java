package org.telegram.messenger.upsidedown;

public interface DoubleBottom {

    boolean isEnabled();

    boolean isMasterSession();

    class Factory {

        private static DoubleBottom INSTANCE;

        public static DoubleBottom get() {
            if (INSTANCE == null) {
                INSTANCE = new DoubleBottomImpl();
            }
            return INSTANCE;
        }

    }
}
