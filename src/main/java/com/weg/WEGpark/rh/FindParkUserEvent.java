package com.weg.WEGpark.rh;

import com.weg.WEGpark.rh.shared.filter.FindUserFilter;

public record FindParkUserEvent(
        
        FindUserFilter filter
) {
}
