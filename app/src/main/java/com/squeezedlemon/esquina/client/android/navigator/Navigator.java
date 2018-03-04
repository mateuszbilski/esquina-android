package com.squeezedlemon.esquina.client.android.navigator;


import android.app.Fragment;
import android.os.Bundle;

public interface Navigator {
    void navigate(Fragment sender, NavigatorEnum nav, Bundle bundle);
}
