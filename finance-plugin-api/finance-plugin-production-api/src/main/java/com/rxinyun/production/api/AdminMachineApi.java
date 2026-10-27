package com.rxinyun.production.api;

import com.rxinyun.production.core.pojo.BaseAdminMachine;

public interface AdminMachineApi {
   BaseAdminMachine findMachineById(String id);
}
