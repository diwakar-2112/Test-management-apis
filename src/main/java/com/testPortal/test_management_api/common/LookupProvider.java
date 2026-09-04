package com.testPortal.test_management_api.common;

import java.util.List;


public interface LookupProvider  {

    String getType();
    List<LookupItem> getDropdownItems();

}
