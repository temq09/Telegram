package org.telegram.messenger.upsidedown;

import org.telegram.messenger.upsidedown.data.PasswordData;

import java.util.Arrays;
import java.util.List;

public class DoubleBottomImpl implements DoubleBottom {


    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isMasterSession() {
        return true;
    }

    @Override
    public List<PasswordData> getPasswords() {
        return Arrays.asList(
                new PasswordData("id_1"),
                new PasswordData("id_2"),
                new PasswordData("id_3")
        );
    }
}
