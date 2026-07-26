package com.weg.WEGpark.park.internal.app.occurrence.dto;

import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;

import java.util.List;

public record RegisterDefaultInfo(

        List<VehicleUser> vehicleUsers,

        Guard guard
) {
}
