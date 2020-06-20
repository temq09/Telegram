package org.telegram.messenger.upsidedown;

import org.telegram.messenger.upsidedown.data.PasswordData;

import java.util.List;

public interface DoubleBottom {

    boolean isEnabled();

    boolean isMasterSession();

    List<PasswordData> getPasswords();

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
